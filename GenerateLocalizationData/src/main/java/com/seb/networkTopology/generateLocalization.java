package com.seb.networkTopology;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.stream.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.datasources.DatabaseDatasourceUtility;
import com.seb.datasources.Datasource;
import com.seb.datasources.DatasourceTopology;
import com.seb.networkTopology.database.DatabaseUtility;
import com.seb.networkTopology.generic.ParseTechnoItf;
import com.seb.networkTopology.generic.Utility;
import com.seb.networkTopology.gsm.ParseGSM;
import com.seb.networkTopology.lte.SimpleLTEParser;
import com.seb.networkTopology.wcdma.SimpleWCDMAParser;
import com.seb.topologyMgt.TopologyMgtDatabaseQuery;
import com.seb.utilities.Technology;
import com.seb.utilities.TraceUtility;


public class generateLocalization {
	private static final Logger LOG = LogManager.getLogger(generateLocalization.class);

	private static int LTECellCount = 0;
	private static int LTENeighborCount = 0;
	private static int WCDMACellCount = 0;
	private static int WCDMANeighborCount = 0;
	private static int GSMCellCount = 0;
	private static int GSMNeighborCount = 0;
	
	private static Connection _DSConnection = null;
	
	private generateLocalization() {}
	
	public static void main (String args[]) {
		
		if (args.length < 1) {
			LOG.fatal("Usage: generateLocalization propertyFileName [-add]");
			System.exit(1);
		}
		
		boolean openExistingDatabase = false;
		if(args.length >= 2) {
			if ("-add".equals(args[1])) {
				openExistingDatabase = true;
			}
		}
				
		generateLocalizationProperties propertiesInstance = generateLocalizationProperties.getInstance();
		propertiesInstance.initialize(args[0]);

		String datasourceFullName = propertiesInstance.getOutputDatasourceDirectory() + "/" + propertiesInstance.getOutputDatasourceDatabaseFileName();
		_DSConnection = DatabaseDatasourceUtility.createDatabase(datasourceFullName);
		if (_DSConnection == null) {
			LOG.fatal("Cannot create the Datasource database: " + propertiesInstance.getOutputDatasourceDatabaseFileName());
			System.exit(1);
		}
		
		String outputXMLDir = Utility.getDirectoryForCurrentDay(propertiesInstance.getOutputDirectory());
		if (outputXMLDir == null) {
			LOG.fatal("main::Error cannot create output directory: " + propertiesInstance.getOutputDirectory());
			System.exit(1);
		}

		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		generateAll(factory, openExistingDatabase);
	
		try {
			_DSConnection.commit();
			_DSConnection.close();
		} catch (SQLException e) {
			LOG.error(e);
		}
		
		if (openExistingDatabase == false) {
			Utility.resetLatestDirectory(propertiesInstance.getOutputDirectory());
		}
	}
	
	/**
	 * Generate the network topology for all techno (LTE/WCDMA/GSM)
	 * 
	 * @param fstream
	 * @param factory
	 */
	static void generateAll(XMLInputFactory factory, boolean openExistingDatabase) {
		
		try (Connection theConnection = getDatabaseConnection(openExistingDatabase)){
			
			if (theConnection == null) {
				LOG.fatal("generateAll::Cannot create or open a database !");
				System.exit(1);
			}
			
			generateForTechno(Technology.LTE, theConnection, factory);
			generateForTechno(Technology.WCDMA, theConnection, factory);
			generateGSM(theConnection);

			TopologyMgtDatabaseQuery.insertAbout(theConnection, 
					LTECellCount, LTENeighborCount, 
					WCDMACellCount, WCDMANeighborCount,
					GSMCellCount, GSMNeighborCount);

			theConnection.commit();
		}
		catch (Exception e) {
			LOG.error(e);
		}
	}
	
	static Connection getDatabaseConnection(boolean openExistingDatabase) {
		String outputDatabaseDir = Utility.getDirectoryForCurrentDay(generateLocalizationProperties.getInstance().getOutputDirectory());
		if (outputDatabaseDir == null) {
			LOG.fatal("generateAll::Error cannot create database directory");
			System.exit(1);
		}

		if (openExistingDatabase == false) {
			return TopologyMgtDatabaseQuery.createNetworkDatabase(outputDatabaseDir, generateLocalizationProperties.getInstance().getOutputDatabaseFileName());
		} else {
			return DatabaseUtility.openDatabaseConnection(outputDatabaseDir, generateLocalizationProperties.getInstance().getOutputDatabaseFileName());
		}
	}
	
	static void generateGSM(Connection theConnection) {
		try {
			ParseTechnoItf parser = new ParseGSM(generateLocalizationProperties.getInstance().getGSMSnapshotName(), generateLocalizationProperties.getInstance().getGSMSnapshotAdj()); 
			generateData(theConnection, parser);
			incrementCellCountersForTechno(parser);
			LOG.info("End GSM");
		} catch (Exception ex) {
			LOG.error(ex);;
		}
	}

