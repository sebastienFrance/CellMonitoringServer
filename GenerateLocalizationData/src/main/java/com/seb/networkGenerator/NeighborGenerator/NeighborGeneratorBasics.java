package com.seb.networkGenerator.NeighborGenerator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkGenerator.generic.TopologyUtils;
import com.seb.networkTopology.database.DatabaseUtility;
import com.seb.networkTopology.generic.Utility;
import com.seb.topologyMgt.GeoLocation;
import com.seb.topologyMgt.TopologyMgtDatabaseQuery;
import com.seb.topologyMgt.TopologyMgtSQLRequestBuilder;
import com.seb.utilities.Technology;

public class NeighborGeneratorBasics implements NeighborGeneratorItf {
	private static final Logger LOG = LogManager.getLogger(NeighborGeneratorBasics.class);

	protected  List<Cell> _allLTECells 		= new ArrayList<Cell>();
	protected  List<Cell> _allWCDMACells 	= new ArrayList<Cell>();
	protected  List<Cell> _allGSMCells 		= new ArrayList<Cell>();
	
	private String _outputDatabaseName;

	public NeighborGeneratorBasics(String outputDatabaseName) {	
		_outputDatabaseName = outputDatabaseName;
	}
	
	@Override
	public void appendCellsFor(Technology techno, List<Cell> cells) {
		switch (techno) {
		case LTE: {
			_allLTECells.addAll(cells);
			break;
		}
		case WCDMA: {
			_allWCDMACells.addAll(cells);
			break;
		}
		case GSM: {
			_allGSMCells.addAll(cells);
			break;
		}
		default: {
			LOG.warn("appendCellsFor called with techno: " + techno);
		}
		}
	}
	
	@Override
	public List<Cell> getCells(Technology techno) {
		switch (techno) {
		case LTE: {
			return _allLTECells;
		}
		case WCDMA: {
			return _allWCDMACells;
		}
		case GSM: {
			return _allGSMCells;
		}
		default: {
			LOG.warn("appendCellsFor called with techno: " + techno);
		}
		}
		return new ArrayList<Cell>();
	}

	
	@Override
	public void insertNRsInTopologyDatabase() {
		try(Connection theConnection = getDatabaseConnection(_outputDatabaseName, true)) {
			LOG.info("generateNetworkTopologyDatabase::Insert LTE NRs in database");
			insertNRsInDatabase(theConnection, _allLTECells);
			LOG.info("generateNetworkTopologyDatabase::Insert WCDMA NRs in database");
			insertNRsInDatabase(theConnection, _allWCDMACells);
			LOG.info("generateNetworkTopologyDatabase::Insert GSM NRs in database");
			insertNRsInDatabase(theConnection, _allGSMCells);
			theConnection.commit();
			LOG.info("generateNetworkTopologyDatabase::End database creation");
		} catch (Exception e) {
			LOG.fatal("generateNetworkTopologyDatabase", e);
			System.exit(0);
		}
		LOG.info("generateNetworkTopologyDatabase::End");

	}

	private static void insertNRsInDatabase(Connection theConnection, List<Cell> cells) {
		for (Cell currentCell : cells) {
			currentCell.generateNeighborsInDatabase(theConnection);
		}
	}



	// Warning: almost same Java code in generateLocalization.java
	private Connection getDatabaseConnection(String outputDatabaseName, boolean appendExistingDatabase) {
		LOG.info("generateNetworkTopologyDatabase::Check database directory");
		String outputDatabaseDir = Utility.getDirectoryForCurrentDay(NetworkGeneratorProperties.getInstance().getOutputDirectory());
		if (outputDatabaseDir == null) {
			LOG.fatal("generateNetworkTopologyDatabase::Error: cannot create output directory for database");
			System.exit(1);
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
	 * Add neighbors relations to all cells based on max distance (configured in property file)
	 */
	@Override
	public void addNeighborRelations() {
		try(Connection theConnection = getDatabaseConnection(_outputDatabaseName, true)) {
			LOG.info("addNeighborRelations::Add neighbor relations for LTE");
			for (Cell currentCell : _allLTECells) {
				addNRForCellInRange(theConnection, currentCell, NetworkGeneratorProperties.getMaxDistanceLTENeighborsInMeters());
			}
			LOG.info("addNeighborRelations::Add neighbor relations for WCDMA");
			for (Cell currentCell : _allWCDMACells) {
				addNRForCellInRange(theConnection, currentCell, NetworkGeneratorProperties.getMaxDistanceWCDMANeighborsInMeters());
			}
			LOG.info("addNeighborRelations::Add neighbor relations for GSM");
			for (Cell currentCell : _allGSMCells) {
				addNRForCellInRange(theConnection, currentCell, NetworkGeneratorProperties.getMaxDistanceGSMNeighborsInMeters());
			}
			LOG.info("addNeighborRelations::End neighbor relations for GSM");
		}
		catch (Exception ex) {
			LOG.error(ex);
		}
	}

	/**
	 * Add neighbors relations for a cell to neighbors cells in a range of maxNRDistance
	 * 
	 * 
	 * @param centerCell The source cell
	 * @param maxNRDistance The max distance from source cell to create relations
	 */
	private void addNRForCellInRange(Connection theConnection, Cell centerCell, int maxNRDistanceInMeter) {
		LOG.info("Look for NR for: " + centerCell.getCellName() + " / " + centerCell.getTelecomId());
		GeoLocation centerLocation = GeoLocation.fromDegrees(centerCell.getLatitude(), centerCell.getLongitude());
		GeoLocation[] boundingBox = centerLocation.boundingCoordinates((maxNRDistanceInMeter / 1000.0), 6371.01);

		String sql = TopologyMgtSQLRequestBuilder.getCellsAroundPosition(true, boundingBox);

		int numberIntraRATNR = 0;
		int numberInterRATNR = 0;
		boolean maxIntraRATNR = false;
		boolean maxInterRATNR = false;

		try (Statement stmt = theConnection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)){

			while (rs.next()) {
				String telecomId = rs.getString("telecomid");
				if (centerCell.getTelecomId().equals(telecomId) == false) {

					double targetLongitude = rs.getDouble("longitude");
					double targetLatitude = rs.getDouble("latitude");
					String techno = rs.getString("techno");
					String dlFrequency = rs.getString("dlfrequency");

					GeoLocation targetCellLocation = GeoLocation.fromDegrees(targetLatitude, targetLongitude);

					if (TopologyUtils.isTargetCellInRange(centerCell, targetCellLocation, maxNRDistanceInMeter)) {

						if (centerCell.getTechno().equals(techno)) {
							numberIntraRATNR++;
							if (numberIntraRATNR <= NetworkGeneratorProperties.getMaxNumberIntraRATNR()) {
								TopologyUtils.addNeighborRelation(centerCell, telecomId, techno, dlFrequency);							
							} else {
								maxIntraRATNR = true;								
							}
						} else {
							numberInterRATNR++;
							if (numberInterRATNR <= NetworkGeneratorProperties.getMaxNumberInterRATNR()) {
								TopologyUtils.addNeighborRelation(centerCell, telecomId, techno, dlFrequency);							
							} else {
								maxInterRATNR = true;
							}
						}

						if ((maxInterRATNR == true) && (maxIntraRATNR == true)) {
							return;
						}					

					}
				}
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
	}
}
