package com.seb.networkTopology.lte;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.datasources.DatasourceTopology;
import com.seb.networkGenerator.generic.AttrNameValue;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkTopology.generateLocalizationProperties;
import com.seb.networkTopology.generic.AbstractXMLTopologyParser;
import com.seb.networkTopology.generic.Adjacency;
import com.seb.networkTopology.generic.Anonymous;
import com.seb.networkTopology.generic.AntennaPort;
import com.seb.networkTopology.generic.XMLAttributesParser;
import com.seb.utilities.Technology;

public class SimpleLTEParser extends AbstractXMLTopologyParser {
	private static final Logger LOG = LogManager.getLogger(SimpleLTEParser.class);

	private static  final String ElementENBEquipment                = "ENBEquipment";
	private static  final String ElementLteCell                     = "LteCell";
	private static  final String ElementCpriRadioEquipment          = "CpriRadioEquipment";
	private static  final String ElementAntennaPort                 = "AntennaPort"; 
	private static  final String ElementLteNeighboringCellRelation  = "LteNeighboringCellRelation";
	private static  final String ElementUTRANeighboringCellRelation = "UtraFddNeighboringCellRelation";
	
	
	// eNB attributes and paths
	
	private static final int ENBEQUIPMENT_ATTR_ENB_EQPID = 0;
	private static final int ENBEQUIPMENT_ATTR_ENB_MODEL = 1;
	private static final int ENBEQUIPMENT_ATTR_ENB_RELEASE = 2;
	
	static private final String PATH_ENB_ATTR_MACROENBID = "snapshot.ENBEquipment.Enb.attributes.macroEnbId";
	static Set<String> eNBPathAttributes = new HashSet<String>()  {{
	    this.add(PATH_ENB_ATTR_MACROENBID);
	}};
	
	
	// AntennaPort attributes and paths
	private static final String PATH_ANTENNAPORT_ATTR_AZIMUTH = "snapshot.ENBEquipment.CpriRadioEquipment.AntennaPort.attributes.azimuth";
	static Set<String> antennaPortPathAttributes = new HashSet<String>()  {{
	    this.add(PATH_ANTENNAPORT_ATTR_AZIMUTH);
	}};
	
	
	// Cell attributes and paths
	
	private static final String PATH_CELL_ATTR_LATITUDE       = "snapshot.ENBEquipment.Enb.LteCell.attributes.lteCellPositionLatitude";
	private static final String PATH_CELL_ATTR_LONGITUDE      = "snapshot.ENBEquipment.Enb.LteCell.attributes.lteCellPositionLongitude";
	private static final String PATH_CELL_ATTR_ANTENNAPORTID  = "snapshot.ENBEquipment.Enb.LteCell.CellAntennaPort.attributes.antennaPortId";
	private static final String PATH_CELL_ATTR_DLEARFCN       = "snapshot.ENBEquipment.Enb.LteCell.FrequencyAndBandwidthFDD.attributes.dlEARFCN";
	private static final String PATH_CELL_ATTR_RELATIVECELLID = "snapshot.ENBEquipment.Enb.LteCell.attributes.relativeCellIdentity";
	private static final String PATH_CELL_ATTR_RADIUS         = "snapshot.ENBEquipment.Enb.LteCell.attributes.cellRadius";
	
	// Neighbors attributes and paths
	private static final String PATH_NR_FREQ_ATTR_DLEARFCN = "snapshot.ENBEquipment.Enb.LteCell.LteNeighboring.LteNeighboringFreqConf.attributes.dlEARFCN";

	private static final String PATH_UTRAN_NR_FREQ_CARRIER_FREQ = "snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf.attributes.carrierFreq";

	static Set<String> cellPathAttributes = new HashSet<String>()  {{
	    this.add(PATH_CELL_ATTR_LATITUDE);
	    this.add(PATH_CELL_ATTR_LONGITUDE);
	    this.add(PATH_CELL_ATTR_ANTENNAPORTID);
	    this.add(PATH_CELL_ATTR_DLEARFCN);
	    this.add(PATH_CELL_ATTR_RELATIVECELLID);
	    this.add(PATH_CELL_ATTR_RADIUS);
	    this.add(PATH_NR_FREQ_ATTR_DLEARFCN);
	    this.add(PATH_UTRAN_NR_FREQ_CARRIER_FREQ);
	}};
	
