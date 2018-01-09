package com.seb.networkTopology.wcdma;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.seb.networkTopology.generic.XMLAttributesParser;
import com.seb.utilities.Technology;

public class SimpleWCDMAParser extends AbstractXMLTopologyParser  {
	private static final Logger LOG = LogManager.getLogger(SimpleWCDMAParser.class);

	private static final String ELEMENT_RNC                         = "RNC";
	private static final String ELEMENT_NODEB                       = "NodeB";
	private static final String ELEMENT_FDDCELL                     = "FDDCell";
	private static final String ELEMENT_UMTS_FDD_NEIGBHBOURING_CELL = "UMTSFddNeighbouringCell";
	
	private static final String ROOT_PATH_RNC = "snapshot.RNC";
	private static final String ROOT_PATH_NODEB = "snapshot.RNC.NodeB";
	private static final String ROOT_PATH_FDD_CELL = "snapshot.RNC.NodeB.FDDCell";
	
	// Cell attributes and paths
	private static final String PATH_CELL_ATTR_DL_FREQUENCY_NUMBER   = "snapshot.RNC.NodeB.FDDCell.attributes.dlFrequencyNumber";
	private static final String PATH_CELL_ATTR_LATITUDE              = "snapshot.RNC.NodeB.FDDCell.App.attributes.latitude";
	private static final String PATH_CELL_ATTR_LONGITUDE             = "snapshot.RNC.NodeB.FDDCell.App.attributes.longitude";
//	private static final String PATH_CELL_ATTR_LATITUDE              = "snapshot.RNC.NodeB.FDDCell.GaiCoord.attributes.latitude";
//	private static final String PATH_CELL_ATTR_LONGITUDE             = "snapshot.RNC.NodeB.FDDCell.GaiCoord.attributes.longitude";
	private static final String PATH_CELL_ATTR_AZIMUTH_ANTENNA_ANGLE = "snapshot.RNC.NodeB.FDDCell.attributes.azimuthAntennaAngle";
	private static final String PATH_CELL_ATTR_RADIUS                = "snapshot.RNC.NodeB.FDDCell.attributes.minCellRadius";
	private static final String PATH_CELL_ATTR_CELL_ID               = "snapshot.RNC.NodeB.FDDCell.attributes.cellId";
	
	static Set<String> cellPathAttributes = new HashSet<String>()  {{
	    this.add(PATH_CELL_ATTR_DL_FREQUENCY_NUMBER);
	    this.add(PATH_CELL_ATTR_LATITUDE);
	    this.add(PATH_CELL_ATTR_LONGITUDE);
	    this.add(PATH_CELL_ATTR_AZIMUTH_ANTENNA_ANGLE);
	    this.add(PATH_CELL_ATTR_CELL_ID);
	    this.add(PATH_CELL_ATTR_RADIUS);
	}};
	
	// Global data
	private ArrayList<Cell> _allCells;
	
	
	// RNC data
	private String _rncId;
	private List<AttrNameValue> _RNCParameters;
	private List<Cell> _RNCCellList;
	
	// NodeB data
	private String _EqpId;
	private String _Release;
	private List<AttrNameValue> _nodeBParameters;
	private List<Cell> _eNBCellList;

	// Cell data
	private Cell _currentCell;
	private List<Adjacency> _currentAdjList; 

	// Neighbors data
	private Adjacency _currentAdjacency;
	private String _currentTargetCell;

	private String  _currentPath = null;

	private Set<String> _listOfRNCParameters;
	private Set<String> _listOfNodeBParameters;
	private Set<String> _listOfCellParameters;
	private Map<String, String> _attributesToSection;
	
	private Map<String, String> _cellIdToTelecomId;
	
	public SimpleWCDMAParser(String snapshotName, XMLInputFactory factory, DatasourceTopology DSTopology) {
		super(Technology.WCDMA, snapshotName, factory, DSTopology);
	}
	
	@Override
	protected String getTechnologyName() {
		return "WCDMA";
	}
	
	@Override
	protected String getReleaseName() {
		return Anonymous.getAnonymousWCDMARelease("UA7.0");
	}
	
	@Override
	protected void initializationBeforeParsing() { 
		String attributesFileName = generateLocalizationProperties.getInstance().getWCDMAAttributeFileName();
		XMLAttributesParser parser = new XMLAttributesParser();
		_attributesToSection = parser.parse(attributesFileName);
		_listOfRNCParameters = new HashSet<String>();
		_listOfNodeBParameters = new HashSet<String>();
		_listOfCellParameters = new HashSet<String>();
		for (String currentAttrName : _attributesToSection.keySet()) {
			if (currentAttrName.startsWith(ROOT_PATH_FDD_CELL)) {
				_listOfCellParameters.add(currentAttrName);
			} else if (currentAttrName.startsWith(ROOT_PATH_NODEB)) {
				_listOfNodeBParameters.add(currentAttrName);
			} else if (currentAttrName.startsWith(ROOT_PATH_RNC)) {
				_listOfRNCParameters.add(currentAttrName);				
			} else {
				LOG.warn("initializationBeforeParsing::WCDMA attribute is ignored: " + currentAttrName);
			}
		}
		
		_cellIdToTelecomId = new HashMap<String,String>();
		_allCells = new ArrayList<Cell>();
  
	}
	
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
			
