package com.seb.networkGenerator.CellGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.FakeDatasourceManager;
import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkGenerator.CartoRadio.CartoRadioGeoCoordinate;
import com.seb.networkGenerator.OpenCellIds.OpenCellIds;
import com.seb.networkGenerator.OpenCellIds.OpenCellIdsUtility;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkGenerator.generic.TopologyUtils;
import com.seb.networkTopology.generic.Utility;
import com.seb.topologyMgt.GeoIndex;
import com.seb.utilities.Technology;

public class OpenCellIdsCellGenerator extends CellGeneratorBasics {
	private static final Logger LOG = LogManager.getLogger(OpenCellIdsCellGenerator.class);

	private String _openCellIdsFileName;
	private int _openCellIdsFileVersion;
	private String _prefix = "US";

	public OpenCellIdsCellGenerator(String openCellIdsFileName, int openCellIdsFileVersion, List<String> listOfIdentifier, int initialCellId, int initialTelecomId, String prefix) {
		super(listOfIdentifier, initialCellId, initialTelecomId);
		_openCellIdsFileVersion = openCellIdsFileVersion;
		_openCellIdsFileName = openCellIdsFileName;
		_prefix = prefix;
	}
	

	@Override
	public boolean parse() {
		if (_openCellIdsFileName == null) {
			return false;
		}

		try(FileReader supportFile = new FileReader(_openCellIdsFileName);
				BufferedReader reader = new BufferedReader (supportFile)) {

			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				String value = currentLine.trim();
				String[] listOfValues = value.split(",");
				OpenCellIds currentOpenCellIds = new OpenCellIds(_openCellIdsFileVersion, listOfValues);

				CartoRadioGeoCoordinate currentCoordinate = new CartoRadioGeoCoordinate(currentOpenCellIds.getLatitude(), currentOpenCellIds.getLongitude());

				int cellPerSite = Utility.generateRandomInteger(1, _maxCellsPerSite);

				for (int i = 0 ; i < cellPerSite; i++) {
					allocateNewCellForOpenCellIds(currentCoordinate);
				}

			}
		} catch (Exception ex){
			LOG.error("parserOpenCellIds::Exception when reading file " + _openCellIdsFileName, ex);
			return false;
		}

		return true;
	}


	private void allocateNewCellForOpenCellIds(CartoRadioGeoCoordinate coordinate) {
		double longitude = 0, latitude = 0;
		try {
			NumberFormat format = NumberFormat.getInstance(Locale.US);
			Number number = format.parse(coordinate.getLongitude());
			longitude = number.doubleValue();
			number = format.parse(coordinate.getLatitude());
			latitude = number.doubleValue();

			if ((latitude > 90.0) || (latitude < -90.0)) {
				return;
			}

			if ((longitude > 180.0) || (longitude < -180.0)) {
				return;
			}
		}	
		catch (Exception ex) {
			LOG.error(ex);
			return;
		}
		allocateNewCellForOpenCellIds(latitude, longitude);
	}

	private String getOpenCellIdsNodeId() {
		int randomEntry = Utility.generateRandomInteger(0, _listOfIdentifier.size() -1);
		String nodeId = _listOfIdentifier.get(randomEntry);
		//_listOfOpenCellIdsIdentifier.remove(0);
		return _prefix + "_" + nodeId;
	}

	private void allocateNewCellForOpenCellIds(double latitude, double longitude) {

		String nodeId = getOpenCellIdsNodeId();
		Technology cellTechno = OpenCellIdsUtility.getCellTechno();
		String release;
		List<Cell> targetCellTable = null;

		switch (cellTechno) {
		case LTE: {
			release =  Utility.getRandomString(NetworkGeneratorProperties.getLTEReleases());
			targetCellTable = _LTECells;
			break;
		}
		case WCDMA: {
			release =  Utility.getRandomString(NetworkGeneratorProperties.getWCDMAReleases());
			targetCellTable = _WCDMACells;
			break;
		}
		default: {
			// Ignore unknown cell type 
			return;
		}
		}

		String cellName = Cell.createCellName(nodeId, _cellId);
		_cellId++;
		String telecomId = Cell.createTelecomId(nodeId, _telecomId);
		_telecomId++;

		// initialize cell properties
		Cell newCell = new Cell(cellName, nodeId, nodeId, latitude, longitude, cellTechno.toString(), telecomId);
		newCell.setDLFrequency("900Mhz");
		newCell.setAzimuth(Integer.toString(Utility.generateRandomInteger(0, 359)));
		newCell.setRadius(TopologyUtils.getRandomRadius());
		newCell.setRelease(release);
		
		int QOSId = FakeDatasourceManager.getInstance().getDatasource(cellTechno).getId();
		int topologyId = FakeDatasourceManager.getInstance().getDatasourceTopology(cellTechno).getId();

		newCell.setQOSDatasource(QOSId);
		newCell.setTopologyDatasource(topologyId);

		// Add generic attributes for the cell
		TopologyUtils.addCellAttributesFor(newCell, cellName);

		_allCells.add(newCell);
		targetCellTable.add(newCell);
	}




}