	// Neighbors attributes and paths

	private static final String PATH_NR_ATTR_MACRO_ENB_ID         = "snapshot.ENBEquipment.Enb.LteCell.LteNeighboring.LteNeighboringFreqConf.LteNeighboringCellRelation.attributes.macroEnbId";
	private static final String PATH_NR_ATTR_RELATIVE_CELL_ID     = "snapshot.ENBEquipment.Enb.LteCell.LteNeighboring.LteNeighboringFreqConf.LteNeighboringCellRelation.attributes.relativeCellIdentity";
	private static final String PATH_NR_ATTR_NO_HO_OR_RESELECTION = "snapshot.ENBEquipment.Enb.LteCell.LteNeighboring.LteNeighboringFreqConf.LteNeighboringCellRelation.attributes.noHoOrReselection";
	private static final String PATH_NR_ATTR_NO_REMOVE            = "snapshot.ENBEquipment.Enb.LteCell.LteNeighboring.LteNeighboringFreqConf.LteNeighboringCellRelation.attributes.noRemove";
	private static final String PATH_NR_ATTR_MEASURED_BY_ANR      = "snapshot.ENBEquipment.Enb.LteCell.LteNeighboring.LteNeighboringFreqConf.LteNeighboringCellRelation.attributes.measuredByAnr";
	static Set<String> neighborPathAttributes = new HashSet<String>()  {{
	    this.add(PATH_NR_ATTR_MACRO_ENB_ID);
	    this.add(PATH_NR_ATTR_RELATIVE_CELL_ID);
	    this.add(PATH_NR_ATTR_NO_HO_OR_RESELECTION);
	    this.add(PATH_NR_ATTR_NO_REMOVE);
	    this.add(PATH_NR_ATTR_MEASURED_BY_ANR);
	}};


	// UTRAN Neighbors attributes and paths

	private static final String PATH_UTRAN_NR_ATTR_CID                  = "snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf.UtraFddNeighboringCellRelation.attributes.cId";
	private static final String PATH_UTRAN_NR_ATTR_RNC_ACCESS_ID        = "snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf.UtraFddNeighboringCellRelation.attributes.rncAccessId";
	private static final String PATH_UTRAN_NR_ATTR_NO_HO_OR_REDIRECTION = "snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf.UtraFddNeighboringCellRelation.attributes.noHoOrRedirection";
	private static final String PATH_UTRAN_NR_ATTR_NO_REMOVE            = "snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf.UtraFddNeighboringCellRelation.attributes.noRemove";
	private static final String PATH_UTRAN_NR_ATTR_MEASURED_BY_ANR      = "snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf.UtraFddNeighboringCellRelation.attributes.measuredByAnr";
	static Set<String> UTRANeighborPathAttributes = new HashSet<String>()  {{
	    this.add(PATH_UTRAN_NR_ATTR_CID);
	    this.add(PATH_UTRAN_NR_ATTR_RNC_ACCESS_ID);
	    this.add(PATH_UTRAN_NR_ATTR_NO_HO_OR_REDIRECTION);
	    this.add(PATH_UTRAN_NR_ATTR_NO_REMOVE);
	    this.add(PATH_UTRAN_NR_ATTR_MEASURED_BY_ANR);
	}};

	// eNB data
	private List<AttrNameValue> _eNBEqpParameters;
	private String _eNBEqpId;
	private String _eNBRelease;
	private String _macroENBId;
	
	// CpriEquipment
	private String _currentCpriRadioEquipmentId = null;
	
	// antennaPort data
	private List<AntennaPort> _antennaPortList;
	private AntennaPort _currentAntennaPort;
	
	private List<Cell> _eNBCellList;

	private String  _currentPath = null;
	
	// Cell data
	private Cell _currentCell;
	private List<Adjacency> _currentAdjList; 
	
	// Neighbors data
	private Adjacency _currentAdjacency;
	private String _targetmacroENBId = null;
	private String _targetRelativeCellIdentity = null;
	private String _neighborDLFrequency;

	// UTRAN Neighbors data
	private String _UTRANeighborCarrierFreq;
	private String _targetCid = null;
	private String _targetRncAccessId = null;


