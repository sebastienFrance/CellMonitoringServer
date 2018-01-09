package com.seb.imonserver;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import com.seb.dataModel.About;
import com.seb.imonserver.database.DatabaseQuery;
import com.seb.imonserver.database.DatabaseUtility;
import com.seb.imonserver.datamodel.Adjacency;
import com.seb.imonserver.datamodel.AttrNameValue;
import com.seb.imonserver.datamodel.Cell;
import com.seb.userManagement.UserDescription;
import com.seb.imonserver.generic.JSONUtilities;
import com.seb.imonserver.generic.Zone;
import com.seb.topologyMgt.GeoLocation;
import com.seb.topologyMgt.TopologyMgtDatabaseQuery;
import com.seb.imonserver.utilities.ParameterWithValues;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TopologyFromDatabase implements TopologyItf {
	private static final Logger LOG = LogManager.getLogger(TopologyFromDatabase.class);

	private String _networkDatabaseName;

	public TopologyFromDatabase(String networkDatabaseName) {
		_networkDatabaseName = networkDatabaseName;
	}

	@Override
	public void getAbout(OutputStream out) {

		LOG.info("getAbout called");
		
		About myAbout = TopologyMgtDatabaseQuery.getAbout(_networkDatabaseName);

		JSONObject newObject = JSONObject.fromObject(myAbout);
		JSONUtilities.sendJSONObject(newObject, out);
	}

	@Override
	public Cell findCell(String cellName, String techno) {
		return DatabaseQuery.getCell(_networkDatabaseName, cellName, techno);
	}

	@Override
	public void getCellsStartingWith(String matching, String techno, int maxResults, OutputStream out, UserDescription user) {
		List<Cell> matchingCells = DatabaseQuery.getCellsStartingWith(_networkDatabaseName, matching, techno, maxResults);
		sendCellsToClient(matchingCells, out);
	}
	
	@Override
	public void getCellsAroundPosition(boolean findCellsByGeoIndex, double latitude, double longitude, double distance, OutputStream out, UserDescription user) {

		GeoLocation centerLocation = GeoLocation.fromDegrees(latitude, longitude);
		GeoLocation[] boundingBox = centerLocation.boundingCoordinates(distance, 6371.01);

		List<Cell> matchingCells = DatabaseQuery.getCellsAroundPosition(findCellsByGeoIndex, _networkDatabaseName, boundingBox, centerLocation, distance, user);

		sendCellsToClient(matchingCells, out);
	}

	@Override
	public void getCellsAroundRoute(boolean findCellsByGeoIndex, List<GeoLocation> theRoute, double distance, OutputStream out, UserDescription user) {
		Collection<Cell> matchingCells = DatabaseQuery.getCellsAroundRoute(findCellsByGeoIndex, _networkDatabaseName, theRoute, distance, user);

		sendCellsToClient(matchingCells, out);
	}

	private static void sendCellsToClient(Collection<Cell> cells, OutputStream out) {
		JSONArray newArray = new JSONArray();
		JSONObject newObject = null;
		for (Cell currentCell : cells) {
			newObject = currentCell.extractJSONEncoding();
			if (newObject == null) {
				newObject = JSONObject.fromObject(currentCell);
			}
			newArray.add(newObject);
		}

		JSONUtilities.sendJSONObject(newArray, out);
	}


	@Override
	public void getCellWithNeighbors(String cellName, String techno, OutputStream out) {
		JSONArray newArray = getCellWithNeighbors(cellName, techno, _networkDatabaseName);
		JSONUtilities.sendJSONObject(newArray, out);
	}
	
	
	public JSONArray getCellWithNeighbors(String cellName, String techno, String databaseName) {
		
		try(Connection theConnection = DatabaseUtility.openDatabaseConnection(databaseName)) {

			JSONArray newArray = new JSONArray();
			Cell currentCell = DatabaseQuery.getCell(theConnection, cellName, techno);
			if (currentCell != null) {
				JSONObject newObject = JSONObject.fromObject(currentCell);
				newArray.add(newObject);

				// Add neighbors....
				JSONArray neighbors = new JSONArray();
				List<Adjacency> adjList = DatabaseQuery.getCellNeighbors(theConnection, cellName);
				neighbors.addAll(adjList);
				newObject = new JSONObject();
				newObject.put("NR", neighbors);

				newArray.add(newObject);

				JSONArray targetNeighbors = new JSONArray();

				// add all target cells

				for (Adjacency targetAdj : adjList) {

					Cell targetCell = DatabaseQuery.getCellbyTelecomId(theConnection, targetAdj.getTargetCell());
					if (targetCell != null) {
						newObject = JSONObject.fromObject(targetCell);
						targetNeighbors.add(newObject);					  
					} else {
						LOG.warn("getCellWithNeighbors::Cannot find target : " + targetAdj.getTargetCell());
					}				  
				}
				newObject = new JSONObject();
				newObject.put("TargetCells", targetNeighbors);
				newArray.add(newObject);

			}
			return newArray;
		} 
		catch (Exception ex) {
			LOG.error(ex);
			return null;
		}
	}
	
	
	@Override
	public void getCellWithNeighborsHistorical(String NetworkDatabaseDirectory, String cellName, String techno, OutputStream out) {
		String[] databaseList = DatabaseUtility.getDatabaseListOrderedByDate(NetworkDatabaseDirectory);
		if ((databaseList == null) || (databaseList.length == 0)) {
			LOG.warn("getCellWithNeighborsHistorical::No database!");						
		}
		
		JSONArray fullCellNRHistory = new JSONArray();
		for (String currentFileName : databaseList) {
			int index = currentFileName.lastIndexOf(File.separator);
			
			JSONArray cellForNRs = getCellWithNeighbors(cellName, techno, DatabaseUtility.buildFullDatabaseName(currentFileName));
			JSONObject theNewRow = new JSONObject();
			theNewRow.put("Date", currentFileName.substring(index+1));
			theNewRow.put("CellsWithNeighbors", cellForNRs);
			fullCellNRHistory.add(theNewRow);
		}
		
		JSONUtilities.sendJSONObject(fullCellNRHistory, out);

	}

	@Override
	public void getCellsFromZone(Zone theZone, OutputStream out, UserDescription user) {
		if (theZone == null) {
			try {
				out.close();
			} catch (Exception ex) {
				LOG.error(ex);
			}
			return;
		}

		String[] listOfCellsFromZone = theZone.extractListOfCells();
		JSONArray newArray = getCellsInJSONFromDatabase(listOfCellsFromZone);
		JSONUtilities.sendJSONObject(newArray, out);
	}

	@Override
	public void getCells(String[] cells, OutputStream out, UserDescription user) {
		JSONArray newArray = getCellsInJSONFromDatabase(cells);
		JSONUtilities.sendJSONObject(newArray, out);
	}


	private JSONArray getCellsInJSONFromDatabase(String[] cells) {
		JSONArray newArray = new JSONArray();
		List<Cell> theCells = DatabaseQuery.getCells(_networkDatabaseName, cells);

		for (Cell currentCell : theCells) {				  
			JSONObject newObject = JSONObject.fromObject(currentCell);
			newArray.add(newObject);
		}
		return newArray;
	}
	@Override
	public void getCellParameters(String cellName, String techno, OutputStream out) {
		JSONArray newArray = getCellParameters(cellName, techno, _networkDatabaseName);
		JSONUtilities.sendJSONObject(newArray, out);
	}
	
	public JSONArray getCellParameters(String cellName, String techno,  String databaseName) {
		JSONArray newArray  = new JSONArray();
		List<AttrNameValue> attrNameValues = DatabaseQuery.getCellAttrNameValue(databaseName, cellName);

		newArray.addAll(attrNameValues);
		return newArray;
	}

	@Override
	public void getCellParametersHistory(String NetworkDatabaseDirectory, String cellName, String techno, OutputStream out) {
		String[] databaseList = DatabaseUtility.getDatabaseListOrderedByDate(NetworkDatabaseDirectory);
		if ((databaseList == null) || (databaseList.length == 0)) {
			LOG.warn("getCellParametersHistory::No database!");						
		}

		JSONArray fullCellParametersHistory = new JSONArray();
		for (String currentFileName : databaseList) {
			int index = currentFileName.lastIndexOf(File.separator);

			JSONArray cellParameters = getCellParameters(cellName, techno, DatabaseUtility.buildFullDatabaseName(currentFileName));
			JSONObject theNewRow = new JSONObject();
			theNewRow.put("Date", currentFileName.substring(index+1));
			theNewRow.put("CellsWithParameters", cellParameters);
			fullCellParametersHistory.add(theNewRow);
		}

		JSONUtilities.sendJSONObject(fullCellParametersHistory, out);
	}

	@Override
	public void getCountParameters(int objectType, String[] objectIds, String paramName, String[] parameterValues, OutputStream out) {
		try (Connection theConnection = DatabaseUtility.openDatabaseConnection(_networkDatabaseName)) {
			
			int theCount = DatabaseQuery.getCountParameters(theConnection, objectType, objectIds, paramName, parameterValues);

			JSONObject newObject = new JSONObject();
			newObject.put("Value", new Integer(theCount));
			JSONUtilities.sendJSONObject(newObject, out);
		}
		catch (Exception ex) {
			LOG.error(ex);
		}
		
	}

	@Override
	public void getCountParameterList(int objectType, String[] objectIds, List<ParameterWithValues> paramListValues, OutputStream out) {
		try (Connection theConnection = DatabaseUtility.openDatabaseConnection(_networkDatabaseName)) {
			
			JSONObject newObject = new JSONObject();
			for (ParameterWithValues currentParamValues : paramListValues) {
				String[] parameterValues = currentParamValues.getParameterValues();
				String parameterName = currentParamValues.getParameterName();
				int theCount = DatabaseQuery.getCountParameters(theConnection, objectType, objectIds, parameterName, parameterValues);
				newObject.put(parameterName, new Integer(theCount));
			}			

			JSONUtilities.sendJSONObject(newObject, out);
		}
		catch (Exception ex) {
			LOG.error(ex);
		}	
	}
}
