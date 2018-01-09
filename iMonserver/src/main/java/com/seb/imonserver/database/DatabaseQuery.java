package com.seb.imonserver.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.dataModel.About;
import com.seb.imonserver.datamodel.Adjacency;
import com.seb.imonserver.datamodel.AttrNameValue;
import com.seb.imonserver.datamodel.Cell;
import com.seb.imonserver.generic.CellDSInfo;

import com.seb.userManagement.UserDescription;
import com.seb.topologyMgt.GeoIndexAllocator;
import com.seb.topologyMgt.GeoLocation;
import com.seb.topologyMgt.TopologyMgtDatabaseUtility;
import com.seb.topologyMgt.TopologyMgtSQLRequestBuilder;

public class DatabaseQuery {
	private static final int OBJECT_TYPE_CELL = 0;
	private static final int OBJECT_TYPE_CELL_ATTRIBUTES = 1;
	private static final int OBJECT_TYPE_NEIGHBOR_RELATION = 2;
	
	private static final Logger LOG = LogManager.getLogger(DatabaseQuery.class);
	
	private DatabaseQuery() {}
	
	/**
	 * Extract a Cell from the database from its name and technology
	 * 
	 * @param theConnection the connection to the database
	 * @param cellName name of the cell to be extracted
	 * @param techno technology of the cell to be extracted
	 * @return The cell initialized if found otherwise null is returned
	 */
	public static Cell getCell(String networkDatabaseName, String cellName, String techno) {
		try (Connection theConnection = DatabaseUtility.openDatabaseConnection(networkDatabaseName)) {
			return getCell(theConnection, cellName, techno);
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
		return null;
	}
	
	public static Cell getCell(Connection theConnection, String cellName, String techno) {
		String sql = TopologyMgtSQLRequestBuilder.getCell(cellName, techno);
		
		return extractCellUsingSQLReq(theConnection, sql);
	}


	/**
	 * Extract a Cell from the database from its name
	 * 
	 * @param theConnection the connection to the database
	 * @param cellName name of the cell to be extracted
	 * @return The cell initialized if found otherwise null is returned
	 */
	public static Cell getCellByName(Connection theConnection, String cellName) {
		String sql = TopologyMgtSQLRequestBuilder.getCellByName(cellName);
		
		return extractCellUsingSQLReq(theConnection, sql);
	}

	/**
	 *  Extract a Cell from the database from its telecomId
	 *  
	 * @param theConnection the connection to the database
	 * @param telecomId telecomId of the cell to be extracted
	 * @return The cell initialized if found otherwise null is returned
	 */
	public static Cell getCellbyTelecomId(Connection theConnection, String telecomId) {
		String sql = TopologyMgtSQLRequestBuilder.getCellbyTelecomId(telecomId);
		
		return extractCellUsingSQLReq(theConnection, sql);
	}
	
	private static Cell extractCellUsingSQLReq(Connection theConnection, String sql) {
		try (Statement stmt = theConnection.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.isBeforeFirst() == true) {// means the resultset is not empty 
				return DatabaseUtility.createCellFromResultSet(rs);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
		return null;
	}

	public static CellDSInfo getCellDSInfoByCellName(Connection theConnection, String cellName) {
		
		if (theConnection == null) {
			LOG.warn("getCellDSInfoByCellName::Connection is null cannot get the DS");
			return null;
		}
		
		if (cellName == null) {
			LOG.warn("getCellDSInfoByCellName::cellName is null cannot get the DS");
			return null;
		}
		
		String sql = TopologyMgtSQLRequestBuilder.getCellDSInfoByCellName(cellName);
		
		CellDSInfo newCellDSInfo = null;
		try(Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			newCellDSInfo = DatabaseUtility.createCellDSInfoFromResultSet(rs);
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
		return newCellDSInfo;
	}

	public static List<CellDSInfo> getCellDSInfoByCellNames(Connection theConnection, String[] cellNames) {
		if (theConnection == null) {
			LOG.warn("getCellDSInfoByCellName::Connection is null cannot get the DS");
			return null;
		}
		
		if ((cellNames == null) || (cellNames.length == 0)) {
			LOG.warn("getCellDSInfoByCellName::cellNames is null or empty cannot get the DS");
			return null;
		}

		String sql = TopologyMgtSQLRequestBuilder.getCellDSInfoByCellNames(cellNames);
		
		List<CellDSInfo> newCellDSInfoList = new ArrayList<CellDSInfo>();
		try (Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			while ( rs.next() ) {
				CellDSInfo newCellDSInfo = DatabaseUtility.createCellDSInfoFromResultSet(rs);
				newCellDSInfoList.add(newCellDSInfo);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
		return newCellDSInfoList;
	}



    /**
     * Extract a list of cells 
     * 
     * @param theConnection the connection to the database
     * @param listOfCells Names of cells to be extracted
     * @return A list that contains all found cells
     */
	public static List<Cell> getCells(String networkDatabaseName, String[] listOfCells) {

		List<Cell> cells = new ArrayList<Cell>();
		try(Connection theConnection = DatabaseUtility.openDatabaseConnection(networkDatabaseName);) {
			for (String currentCellName : listOfCells) {
				// TODO: Optimize, only one request to extract all cells from DB

				Cell currentCell = getCellByName(theConnection, currentCellName);
				cells.add(currentCell);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

		return cells;
	}

	/**
	 * Extract maxResults of cells whose name is starting with the matching parameter
	 * 
	 * @param theConnection the connection to the database
	 * @param matching string pattern used to find the cells (all cells starting with matching string)
	 * @param techno technology to search cells
	 * @param maxResults maximum of cells to be searched
	 * @return List of cells matching the pattern
	 */
	public static List<Cell> getCellsStartingWith(String networkDatabaseName, String matching, String techno, int maxResults) {
		List<Cell> matchingCells = new ArrayList<Cell>();

		String sql = TopologyMgtSQLRequestBuilder.getCellsStartingWith(matching, techno, maxResults);
		try(Connection theConnection = DatabaseUtility.openDatabaseConnection(networkDatabaseName);
			Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Cell newCell = DatabaseUtility.createCellFromResultSet(rs);
				matchingCells.add(newCell);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

		return matchingCells;
	}

	/**
	 * Get the cells around a geographical location
	 * 
	 * @param theConnection the connection to the database
	 * @param boundingBox the geographical box gives min/max for lat/long 
	 * @param centerLocation center location for searching
	 * @param distance max distance from a center location to get a cell
	 * @param user user requesting the data
	 * @return List of cells located in the circle in the distance from the center location
	 */

	public static List<Cell> getCellsAroundPosition(boolean findCellsByGeoIndex, String networkDatabaseName, 
			GeoLocation[] boundingBox, GeoLocation centerLocation, double distance, UserDescription user) {

	if (findCellsByGeoIndex) {
			return getCellsAroundPositionThreaded(networkDatabaseName, boundingBox, centerLocation, distance, user);
		} else {

			try (Connection theConnection = DatabaseUtility.openDatabaseConnection(networkDatabaseName)){
				return getCellsAroundPosition(findCellsByGeoIndex, theConnection, boundingBox, centerLocation, distance, user);
			} 
			catch (Exception ex) {
				LOG.error(ex);
			}

			return new ArrayList<Cell>();
		}
	}

	public static List<Cell> getCellsAroundPosition(boolean findCellsByGeoIndex, Connection theConnection, 
			GeoLocation[] boundingBox, GeoLocation centerLocation, double distance, UserDescription user) {

		List<Cell> matchingCells = new ArrayList<Cell>();
		
		String sql = TopologyMgtSQLRequestBuilder.getCellsAroundPosition(findCellsByGeoIndex, boundingBox);

		try (Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){

			matchingCells = new ArrayList<Cell>();
			while (rs.next()) {
				Cell newCell = DatabaseUtility.createCellFromResultSet(rs);
				if (isCellMatchingDistance(newCell, centerLocation, distance, user)) {
					matchingCells.add(newCell);
				}
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

		return matchingCells;
	}
	
	public static List<Cell> getCellsAroundPositionThreaded(String networkDatabaseName, 
			GeoLocation[] boundingBox, GeoLocation centerLocation, double distance, UserDescription user) {
		

		GeoIndexAllocator.GeoIndexSize indexSize = GeoIndexAllocator.getInstance().getBestGeoIndexSizeFor(boundingBox);
	    List<Long> indexes = GeoIndexAllocator.getInstance().indexesForBoundingBox(indexSize, boundingBox);
	    
	    ExecutorService executor = Executors.newCachedThreadPool();
	    List<WorkerExtractCellsFromGeoIndex> GeoIndexWorkerThreads = new ArrayList<WorkerExtractCellsFromGeoIndex>();
	    
	    for (Long currentIndex : indexes) {	    	
			WorkerExtractCellsFromGeoIndex worker = new WorkerExtractCellsFromGeoIndex(currentIndex, indexSize, networkDatabaseName, centerLocation, distance, user);
			GeoIndexWorkerThreads.add(worker);
			executor.execute(worker);
	    }
	    
		executor.shutdown();

		List<Cell> matchingCells = new ArrayList<Cell>();
		try {
			if (executor.awaitTermination(24, TimeUnit.HOURS) == false) {
				LOG.fatal("getCellsAroundPositionThreaded:: stopped on timeout, all threads not completed!");
			} else {
				for (WorkerExtractCellsFromGeoIndex currentWorkerThread : GeoIndexWorkerThreads) {
					matchingCells.addAll(currentWorkerThread.getCellsForGeoIndexResult());
				}
			}			
		} catch (Exception ex) {
			LOG.fatal("getCellsAroundPositionThreaded::Exception when executing threads", ex);
		}

	    return matchingCells;

	}
	

	public static Collection<Cell> getCellsAroundRoute(Boolean findCellsByGeoIndex, String networkDatabaseName, List<GeoLocation> theRoute, double distance, UserDescription user) {

		Map<String, Cell> cells = new HashMap<String, Cell>();

		try (Connection theConnection = DatabaseUtility.openDatabaseConnection(networkDatabaseName)) {
			List<Cell> matchingCells = new ArrayList<Cell>();

			for (GeoLocation currentCenterLocationPoint : theRoute) {
				GeoLocation[] boundingBox = currentCenterLocationPoint.boundingCoordinates(distance, 6371.01);

				matchingCells = getCellsAroundPosition(findCellsByGeoIndex, theConnection, boundingBox, currentCenterLocationPoint, distance, user);
				for (Cell currentCell : matchingCells) {
					cells.put(currentCell.getCellName(), currentCell);
				}
			}
		} catch (Exception ex) {
			LOG.error(ex);
		}

		return cells.values();
	}


	/**
	 * Compute if a cell is in the circle of a center location
	 * 
	 * @param theCell cell to be checked
	 * @param centerLocation lat/long of the center location
	 * @param distance max distance from the center location
	 * @param user user sending the request
	 * @return True when the cell is matching otherwise it returns false
	 */
	public static boolean isCellMatchingDistance(Cell theCell, GeoLocation centerLocation, double distance, UserDescription user) {

//		if (user.hasAccess(theCell) == false) {
//			return false;
//		}


		GeoLocation cellLocation = theCell.extractLocation();
		double distanceInKm = cellLocation.distanceTo(centerLocation, 6371.01);

		if (Double.isNaN(distanceInKm)) {
			// Can happens when the centerLocation and cell position are exactly the same??
			return true;
		} else {
			return distanceInKm < distance ? true : false; 
		}
	}

	/**
	 * Extracts the neighbors for a Cell
	 * 
	 * @param theConnection the connection to the database
	 * @param cellName Name of the cell
	 * @return List of Neighbor relations of the cell
	 */
	public static List<Adjacency> getCellNeighbors(Connection theConnection, String cellName) {
		List<Adjacency> adjacencies = new ArrayList<Adjacency>();
		
		String sql = TopologyMgtSQLRequestBuilder.getCellNeighbors(cellName);
		
		try(Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Adjacency newAdjacency = DatabaseUtility.createNeighborFromResultSet(rs);
				adjacencies.add(newAdjacency);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
		return adjacencies;	
	}

	/**
	 * Extracts the parameters of the cell
	 * 
	 * @param theConnection the connection to the database
	 * @param cellName Name of the cell
	 * @return List of AttrNameValue for the cell
	 */
	public static List<AttrNameValue> getCellAttrNameValue(String databaseName, String cellName) {
		List<AttrNameValue> attrNameValues = new ArrayList<AttrNameValue>();
		
		String sql = TopologyMgtSQLRequestBuilder.getCellAttrNameValue(cellName);
		
		try(Connection theConnection = DatabaseUtility.openDatabaseConnection(databaseName);
			Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				AttrNameValue newAttrNameValue = DatabaseUtility.createAttrNameValueFromResultSet(rs);
				attrNameValues.add(newAttrNameValue);
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}
		return attrNameValues;
	}

	public static int getCountParameters(Connection theConnection, int objectType, String[] objectIds, String paramName, String[] paramValues) {
		switch (objectType) {
		case OBJECT_TYPE_CELL: {
			return 0;
		}
		case OBJECT_TYPE_CELL_ATTRIBUTES: {
			return getCountParametersForCellAttributes(theConnection, objectType, objectIds, paramName, paramValues);
		}
		case OBJECT_TYPE_NEIGHBOR_RELATION: {
			return getCountParametersForNRs(theConnection, objectType, objectIds, paramName, paramValues);
		}
		default: {
			LOG.warn("getCountParameters::unknown objectTypet: "+ objectType);
			return 0;
		}
		}
	}
	
	
	public static int getCountParametersForCellAttributes(Connection theConnection, int objectType, String[] objectIds, String paramName, String[] paramValues) {

		String sqlRequest = TopologyMgtSQLRequestBuilder.getCountParametersForCellAttributes(objectIds, paramName, paramValues);
		return getCountParametersForSQLRequest(theConnection, sqlRequest);
	}
	
	public static int getCountParametersForNRs(Connection theConnection, int objectType, String[] objectIds, String paramName, String[] paramValues) {
		String sqlRequest = TopologyMgtSQLRequestBuilder.getCountParametersForNRs(objectIds, paramName, paramValues);
		return getCountParametersForSQLRequest(theConnection, sqlRequest);
	}
	
	private static int getCountParametersForSQLRequest(Connection theConnection, String sqlRequest) {
		try(Statement stmt = theConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlRequest)) {

			int countResult = 0;

			while (rs.next()) {
				countResult = rs.getInt(1);
			}
			return countResult;
		} 
		catch (Exception ex) {
			LOG.error(ex);
			return 0;
		}
	}
	
}