	private Set<String> _listOfENBParameters;
	private Set<String> _listOfCellParameters;
	private Map<String, String> _attributesToSection;
	
	public SimpleLTEParser(String LTESnapshotName, XMLInputFactory factory, DatasourceTopology DSTopology) {
		super(Technology.LTE, LTESnapshotName, factory, DSTopology);
	}
	
	
	/**
	 * 
	 * Read generic LTE parameters and they are added to the list of eNB or cell parameters
	 * to be matched
	 * 
	 */
	@Override
	protected void initializationBeforeParsing() {		
		String attributesFileName = generateLocalizationProperties.getInstance().getLTEAttributeFileName();
		XMLAttributesParser parser = new XMLAttributesParser();
		_attributesToSection = parser.parse(attributesFileName);

		_listOfENBParameters = new HashSet<String>();
		_listOfCellParameters = new HashSet<String>();
		for (String currentAttrName : _attributesToSection.keySet()) {
			if (currentAttrName.startsWith("snapshot.ENBEquipment.Enb.LteCell")) {
				_listOfCellParameters.add(currentAttrName);
				
			} else {
				_listOfENBParameters.add(currentAttrName);
			}
		}
	}
	
	@Override
	protected String getTechnologyName() {
		return "LTE";
	}
	
	@Override
	protected String getReleaseName() {
		return Anonymous.getAnonymousLTERelease("LA5.0");
	}
	
	/**
	 * 
	 * Catch XML start Elements that are needed to build the Network Topology
	 * to be matched.
	 * Each new Element is appended to the _currentPath that is used to
	 * match which paramters must be exported in the topology
	 * 
	 * @param eventReader the XML reader
	 */
	@Override
	protected void manageStartElement(XMLStreamReader eventReader) {
		String elementName = eventReader.getLocalName();
		if (_currentPath == null) {
			_currentPath = new String(elementName);
		} else {
			_currentPath = new String(_currentPath + "." + elementName);
		}

		try {
			boolean readNextEvent = true;
			// Manage eNB level
			if (elementName.endsWith(ElementENBEquipment)) {
				initializaEnb(eventReader);        	
			} else if (_listOfENBParameters.contains(_currentPath) || eNBPathAttributes.contains(_currentPath)) {
				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addEnbAttribute(_currentPath, paramValue, _attributesToSection.get(_currentPath));
				} 
				
				readNextEvent = false;
			// Manage cell level
			} else if (elementName.endsWith(ElementCpriRadioEquipment)) {
				initializeNewCpriRadioEquipment(eventReader.getAttributeValue(0));
			} else if (elementName.equals(ElementAntennaPort)) {
				initializeNewAntennaPort(eventReader.getAttributeValue(0));
			} else if (antennaPortPathAttributes.contains(_currentPath)) {

				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addAntennaPortAttribute(_currentPath, paramValue, _attributesToSection.get(_currentPath));			
				} 
				
				readNextEvent = false;
			// Manage LTE Neighbor level		
			} else if (elementName.endsWith(ElementLteCell)) {
				initializeNewCell(eventReader.getAttributeValue(0));
			} else if (_listOfCellParameters.contains(_currentPath) || cellPathAttributes.contains(_currentPath)) {

				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addCellAttribute(_currentPath, paramValue, _attributesToSection.get(_currentPath));			
				} 
				
