package com.seb.networkGenerator;


import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.dataModel.About;
import com.seb.networkGenerator.CellGenerator.CartoRadioCellGenerator;
import com.seb.networkGenerator.CellGenerator.CellGeneratorItf;
import com.seb.networkGenerator.CellGenerator.OpenCellIdsCellGenerator;
import com.seb.networkGenerator.NeighborGenerator.GeoContainer;
import com.seb.networkGenerator.NeighborGenerator.NeighborGeneratorItf;
import com.seb.networkGenerator.NeighborGenerator.NeighborGeneratorPerGeographicalThreaded;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkGenerator.generic.TopologyUtils;
import com.seb.networkGenerator.generic.ZoneFileGenerator;
import com.seb.networkTopology.database.DatabaseUtility;
import com.seb.networkTopology.generic.Utility;
import com.seb.topologyMgt.GeoIndex;
import com.seb.topologyMgt.TopologyMgtDatabaseQuery;
import com.seb.utilities.Technology;

public class ImprovedNetworkGenerator {
	private static final Logger LOG = LogManager.getLogger(ImprovedNetworkGenerator.class);

	/**
	 * NetworkGenerator does:
	 * 		- Parse a KML file to create random cell (each POI becomes one or several cells)
	 * 		- Add for each cell a list of NR relation (intraFreq / interFreq / interRAT)
	 * 		- Generate an XML file with the network topology
	 * 		- Generate a zone file based on the Network topology
	 * 
	 * @param args
	 */
	public static void main (String args[]) {

		if (args.length < 1) {
			LOG.fatal("main::Usage: NetworkGenerator propertyFileName [-add]");
			System.exit(1);
		}

		boolean openExistingDatabase = false;
		if(args.length >= 2) {
			if (args[1].equals("-add")) {
				openExistingDatabase = true;
			}
		}

		NetworkGeneratorProperties instanceProperties = NetworkGeneratorProperties.getInstance();
		instanceProperties.initialize(args[0]);

		List<String> _listOfIdentifier = Utility.parseListOfValues(NetworkGeneratorProperties.getNodeSourceFileName());
		List<String> _listOfOpenCellIdsIdentifier = Utility.parseListOfValues(NetworkGeneratorProperties.getOpenCellIdsNodeSourceFileName());

		FakeDatasourceManager.getInstance().initializeWith(instanceProperties, openExistingDatabase);

		// Create cells from cartoRadio

		int initialCellId = 0;
		int initialTelecomId = 1000;

		LOG.info("main::Parsing cartoRadio");
		CellGeneratorItf cartoRadio = new CartoRadioCellGenerator(NetworkGeneratorProperties.getCartoRadioSupportsFileName(), NetworkGeneratorProperties.getCartoRadioGeoCoordsFileName(), _listOfIdentifier, 0, 1000);
		boolean result = cartoRadio.parse();
		if (result == false) {
			LOG.warn("main::Parsing failure for CartoRadio");
		} else {
			initialCellId = cartoRadio.getLatestCellId();
			initialTelecomId = cartoRadio.getLatestTelecomId();
		}

		LOG.info("main::Parsing OpenCellIds");
		CellGeneratorItf openCellIds = new OpenCellIdsCellGenerator(NetworkGeneratorProperties.getOpenCellIdsFileName(), NetworkGeneratorProperties.getOpenCellIdsFileVersion() , 
				_listOfOpenCellIdsIdentifier, initialCellId, initialTelecomId, NetworkGeneratorProperties.getOpenCellIdsCellPrefix());
		openCellIds.setMaxCellsPerSite(NetworkGeneratorProperties.getOpenCellIdsMaxCellsPerSite());
		result = openCellIds.parse();
		if (result == false) {
			LOG.warn("main::Parsing failure for openCellIds");
		} 

		LOG.info("main::End of parsing");
		Technology[] supportedTechnos = {Technology.LTE, Technology.WCDMA, Technology.GSM};
		for (Technology currentTechno : supportedTechnos) {
			int cellNumber = (cartoRadio.getCells(currentTechno).size() + openCellIds.getCells(currentTechno).size());
			LOG.info("main:: number of cells for techno " + currentTechno + ": " + cellNumber);
		}

		// Add all cells in the Topology Database
		LOG.info("main::Save cells in Database for CartoRadio");
		cartoRadio.insertCellsInTopologyDatabase(instanceProperties.getOutputDatabaseFileName(), openExistingDatabase);
		LOG.info("main::Save cells in Database for OpenCellsId");
		openCellIds.insertCellsInTopologyDatabase(instanceProperties.getOutputDatabaseFileName(), true);
		
		// cleanup memory before to generate the NRs
		LOG.info("main::Cleanup cells to recover some memory");
		for (Cell currentCell : cartoRadio.getCells(Technology.ALL)) {
			currentCell.cleanup();
		}
		for (Cell currentCell : openCellIds.getCells(Technology.ALL)) {
			currentCell.cleanup();
		}
		
		LOG.info("main::Build GeoContainer");
		GeoIndex theGeoData = new GeoIndex(0.01, 0.01);
		GeoContainer theContainer = new GeoContainer(theGeoData);
		theContainer.addCells(cartoRadio.getCells(Technology.ALL));
		theContainer.addCells(openCellIds.getCells(Technology.ALL));
		
		NeighborGeneratorItf NRGenerator = new NeighborGeneratorPerGeographicalThreaded(instanceProperties.getOutputDatabaseFileName(), theContainer);
		
		// useless for NeighborGeneratorPerGeographicalThreaded but usefull for NeighborGeneratorBasics... could be removed in future
		for (Technology currentTechno : supportedTechnos) {
			NRGenerator.appendCellsFor(currentTechno, cartoRadio.getCells(currentTechno));
		}
		
		for (Technology currentTechno : supportedTechnos) {
			NRGenerator.appendCellsFor(currentTechno, openCellIds.getCells(currentTechno));
		}	

		LOG.info("main::Generate NRs in Memory");
		NRGenerator.addNeighborRelations();

		LOG.info("main::Save NRs in Database");
		NRGenerator.insertNRsInTopologyDatabase();
		// Generate the zone file

		LOG.info("main::Insert about in Database");
		insertAboutInTopologyDatabase(NRGenerator, instanceProperties.getOutputDatabaseFileName(), true);

		LOG.info("main::Generate Zone files");
		generateZoneFile(NetworkGeneratorProperties.getZoneFileName(), NRGenerator);

		if (openExistingDatabase == false) {
			Utility.resetLatestDirectory(instanceProperties.getOutputDirectory());
		}
	}

