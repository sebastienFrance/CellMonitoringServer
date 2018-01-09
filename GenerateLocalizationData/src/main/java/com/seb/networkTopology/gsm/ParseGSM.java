package com.seb.networkTopology.gsm;

import java.io.FileReader;
import java.sql.Connection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkTopology.generic.Adjacency;
import com.seb.networkTopology.generic.ParseTechnoItf;
import com.seb.utilities.Technology;

public class ParseGSM implements ParseTechnoItf {
	private static final Logger LOG = LogManager.getLogger(ParseGSM.class);

	private String _GSMSnapshotName;
	private String _GSMSnapshotAdj;
	private double _deltaLat = 0.0;
	private double _deltaLong = 0.0;
	private String _prefix = null;
	
	private String _EqpId;
	private String _Model;
	private String _Release;

	private int _cellCount = 0;
	private int _neighborCount = 0;

	final int INDEX_CELL_ID = 1;
	final int INDEX_AZIMUTH = 157;
	final int INDEX_BSS_RELEASE = 160;
	final int INDEX_BSS_ID = 162;
	final int INDEX_GLOBAL_CELL_ID = 175;
	final int INDEX_GEO_COORD = 237;
	final int INDEX_USER_LABEL = 376;

	
	
	private Map<String, ArrayList<String>> _neighborList;
	
	private Map<String, String> _cellMappingToGlobalCellId;
	
	
	public ParseGSM(String GSMSnapshotName, String GSMSnapshotAdj) {
		_GSMSnapshotName = GSMSnapshotName;
		_GSMSnapshotAdj = GSMSnapshotAdj;
		_neighborList = new HashMap<String, ArrayList<String>>();
	}
	
	@Override
	public Technology getTechnology() {
		return Technology.GSM;
	}
	
	@Override
	public int getCellCount() {
		return _cellCount;
	}
	
	@Override
	public int getNeighborCount() {
		return _neighborCount;
	}

	@Override
	public void setDeltaCoord(double latitude, double longitude) {
		_deltaLat = latitude;
		_deltaLong = longitude;
	}
	
	@Override
	public void setPrefix(String prefix) {
		_prefix = prefix;
	}
	
	
	// Warning: nothing is saved in database !
	@Override
	public void parse(Connection theConnection) {
		LOG.info("parse::Start parsing GSM");

		try (CSVParser reader = CSVFormat.DEFAULT.withDelimiter(';').parse(new FileReader(_GSMSnapshotName))) {

			prepareNeighbors();
			parseNeighbors();

			extractBTSEquipment();
			int lineCount = 0;

			for (CSVRecord nextLine : reader) {
				// nextLine[] is an array of values from the line
				lineCount++;

				if (lineCount > 2) {
					String cellIdentifier = nextLine.get(INDEX_CELL_ID);
					String azimuth = nextLine.get(INDEX_AZIMUTH);
					//String BSSRelease = nextLine[INDEX_BSS_RELEASE];
					//String BSSId = nextLine[INDEX_BSS_ID];
					String cellGlobalId = nextLine.get(INDEX_GLOBAL_CELL_ID);
					String geoCoord = nextLine.get(INDEX_GEO_COORD);
					String cellUserLabel = nextLine.get(INDEX_USER_LABEL);

					// { applicationID "A1353RA_85287ee0", cellRef 92}
					cellIdentifier = matchPatern("(application.*?)}", cellIdentifier);

					//{ lai { mcc 'f540'H, mnc '01'H, lac 778}, ci 7}
					String cellMCC = matchPatern("mcc \\'f(.*?)\\'", cellGlobalId);
					String cellMNC = matchPatern("mnc \\'(.*?)\\'", cellGlobalId);
					String cellLAC = matchPatern("lac (.*?)}", cellGlobalId);
					String cellCI = matchPatern("ci (.*?)}", cellGlobalId);

					String cellId = cellMCC + "/" + cellMNC + "/" + cellLAC + "/" + cellCI;

					// { longitude 0.000000, latitude 0.000000, significant FALSE}
					String cellLongitude = matchPatern("longitude (.*?),", geoCoord);
					String cellLatitude = matchPatern("latitude (.*?),", geoCoord);
					String cellSignificant = matchPatern("significant (.*?)}", geoCoord);
					// write data in file
//					StringBuffer cellDescription = new StringBuffer();
//					TopologyHelper.appendCellElement(cellDescription, cellId);
//
//
//					TopologyHelper.cellAppendLatitude(cellDescription, cellLatitude, _deltaLat);
//					TopologyHelper.cellAppendLongitude(cellDescription, cellLongitude, _deltaLong);
//					TopologyHelper.cellAppendAzimuth(cellDescription, azimuth);
//					//					TopologyHelper.cellAppendAdminState(cellDescription, "unlocked");
//					//					TopologyHelper.cellAppendAvailStatus(cellDescription, "active");
//					//					TopologyHelper.cellAppendOperationalState(cellDescription, "enabled");
//					TopologyHelper.cellAppendSite(cellDescription, _EqpId);
//					TopologyHelper.cellAppendRelease(cellDescription, _Release);
//					TopologyHelper.cellAppendDlFrequency(cellDescription, "0");
//					TopologyHelper.cellAppendTelecomId(cellDescription, cellId);

					if (_prefix != null) {
						StringBuffer buff = new StringBuffer();
						buff.append(_prefix);
						buff.append(cellIdentifier);
						cellIdentifier = buff.toString();
					}		        

					ArrayList<String> targetCells = _neighborList.get(cellId);
					if (targetCells != null) {
						for (String target : targetCells) {
							Adjacency newAdjacency = new Adjacency(target, "false", "false", "false", "GSM");
//							TopologyHelper.appendAdjacency(cellDescription, newAdjacency);
						}
					} 
//					TopologyHelper.appendCellEndElement(cellDescription);
				}

			}

//			StringBuffer endZoneHeader = new StringBuffer();
//			TopologyHelper.appendEndZoneElement(endZoneHeader);


		}
		catch (Exception e) {
			LOG.error(e);
		}
		LOG.info("parse::End of parsing GSM");
	}
	