				readNextEvent = false;
			// Manage LTE Neighbor level		
			} else if (elementName.equals(ElementLteNeighboringCellRelation)) {
				initializeNewAdjacency();
			} else	if (neighborPathAttributes.contains(_currentPath)) { 
				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addAdjacencyAttributes(_currentPath, paramValue);			
				} 
				
				readNextEvent = false;
			// Manage UTRAN Neighbor level
			} else if (elementName.equals(ElementUTRANeighboringCellRelation)) {
				initializeNewUTRANAdjacency();
			} else	if (UTRANeighborPathAttributes.contains(_currentPath)) { 
				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addUTRANAdjacencyAttributes(_currentPath, paramValue);
				} 
				
				readNextEvent = false;
			}
			
			if (readNextEvent == true) {
				eventReader.next();
			}
		}
		catch (Exception ex) {
			LOG.fatal(ex);;
			System.exit(1);
		}
		
	}
	
	/**
	 * 
	 * Catch XML end Elements that are needed to build the Network Topology
	 * to be matched.
	 * Each end Element is removed from the _currentPath 
	 * 
	 * @param eventReader the XML reader
	 * @param out Buffer used to generate the XML Topology
	 */
	@Override
	protected void manageEndElement(XMLStreamReader eventReader, Connection theConnection) {
        String elementName = eventReader.getLocalName();

        int endIndex = _currentPath.lastIndexOf(".");
        if (endIndex == -1) {
        	LOG.warn("manageEndElement::Warning, no last RDN in path: " + _currentPath);
        	
        } else {
        	_currentPath = new String(_currentPath.substring(0, endIndex));
        } 
        
        if (elementName.endsWith(ElementENBEquipment)) {
        	finalizeEnb(theConnection);
        } else if (elementName.endsWith(ElementAntennaPort)) {
     		finishAntennaPortInitialization();
        } else if (elementName.endsWith(ElementLteCell)) {
     		finishCellIinitialization();
        } else if (elementName.endsWith(ElementLteNeighboringCellRelation)) {
        	finishAdjacencyInitialization();
        } 
        else if (elementName.endsWith(ElementUTRANeighboringCellRelation)) {
        	finishUTRANAdjacencyInitialization();
       }
	}
	
	/**
	 * Extract parameters from the eNB and create internal list to record generic eNB parameters
	 * and list of cells
	 * 
	 * @param eventReader XML stream to read the parameters
	 */
	private void initializaEnb(XMLStreamReader eventReader) {
    	_eNBEqpId = eventReader.getAttributeValue(ENBEQUIPMENT_ATTR_ENB_EQPID);
    	_eNBRelease = eventReader.getAttributeValue(ENBEQUIPMENT_ATTR_ENB_RELEASE);

    	_eNBEqpParameters = new ArrayList<AttrNameValue>();
		_eNBCellList = new ArrayList<Cell>();
		_antennaPortList = new ArrayList<AntennaPort>();
	}

	/**
	 * Configure the eNB parameters and record generic parameters at eNB level
	 * 
	 * @param Name of the LTE eNB parameter (full name with path like snapshot.ENBEquipment.Enb...
	 * @param Value of the parameter
	 * @param sectionName used only to classify generic parameters
	 */
	private void addEnbAttribute(String attrName, String attrValue, String sectionName) {
		if (attrName != null && attrValue != null) {
			
			if (attrName.equals(PATH_ENB_ATTR_MACROENBID)) {
				_macroENBId = attrValue;
			} else {
				String theAttrName = attrName.substring(attrName.lastIndexOf(".") + 1);
				AttrNameValue newAttr = new AttrNameValue(theAttrName, attrValue, sectionName);
				_eNBEqpParameters.add(newAttr);
			}
		}
	}
	
	/**
	 * Add to the cell parameters from the eNB and generate it in XML format
	 * 
	 * @param out buffer to generate the XML
	 */
	private void finalizeEnb(Connection theConnection) {
		
    	for (Cell currentCell : _eNBCellList) {
    		currentCell.addParamValueList(_eNBEqpParameters);
    		currentCell.generateInDatabase(theConnection);
    	}
    	_eNBEqpParameters = null;
    	_eNBCellList = null;
	}
	
	private void initializeNewCpriRadioEquipment(String cpriRadioEquipmentId) {

		_currentCpriRadioEquipmentId = cpriRadioEquipmentId;
	}

	private void initializeNewAntennaPort(String antennaPortId) {

		_currentAntennaPort = new AntennaPort(_eNBEqpId, _currentCpriRadioEquipmentId, antennaPortId);
	}

	private void finishAntennaPortInitialization() {

    	_antennaPortList.add(_currentAntennaPort);
	}
	
	private void addAntennaPortAttribute(String attrName, String attrValue, String sectionName) {
		if (attrName != null && attrValue != null) {
			if (attrName.equals(PATH_ANTENNAPORT_ATTR_AZIMUTH)) {
				_currentAntennaPort.setAzimuth(attrValue);
			}			
		}
	}

	/**
	 * Create a new cell with a list of Neighbors (LTE or UTRAN)
	 * 
	 * @param cellId name of the new cell
	 */
	private void initializeNewCell(String cellId) {
		if (_prefix != null) {
			StringBuilder buff = new StringBuilder();
			buff.append(_prefix);
			buff.append(cellId);
			cellId = buff.toString();
		}

		_currentCell = new Cell(Anonymous.getCellMapping(_eNBEqpId, cellId));
		_currentCell.setTechno("LTE");
		_currentCell.setQOSDatasource(_DSTopology.getParentDS().getId());
		_currentCell.setTopologyDatasource(_DSTopology.getId());
		_currentAdjList = new ArrayList<Adjacency>();
		
	}
	
	/**
	 * Add parameters to an LTE cell that are provided by a parent object. The LTE cell is also
	 * added to the list if cell of the current eNB 
	 */
	private void finishCellIinitialization() {
 		_currentCell.setSite(Anonymous.getSiteMapping(_eNBEqpId));
 		_currentCell.setRelease(Anonymous.getAnonymousLTERelease(_eNBRelease));
 		_currentCell.addNeighborList(_currentAdjList);
 		_currentAdjList = null;
     	_eNBCellList.add(_currentCell);

		_cellCount++;
	}
	
	/**
	 * Configure the LTE cell parameters
	 * 
	 * @param Name of the LTE Cell parameter (full name with path like snapshot.ENBEquipment.Enb.LteCell..
	 * @param Value of the parameter
	 * @param sectionName used only to classify generic parameters
	 */
	private void addCellAttribute(String attrName, String attrValue, String sectionName) {
		if (attrName != null && attrValue != null) {
			if (attrName.equals(PATH_CELL_ATTR_LATITUDE)) {
				_currentCell.setLatitude(attrValue);
				_currentCell.setDeltaLat(_deltaLat);
			} else if (attrName.equals(PATH_CELL_ATTR_LONGITUDE)) {
				_currentCell.setLongitude(attrValue);
				_currentCell.setDeltaLong(_deltaLong);
			} else if (attrName.equals(PATH_CELL_ATTR_ANTENNAPORTID)) {
				processAntennaPortForCellAzimuth(attrValue);
			} else if (attrName.equals(PATH_CELL_ATTR_DLEARFCN)) {
				_currentCell.setDLFrequency(Anonymous.getDLFrequencyMapping(attrValue));
			} else if (attrName.equals(PATH_CELL_ATTR_RELATIVECELLID)) {
				processRelativeCellIdForTelecomIdAndSiteId(attrValue);
			} else if (attrName.equals(PATH_CELL_ATTR_RADIUS)) {
				processRadius(attrValue);
			} else if (attrName.equals(PATH_NR_FREQ_ATTR_DLEARFCN)) {
				_neighborDLFrequency = attrValue;
			} else if (attrName.equals(PATH_UTRAN_NR_FREQ_CARRIER_FREQ)) {
				_UTRANeighborCarrierFreq = attrValue;
			} else {
				attrName = attrName.substring(attrName.lastIndexOf(".") + 1);
				_currentCell.addParamValue(attrName, attrValue, sectionName);
			}			
		}
	}
	
	private void processAntennaPortForCellAzimuth(String attrValue) {
		for (AntennaPort currentAntennaPort : _antennaPortList) {
			if (currentAntennaPort.getAntennaPortFDN().equals(attrValue)) {
				_currentCell.setAzimuth(currentAntennaPort.getAzimuth());
				break;
			}
		}
	}
	
	private void processRelativeCellIdForTelecomIdAndSiteId(String attrValue) {
		String theMacroENBId = _macroENBId;
		if (_prefix != null) {
			StringBuilder buff = new StringBuilder();
			buff.append(_prefix);
			buff.append(theMacroENBId);
			theMacroENBId = buff.toString();
		}
		_currentCell.setTelecomId(Anonymous.getTelecomIdMapping(theMacroENBId + "_" + attrValue));
		_currentCell.setSiteId(theMacroENBId);

	}
	
	private void processRadius(String attrValue) {
		int radius = (int) (Float.parseFloat(attrValue) * 1000);
		_currentCell.setRadius(radius);
	}
	
	private void initializeNewAdjacency() {
    	_currentAdjacency = new Adjacency();
	}
	
	/**
	 * Add parameters to an LTE relation that are provided by a parent object. The LTE relation is also
	 * added to the list if Adjacencies of the current cell 
	 */
	private void finishAdjacencyInitialization() {
		String targetName = _targetmacroENBId + "_" + _targetRelativeCellIdentity;
		if (_prefix != null) {
			targetName = _prefix + targetName; 
		}
    	_currentAdjacency.setTechnoTarget("LTE");
    	_currentAdjacency.setDlFrequency(Anonymous.getDLFrequencyMapping(_neighborDLFrequency));
		_currentAdjacency.setTargetCell(Anonymous.getTelecomIdMapping(targetName));
    	_currentAdjList.add(_currentAdjacency);
		_currentAdjacency = null;
		_targetmacroENBId = null;
		_targetRelativeCellIdentity = null;

		_neighborCount++;
	}
	
	/**
	 * Configure the LTE Neighbor relation parameters
	 * 
	 * @param Name of the LTE NR parameter (full name with path like snapshot.ENBEquipment.Enb.LteCell..
	 * @param Value of the parameter
	 */
	private void addAdjacencyAttributes(String attrName, String attrValue) {
		if (attrName != null && attrValue != null) {
			if (attrName.equals(PATH_NR_ATTR_NO_HO_OR_RESELECTION)) {
				_currentAdjacency.setNoHo(attrValue);
			} else if (attrName.equals(PATH_NR_ATTR_NO_REMOVE)) {
				_currentAdjacency.setNoRemove(attrValue);
			} else if (attrName.equals(PATH_NR_ATTR_MEASURED_BY_ANR)) {
				_currentAdjacency.setMeasuredByANR(attrValue);
			} else if (attrName.equals(PATH_NR_ATTR_MACRO_ENB_ID)) {
				_targetmacroENBId = attrValue;
			} else if (attrName.equals(PATH_NR_ATTR_RELATIVE_CELL_ID)) {
				_targetRelativeCellIdentity = attrValue;
			}
		}
	}
	
	private void initializeNewUTRANAdjacency() {
    	_currentAdjacency = new Adjacency();
	}
	
	/**
	 * Add parameters to an UTRAN relation that are provided by a parent object. The UTRAN relation is also
	 * added to the list if Adjacencies of the current cell 
	 */
	private void finishUTRANAdjacencyInitialization() {
		
		String targetName = _targetRncAccessId + "_" + _targetCid; 
		if (_prefix != null) {
			targetName = _prefix + targetName; 
		}
	   	_currentAdjacency.setTechnoTarget("WCDMA");
    	_currentAdjacency.setDlFrequency(Anonymous.getDLFrequencyMapping(_UTRANeighborCarrierFreq));
		_currentAdjacency.setTargetCell(Anonymous.getTelecomIdMapping(targetName));
    	_currentAdjList.add(_currentAdjacency);
		_currentAdjacency = null;
		_targetRncAccessId = null;
		_targetCid = null;
		
		_neighborCount++;
	}
	
	/**
	 * Configure the UTRAN Neighbor relation parameters
	 * 
	 * @param Name of the UTRAN parameter (full name with path like snapshot.ENBEquipment.Enb.LteCell.UtraNeighboring.UtraFddNeighboringFreqConf...
	 * @param Value of the parameter
	 */
	private void addUTRANAdjacencyAttributes(String attrName, String attrValue) {
		if (attrName != null && attrValue != null) {
			if (attrName.equals(PATH_UTRAN_NR_ATTR_NO_HO_OR_REDIRECTION)) {
				_currentAdjacency.setNoHo(attrValue);
			} else if (attrName.equals(PATH_UTRAN_NR_ATTR_NO_REMOVE)) {
				_currentAdjacency.setNoRemove(attrValue);
			} else if (attrName.equals(PATH_UTRAN_NR_ATTR_MEASURED_BY_ANR)) {
				_currentAdjacency.setMeasuredByANR(attrValue);
			} else if (attrName.equals(PATH_UTRAN_NR_ATTR_CID)) {
				_targetCid = attrValue;
			} else if (attrName.equals(PATH_UTRAN_NR_ATTR_RNC_ACCESS_ID)) {
				_targetRncAccessId = attrValue.substring(attrValue.lastIndexOf('/') + 1);
			}
		}
	}



}