			if (elementName.equals(ELEMENT_RNC)) {
				initializeRNC(eventReader);
			} else if (_listOfRNCParameters.contains(_currentPath)) {
				
				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addRNCAttribute(_currentPath, paramValue, _attributesToSection.get(_currentPath));			
				} 
				
				readNextEvent = false;
			} else if (elementName.equals(ELEMENT_NODEB)) {
				initializeNodeBEquipment(eventReader);
			} else if (_listOfNodeBParameters.contains(_currentPath)) {
				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addNodeBAttribute(_currentPath, paramValue, _attributesToSection.get(_currentPath));			
				} 
				
				readNextEvent = false;
	        } else if (elementName.endsWith(ELEMENT_FDDCELL)) {
				initializeNewCell(eventReader.getAttributeValue(0));
			} else if (_listOfCellParameters.contains(_currentPath) || cellPathAttributes.contains(_currentPath)) {

				String paramValue = getElementText(eventReader);
				if (paramValue != null) {
					addCellAttribute(_currentPath, paramValue, _attributesToSection.get(_currentPath));			
				} 
				
				readNextEvent = false;
			} else if (elementName.equals(ELEMENT_UMTS_FDD_NEIGBHBOURING_CELL)) {
				initializeNewAdjacency(eventReader);
			}
			
			if (readNextEvent == true) {
				eventReader.next();
			}
		}
		catch (Exception ex) {
			LOG.fatal(ex);
			System.exit(1);
		}
		
	}
	
	@Override
	protected void manageEndElement(XMLStreamReader eventReader, Connection theConnection) {
        String elementName = eventReader.getLocalName();

        int endIndex = _currentPath.lastIndexOf(".");
        if (endIndex == -1) {
        	LOG.warn("manageEndElement::Warning, no last RDN in path: " + _currentPath);
        	
        } else {
        	_currentPath = new String(_currentPath.substring(0, endIndex));
        } 
        
        if (elementName.equals(ELEMENT_RNC)) {
        	finalizeRNCEquipment(theConnection);
        } else if (elementName.equals(ELEMENT_NODEB)) {
        	finalizeNodeBEquipment();
        } else if (elementName.equals(ELEMENT_FDDCELL)) {
     		finishCellIinitialization();
        } else if (elementName.endsWith(ELEMENT_UMTS_FDD_NEIGBHBOURING_CELL)) {
        	finishAdjacencyInitialization();
        }
	}
	
	@Override
	protected void manageEndOfParsing(XMLStreamReader eventReader, Connection theConnection) {
		LOG.info("manageEndElement::Update TelecomId of 3G NRs");
		
		for (Cell currentCell : _allCells) {
			currentCell.updateTargetNeighbor(_cellIdToTelecomId);
			currentCell.generateInDatabase(theConnection);
		}
	
	}
	
	/**
	 * 
	 * @param eventReader points to the RNC Element
	 */
	private void initializeRNC(XMLStreamReader eventReader) {
		_rncId = eventReader.getAttributeValue(0);
		_RNCCellList = new ArrayList<Cell>();
		_RNCParameters = new ArrayList<AttrNameValue>();
	}

	private void addRNCAttribute(String attrName, String attrValue, String sectionName) {
		if (attrName != null && attrValue != null) {
			attrName = attrName.substring(attrName.lastIndexOf(".") + 1);
			AttrNameValue newAttr = new AttrNameValue(attrName, attrValue, sectionName);
			_RNCParameters.add(newAttr);
		}
	}

	/**
	 * @param out Writer to generate all cells
	 */
	private void finalizeRNCEquipment(Connection theConnection) {
		// Write cells and Neighbors relations
		for (Cell currentCell : _RNCCellList) {
			currentCell.addParamValueList(_RNCParameters);
		}
		
		_allCells.addAll(_RNCCellList);
		
		_RNCCellList = null;
	}

	private void initializeNodeBEquipment(XMLStreamReader eventReader) {
    	_EqpId = eventReader.getAttributeValue(0);
    	_Release = "UA7.0";
		_eNBCellList = new ArrayList<Cell>();
	   	_nodeBParameters = new ArrayList<AttrNameValue>();
	}
	
	private void addNodeBAttribute(String attrName, String attrValue, String sectionName) {
		if (attrName != null && attrValue != null) {

			attrName = attrName.substring(attrName.lastIndexOf(".") + 1);
			AttrNameValue newAttr = new AttrNameValue(attrName, attrValue, sectionName);
			_nodeBParameters.add(newAttr);
		}
	}


	
	private void finalizeNodeBEquipment() {
		// Write cells and Neighbors relations
    	for (Cell currentCell : _eNBCellList) {
    		currentCell.addParamValueList(_nodeBParameters);
    	}
    	_RNCCellList.addAll(_eNBCellList);
    	_eNBCellList = null;
	}
	
		
	private void initializeNewCell(String cellId) {
		if (_prefix != null) {
			StringBuilder buff = new StringBuilder();
			buff.append(_prefix);
			buff.append(cellId);
			cellId = buff.toString();
		}

		_currentCell = new Cell(Anonymous.getCellMapping("", cellId));
		_currentCell.setTechno("WCDMA");
		_currentCell.setQOSDatasource(_DSTopology.getParentDS().getId());
		_currentCell.setTopologyDatasource(_DSTopology.getId());
		_currentAdjList = new ArrayList<Adjacency>();
	}
	
	private void finishCellIinitialization() {
 		_currentCell.setSite(Anonymous.getSiteMapping(_EqpId));
		_currentCell.setRelease(Anonymous.getAnonymousWCDMARelease(_Release));
 		_currentCell.addNeighborList(_currentAdjList);
 		_currentAdjList = null;
     	_eNBCellList.add(_currentCell);
     	
     	
     	_cellCount++;
	}
	
	
	private void addCellAttribute(String attrName, String attrValue, String sectionName) {
		if (attrName != null && attrValue != null) {
			if (attrName.equals(PATH_CELL_ATTR_LATITUDE)) {
				processCellLatitude(attrValue);
			} else if (attrName.equals(PATH_CELL_ATTR_LONGITUDE)) {
				processCellLongitude(attrValue);
			} else if (attrName.equals(PATH_CELL_ATTR_AZIMUTH_ANTENNA_ANGLE)) {
				_currentCell.setAzimuth(attrValue);
			} else if (attrName.equals(PATH_CELL_ATTR_DL_FREQUENCY_NUMBER)) {
				_currentCell.setDLFrequency(Anonymous.getDLFrequencyMapping(attrValue));
			} else if (attrName.equals(PATH_CELL_ATTR_CELL_ID)) {
				processAttrCellIdForTelecomIdAndSiteId(attrValue);
			} else if (attrName.equals(PATH_CELL_ATTR_RADIUS)) {
				int radius = Integer.parseInt(attrValue);
				_currentCell.setRadius(radius);
			} else {
				String shortAttrName = attrName.substring(attrName.lastIndexOf(".") + 1);
				_currentCell.addParamValue(shortAttrName, attrValue, sectionName);
			}			
		}
	}
	
	private void processCellLatitude(String attrValue) {
		Double latitudeFloat = Double.valueOf(attrValue);
		latitudeFloat = (latitudeFloat / 8388608) * 90;
		_currentCell.setLatitude(latitudeFloat);
		_currentCell.setDeltaLat(_deltaLat);	
	}
	
	private void processCellLongitude(String attrValue) {
		Double longitudeFloat = Double.valueOf(attrValue);
		longitudeFloat = (longitudeFloat / 16777216) * 360;
		_currentCell.setLongitude(longitudeFloat);
		_currentCell.setDeltaLong(_deltaLong);
	}
	
	private void processAttrCellIdForTelecomIdAndSiteId(String attrValue) {
		processAttrCellIdForTelecomIdAndSiteId(attrValue);
    	String telecomId = null;
    	if (_prefix != null) {

    		StringBuilder buffTelecomId = new StringBuilder();
    		buffTelecomId.append(_prefix);
    		buffTelecomId.append(_rncId);
    		buffTelecomId.append("_");
    		buffTelecomId.append(attrValue);
    		telecomId = buffTelecomId.toString();
    	} else {
    		telecomId = _rncId + "_" + attrValue;
    	}
		_currentCell.setTelecomId(Anonymous.getTelecomIdMapping(telecomId));
		_cellIdToTelecomId.put(_currentCell.getCellName(), telecomId);
		
		_currentCell.setSiteId(_rncId);
	}
	
	private void initializeNewAdjacency(XMLStreamReader eventReader) {
		_currentTargetCell = eventReader.getAttributeValue(0);

		_currentAdjacency = new Adjacency();
	}
	
	private void finishAdjacencyInitialization() {
		String targetCell = _currentTargetCell; 
		if (_prefix != null) {
			targetCell = _prefix + _currentTargetCell;	
		}

    	_currentAdjacency.setTechnoTarget("WCDMA");
 		_currentAdjacency.setTargetCell(Anonymous.getCellMapping("", targetCell));
 		_currentAdjacency.setDlFrequency("0"); // To be completed to collect the right value???
 		_currentAdjacency.setMeasuredByANR("false");
    	_currentAdjList.add(_currentAdjacency);
		_currentAdjacency = null;
		
		_neighborCount++;
	}



}