	private static String matchPatern(String pattern, String sourceString) {
		Pattern findLac = Pattern.compile(pattern);
		Matcher matcherLac = findLac.matcher(sourceString);
		if (matcherLac.find()) {
			 return matcherLac.group(1);
		} else {
			return null;
		}
	}
	
	// build a mapping table for each cell from the from { applicationID "A1353RA_85287ee0", cellRef 92} to MCC/MNC/LAC/CI
	public void prepareNeighbors() {
		LOG.info("prepareNeighbors::Start parsing GSM");
		try (CSVParser reader = CSVFormat.DEFAULT.withDelimiter(';').parse(new FileReader(_GSMSnapshotName))) {
			//String [] nextLine;
			int lineCount = 0;
			
			_cellMappingToGlobalCellId = new HashMap<String, String>();
			
			for (CSVRecord nextLine : reader) {
				lineCount++;

				if (lineCount > 2) {
					String cellIdentifier = nextLine.get(INDEX_CELL_ID);
					String cellGlobalId = nextLine.get(INDEX_GLOBAL_CELL_ID);
					
					// { applicationID "A1353RA_85287ee0", cellRef 92}
					cellIdentifier = matchPatern("(application.*?)}", cellIdentifier);
					//{ lai { mcc 'f540'H, mnc '01'H, lac 778}, ci 7}
					String cellMCC = matchPatern("mcc \\'f(.*?)\\'",cellGlobalId);
					String cellMNC = matchPatern("mnc \\'(.*?)\\'",cellGlobalId);
					String cellLAC = matchPatern("lac (.*?)}",cellGlobalId);
					String cellCI = matchPatern("ci (.*?)}",cellGlobalId);
					
					String cellId = cellMCC + "/" + cellMNC + "/" + cellLAC + "/" + cellCI;

		        	if (_prefix != null) {
		        		StringBuilder buff = new StringBuilder();
		        		buff.append(_prefix);
		        		buff.append(cellIdentifier);
		        		cellIdentifier = buff.toString();
		        		
		        		buff = new StringBuilder();
		        		buff.append(_prefix);
		        		buff.append(cellId);
		        		cellId = buff.toString();
		        	}		        

					
					_cellMappingToGlobalCellId.put(cellIdentifier, cellId);
				}
			}
		}
		catch (Exception e) {
			LOG.error(e);
		}
		LOG.info("prepareNeighbors::End of parsing GSM preparation");
	}
	

	
	public void parseNeighbors() {
		final int INDEX_NEIGHBOR_ID = 1;
		LOG.info("parseNeighbors::Start parsing GSM Adj");

		try (CSVParser reader = CSVFormat.DEFAULT.withDelimiter(';').parse(new FileReader(_GSMSnapshotAdj))) {
			int lineCount = 0;


			for (CSVRecord nextLine : reader) {
				// nextLine[] is an array of values from the line
				lineCount++;

				if (lineCount > 2) {
					String neighborIdentifier = nextLine.get(INDEX_NEIGHBOR_ID);

					String sourceCell = null;
					String targetCell = null;
					//{ cell { applicationID "A1353RA_85287ee0", cellRef 24}, targetCell { applicationID "A1353RA_85287ee0", cellRef 5}}
					Pattern findSource = Pattern.compile("(applicationID .*?)}.*?(applicationID .*?)}");
					Matcher matcherSource = findSource.matcher(neighborIdentifier);
					if (matcherSource.find()) {
						sourceCell = matcherSource.group(1);
						targetCell = matcherSource.group(2);

			        	if (_prefix != null) {
			        		StringBuilder buff = new StringBuilder();
			        		buff.append(_prefix);
			        		buff.append(sourceCell);
			        		sourceCell = buff.toString();
			        		
			        		buff = new StringBuilder();
			        		buff.append(_prefix);
			        		buff.append(targetCell);
			        		targetCell = buff.toString();
			        	}
						
						addNeighbor(sourceCell, targetCell);
					}
					
					
				}
			}
		}
		catch (Exception e) {
			LOG.error(e);
		}
		LOG.info("parseNeighbors::End of parsing GSM");

	}
	
	private void addNeighbor(String sourceCell, String targetCell) {
		
		// convert Axxx to MCC/MNC/LAC/CI for source and target
		String globalSourceCellId = _cellMappingToGlobalCellId.get(sourceCell);
		String globalTargetCellId = _cellMappingToGlobalCellId.get(targetCell);

		if ((globalTargetCellId == null)) {
			LOG.warn("addNeighbor::Ignore neighor because unknown target: " + targetCell);
			return;			
		}
		if ((globalSourceCellId == null)) {
			LOG.warn("addNeighbor::Ignore neighor because unknown source: " + sourceCell);
			return;
		}
		
		
		ArrayList<String> cellTargets = _neighborList.get(globalSourceCellId);
		if (cellTargets == null) {
			cellTargets = new ArrayList<String>();
			_neighborList.put(globalSourceCellId, cellTargets);
		}
		cellTargets.add(globalTargetCellId);
	}
	
	private void extractBTSEquipment() {
    	_EqpId = "BTSX";
    	_Model = "BTS";
    	_Release = "B12";

	}

	
}
