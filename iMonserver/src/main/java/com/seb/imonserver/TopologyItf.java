package com.seb.imonserver;

import java.io.OutputStream;
import java.util.List;

import com.seb.imonserver.datamodel.Cell;
import com.seb.userManagement.UserDescription;
import com.seb.imonserver.generic.Zone;
import com.seb.topologyMgt.GeoLocation;
import com.seb.imonserver.utilities.ParameterWithValues;


/**
 * Generic interface that defines the topology web services and others mandatory methods
 * 
 * @author Sebastien
 *
 */
public interface TopologyItf {
	public void getAbout(OutputStream out);
	public void getCellsStartingWith(String matching, String techno, int maxResults, OutputStream out, UserDescription user);
	public void getCellsAroundPosition(boolean findCellsByGeoIndex, double latitude, double longitude, double distance, OutputStream out, UserDescription user);
	
	public void getCellsAroundRoute(boolean findCellsByGeoIndex, List<GeoLocation> theRoute, double distance, OutputStream out, UserDescription user);
	public void getCellWithNeighbors(String cellName, String techno, OutputStream out);
	public void getCellWithNeighborsHistorical(String NetworkDatabaseDirectory, String cellName, String techno, OutputStream out);
	public void getCellsFromZone(Zone theZone, OutputStream out, UserDescription user);
	public void getCells(String[] cells, OutputStream out, UserDescription user);
	
	public void getCellParameters(String cellName, String techno, OutputStream out);
	public void getCellParametersHistory(String NetworkDatabaseDirectory, String cellName, String techno, OutputStream out);
	public void getCountParameters(int objectType, String[] objectIds, String paramName, String[] parameterValues, OutputStream out);
	public void getCountParameterList(int objectType, String[] objectIds, List<ParameterWithValues> paramListValues, OutputStream out);
	
	public Cell findCell(String cellName, String techno);
}