	private static  void insertAboutInTopologyDatabase(NeighborGeneratorItf NRGenerator, String outputDatabaseName, boolean appendExistingDatabase) {
		try(Connection theConnection = getDatabaseConnection(outputDatabaseName, appendExistingDatabase)) {

			int LTENRCount = TopologyUtils.countNeighbors(NRGenerator.getCells(Technology.LTE));
			int WCDMANRCount = TopologyUtils.countNeighbors(NRGenerator.getCells(Technology.WCDMA));;
			int GSMNRCount = TopologyUtils.countNeighbors(NRGenerator.getCells(Technology.GSM));;

			 int LTECells = NRGenerator.getCells(Technology.LTE).size();
			 int WCDMACells = NRGenerator.getCells(Technology.WCDMA).size();
			 int GSMCells = NRGenerator.getCells(Technology.GSM).size();

			About oldAbout = TopologyMgtDatabaseQuery.getAbout(theConnection);
			if (oldAbout != null) {
				LOG.info("generateNetworkTopologyDatabase::Update About in database");
				LTENRCount += oldAbout.getLTENeighborCount();
				WCDMANRCount += oldAbout.getWCDMANeighborCount();
				GSMNRCount += oldAbout.getGSMNeighborCount();
				LTECells += oldAbout.getLTECellCount();
				WCDMACells += oldAbout.getWCDMACellCount();
				GSMCells += oldAbout.getGSMCellCount();
				TopologyMgtDatabaseQuery.updateAbout(theConnection, LTECells, LTENRCount, WCDMACells, WCDMANRCount, GSMCells, GSMNRCount);				
			} else {
				LOG.info("generateNetworkTopologyDatabase::Insert About in database");
				TopologyMgtDatabaseQuery.insertAbout(theConnection, LTECells, LTENRCount, WCDMACells, WCDMANRCount, GSMCells, GSMNRCount);
			}
			theConnection.commit();
			LOG.info("generateNetworkTopologyDatabase::End database creation");
		} catch (Exception e) {
			LOG.fatal("generateNetworkTopologyDatabase", e);
		}
	}
	
	// Warning: almost same Java code in generateLocalization.java
	static Connection getDatabaseConnection(String outputDatabaseName, boolean appendExistingDatabase) {
		LOG.info("generateNetworkTopologyDatabase::Check database directory");
		String outputDatabaseDir = Utility.getDirectoryForCurrentDay(NetworkGeneratorProperties.getInstance().getOutputDirectory());
		if (outputDatabaseDir == null) {
			LOG.fatal("generateNetworkTopologyDatabase::Error: cannot get database directory");
			return null;
		}

		if (appendExistingDatabase == true) {
			LOG.info("generateNetworkTopologyDatabase::Open existing database");
			return DatabaseUtility.openDatabaseConnection(outputDatabaseDir, outputDatabaseName);
		} else {
			LOG.info("generateNetworkTopologyDatabase::Create database");
			return TopologyMgtDatabaseQuery.createNetworkDatabase(outputDatabaseDir, outputDatabaseName);
		}
	}

	/**
	 * 
	 * Generate random zones for each technology (LTE/WCDMA/GSM)
	 * 
	 * @param outputFileDir Directory where the file will be created
	 * @param zoneFileName Name of the zone file
	 */
	private static void generateZoneFile(String zoneFileName, NeighborGeneratorItf NRGenerator) {
		LOG.info("Start zone file creation");
		ZoneFileGenerator zoneFile = new ZoneFileGenerator(zoneFileName);
		zoneFile.generateZoneFor(NRGenerator.getCells(Technology.LTE));
		zoneFile.generateZoneFor(NRGenerator.getCells(Technology.WCDMA));
		zoneFile.generateZoneFor(NRGenerator.getCells(Technology.GSM));
		zoneFile.closeZoneFile();
		LOG.info("End zone file creation");
	}

}
