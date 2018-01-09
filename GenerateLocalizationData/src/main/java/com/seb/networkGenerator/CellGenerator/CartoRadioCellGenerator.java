package com.seb.networkGenerator.CellGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.FakeDatasourceManager;
import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkGenerator.CartoRadio.CartoRadioGeoCoordinate;
import com.seb.networkGenerator.CartoRadio.CartoRadioGeoCoords;
import com.seb.networkGenerator.CartoRadio.CartoRadioSupport;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkGenerator.generic.TopologyUtils;
import com.seb.networkTopology.generic.Utility;
import com.seb.topologyMgt.GeoIndex;
import com.seb.utilities.Technology;

public class CartoRadioCellGenerator extends CellGeneratorBasics {
	private static final Logger LOG = LogManager.getLogger(CartoRadioCellGenerator.class);

	private String _supportFileName;
	private String _geoCoordsFileName;

	public CartoRadioCellGenerator(String supportFileName, String geoCoordsFileName, List<String> listOfIdentifier, int initialCellId, int initialTelecomId) {
		super(listOfIdentifier, initialCellId, initialTelecomId);
		_supportFileName = supportFileName;
		_geoCoordsFileName = geoCoordsFileName;
	}

	@Override
	public boolean parse() {
		if ((_supportFileName == null) || (_geoCoordsFileName == null)) {
			LOG.error("parseCartoRadio::No CartoRadio because at least one file is missing.");
			return false;
		}

		CartoRadioGeoCoords geoCoords = new CartoRadioGeoCoords(_geoCoordsFileName);
		Map<String, CartoRadioSupport> cells = new HashMap<String, CartoRadioSupport>();
		try(FileReader supportFile = new FileReader(_supportFileName);
				BufferedReader reader = new BufferedReader (supportFile)) {

			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String value = currentLine.trim();
				String[] listOfValues = value.split(";");
				CartoRadioSupport currentRadioSupport = new CartoRadioSupport(listOfValues);

				String theNewKey = new String(currentRadioSupport.getAttribute(CartoRadioSupport.INDEX_CARTORADIO_SUPPORT_ANTENNA_ID) 
						+ currentRadioSupport.getAttribute(CartoRadioSupport.INDEX_CARTORADIO_SUPPORT_ANTENNA_SYSTEM));

				if (cells.containsKey(theNewKey) == false) {
					cells.put(theNewKey, currentRadioSupport);
				} else {
					LOG.warn("parseCartoRadio::Ignore cell: " + theNewKey);
				}	
			}
		} catch (Exception ex){
			LOG.info("parseCartoRadio::Exception when reading file " + _geoCoordsFileName, ex);
			return false;
		}

		LOG.info("parseCartoRadio::Number of cells: " + cells.values().size());

		initializeCartoRadioSupport(cells.values(), geoCoords);

		return true;
	}

	void initializeCartoRadioSupport(Collection<CartoRadioSupport> cells, CartoRadioGeoCoords geoCoords) {
		for (CartoRadioSupport currentSupport : cells) {
			String supportId = currentSupport.getAttribute(CartoRadioSupport.INDEX_CARTORADIO_SUPPORT_SUPPORT_ID);
			if (supportId == null) {
				LOG.warn("parseCartoRadio::Warning, nil support id found, ignore the entry!");
				continue;
			}

			CartoRadioGeoCoordinate currentCoordinate = geoCoords.findGeoCoordinateForSupport(supportId);

			if (currentCoordinate != null) {
				allocateNewCellForCartoRadio(currentCoordinate, currentSupport);
			} else {
				LOG.error("initializeCatroRadioSupport::Cannot find Geo cordinate for " + currentSupport.getAttribute(CartoRadioSupport.INDEX_CARTORADIO_SUPPORT_SUPPORT_ID));
			}
		}		
	}


	/**
	 * Create randomly a LTE/WCDMA/GSM cell. Created cell is recorded in the related 
	 * internal static table 
	 * 
	 * @param coordinate CartoRadio info about cell coordinate
	 * @param support CartoRadio info about the cells to create
	 */
	private void allocateNewCellForCartoRadio(CartoRadioGeoCoordinate coordinate, CartoRadioSupport support) {
		double longitude = 0, latitude = 0;

		try {
			NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
			Number number = format.parse(coordinate.getLongitude());
			longitude = number.doubleValue();
			number = format.parse(coordinate.getLatitude());
			latitude = number.doubleValue();
		}
		catch (Exception ex) {
			LOG.error(ex);
			return;
		}

		allocateNewCellForCartoRadio(latitude, longitude, support);
	}

	private void allocateNewCellForCartoRadio(double latitude, double longitude, CartoRadioSupport support) {

		String nodeId = getNodeId();
		Technology cellTechno = support.getCellTechno();

		String release = null;
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
		case GSM: {
			release =  Utility.getRandomString(NetworkGeneratorProperties.getGSMReleases());
			targetCellTable = _GSMCells;
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
		newCell.setDLFrequency(support.getAttribute(CartoRadioSupport.INDEX_CARTORADIO_SUPPORT_SUPPORT_START_FREQUENCY));
		newCell.setAzimuth(support.getAttribute(CartoRadioSupport.INDEX_CARTORADIO_SUPPORT_ANTENNA_AZIMUTH));
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