	static void generateForTechno(Technology techno, Connection theConnection, XMLInputFactory factory) {
		if ((techno != Technology.LTE) && (techno != Technology.WCDMA)) {
			LOG.error("generateForTechno::called with a wrong technology :" + techno.toString());
		}
		
		String snapshotFolder = generateLocalizationProperties.getInstance().getSnapshotDirectoryName(techno);
		String snapshotExtension = generateLocalizationProperties.getInstance().getSnapshotFileExtension(techno);
		
		Datasource parentDS = createDatasourceForTechno(techno);

		File folder = new File(snapshotFolder);
		if (folder.isDirectory() == false) {
			LOG.error("generateForTechno::Error: snapshot folder is not a directory" + snapshotFolder);
			return;
		}
		
		for (String currentFileName : folder.list()) {
			DatasourceTopology DSTopology = createDatasourceTopology(parentDS, currentFileName);
			
			if (currentFileName.endsWith(snapshotExtension)) {			
				Date currrentStartDate = new Date();
				
				ParseTechnoItf parser = getSnapshotParserForTechno(techno, snapshotFolder, currentFileName, factory, DSTopology);				
				generateData(theConnection, parser);
				
				LOG.info("generateForTechno::end generating data for " + currentFileName + TraceUtility.duration(currrentStartDate));

				incrementCellCountersForTechno(parser);
			}
		}
		
		LOG.info("End generateForTechno for " + techno);
	}
	
	private static ParseTechnoItf getSnapshotParserForTechno(Technology techno, String snapshotFolder, String currentFileName, XMLInputFactory factory, DatasourceTopology DSTopology) {
		switch (techno) {
		case LTE: {
			return new SimpleLTEParser(snapshotFolder + "/" + currentFileName, factory, DSTopology);
		}
		case WCDMA: {
			return new SimpleWCDMAParser(snapshotFolder + "/" + currentFileName, factory, DSTopology);
		}
		case GSM: return null;
		default: return null;
		}
	}
	
	private static Datasource createDatasourceForTechno(Technology techno) {
		switch (techno) {
		case LTE: return createDatasource("LTE Datasource", Datasource.DS_TECHNO_LTE);
		case WCDMA: return createDatasource("WCDMA Datasource", Datasource.DS_TECHNO_WCDMA);
		case GSM: return null;
		default: return null;
		}
	}
	
	private static void incrementCellCountersForTechno(ParseTechnoItf parser) {
		switch (parser.getTechnology()) {
		case LTE: {
			LTECellCount += parser.getCellCount();
			LTENeighborCount += parser.getNeighborCount();					
			break;
		}
		case WCDMA: {
			WCDMACellCount += parser.getCellCount();
			WCDMANeighborCount += parser.getNeighborCount();
			break;
		}
		case GSM: {
			GSMCellCount += parser.getCellCount();
			GSMNeighborCount += parser.getNeighborCount();
			break;
		}
		default: {
			LOG.warn("incrementCellCountersForTechno:: called for an unmanaged technology -> " + parser.getTechnology());
		}
		}
	}

	
	/**
	 * Generate the topology with or without geographical relocation
	 * 
	 * @param out buffer used to write the topology
	 * @param parser to extract the topology from the input file and then generate the new topology
	 */
	static void generateData(Connection theConnection, ParseTechnoItf parser) {
		try {

			if ((generateLocalizationProperties.getInstance().getRelocateOnly() == false) || (generateLocalizationProperties.getInstance().getRelocate() == false)) {
				parser.parse(theConnection);
			}

			if (generateLocalizationProperties.getInstance().getRelocate()) {

				double targetLat = generateLocalizationProperties.getInstance().getRelocateTargetLatitude();
				double targetLong = generateLocalizationProperties.getInstance().getRelocateTargetLongitude();
				
				double srcLat = generateLocalizationProperties.getInstance().getRelocateSourceLatitude();
				double srcLong = generateLocalizationProperties.getInstance().getRelocateSourceLongitude();

				double deltaLat = targetLat - srcLat;
				double deltaLong = targetLong - srcLong;

				parser.setDeltaCoord(deltaLat, deltaLong);
				parser.setPrefix("Vr");
				parser.parse(theConnection);

			}

		}
		catch (Exception e) {
			LOG.error(e);
		}
	}
	

	
	/**
	 * Create Fake datasources in database 
	 * 
	 * @param name
	 * @param techno
	 * @return Datasource
	 */
	static Datasource createDatasource(String name, int techno) {
		Datasource newDS = DatabaseDatasourceUtility.createFakeDatasource(name, techno);
		
		DatabaseDatasourceUtility.insertDatasource(_DSConnection, newDS);
		int id = DatabaseDatasourceUtility.getLastRowId(_DSConnection);
		newDS.setId(id);
		
		return newDS;
	}
	
	/**
	 * Create Fake DatasourceTopology in database
	 * @param parentDS
	 * @param name
	 * @return
	 */
	static DatasourceTopology createDatasourceTopology(Datasource parentDS, String name) {
		DatasourceTopology newDSTopology = DatabaseDatasourceUtility.createFakeDatasourceTopology(parentDS, name);
		
		DatabaseDatasourceUtility.insertDatasourceTopology(_DSConnection, newDSTopology);
		int id = DatabaseDatasourceUtility.getLastRowId(_DSConnection);
		newDSTopology.setId(id);
		
		return newDSTopology;
	}
	
	

}
