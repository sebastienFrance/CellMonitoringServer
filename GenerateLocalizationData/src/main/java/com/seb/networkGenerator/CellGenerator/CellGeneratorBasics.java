package com.seb.networkGenerator.CellGenerator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkTopology.database.DatabaseUtility;
import com.seb.networkTopology.generic.Utility;
import com.seb.topologyMgt.GeoIndex;
import com.seb.topologyMgt.TopologyMgtDatabaseQuery;
import com.seb.utilities.Technology;

public abstract class CellGeneratorBasics implements CellGeneratorItf {
	private static final Logger LOG = LogManager.getLogger(CellGeneratorBasics.class);

	protected int _cellId;
	protected int _telecomId;

	protected  List<Cell> _LTECells = new ArrayList<Cell>();
	protected  List<Cell> _WCDMACells = new ArrayList<Cell>();
	protected  List<Cell> _GSMCells = new ArrayList<Cell>();
	protected  List<Cell> _allCells = new ArrayList<Cell>();


	protected List<String> _listOfIdentifier;
	protected int _maxCellsPerSite = 1;

	protected CellGeneratorBasics(List<String> listOfIdentifier, int initialCellId, int initialTelecomId) {
		_listOfIdentifier = listOfIdentifier;
		_cellId = initialCellId;
		_telecomId = initialTelecomId;
	}

	@Override
	public void setMaxCellsPerSite(int maxCellsPerSite) {
		if (maxCellsPerSite > 0) {
			_maxCellsPerSite = maxCellsPerSite;
		}
	}

	@Override
	public int getLatestCellId() {
		return _cellId;
	}

	@Override
	public int getLatestTelecomId() {
		return _telecomId;
	}

	@Override
	public List<Cell> getCells(Technology techno) {
		switch (techno) {
		case LTE: {
			return _LTECells;
		}
		case WCDMA: {
			return _WCDMACells;
		}
		case GSM: {
			return _GSMCells;
		}
		case ALL: {
			return _allCells;
		}
		default: {
			return null;
		}
		}
	}

	/**
	 * Return a unique name extracted from the input file
	 * 
	 * 
	 * @return unique name to build a cell name
	 */
	protected String getNodeId() {
		String nodeId = _listOfIdentifier.get(0);
		_listOfIdentifier.remove(0);
		return nodeId;
	}



	@Override
	public  void insertCellsInTopologyDatabase(String outputDatabaseName, boolean appendExistingDatabase) {
		try(Connection theConnection = getDatabaseConnection(outputDatabaseName, appendExistingDatabase)) {
			LOG.info("generateNetworkTopologyDatabase::Insert LTE Cells in database");
			insertCellsInDatabase(theConnection, _LTECells);
			LOG.info("generateNetworkTopologyDatabase::Insert WCDMA Cells in database");
			insertCellsInDatabase(theConnection, _WCDMACells);
			LOG.info("generateNetworkTopologyDatabase::Insert GSM Cells in database");
			insertCellsInDatabase(theConnection, _GSMCells);
			theConnection.commit();
			LOG.info("generateNetworkTopologyDatabase::End database creation");
		} catch (Exception e) {
			LOG.fatal("generateNetworkTopologyDatabase", e);
			System.exit(0);
		}
	}

	private  static void insertCellsInDatabase(Connection theConnection, List<Cell> cells) {
		for (Cell currentCell : cells) {
			currentCell.generateCellOnlyInDatabase(theConnection);
		}
	}

	// Warning: almost same Java code in generateLocalization.java
	private Connection getDatabaseConnection(String outputDatabaseName, boolean appendExistingDatabase) {
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
}
