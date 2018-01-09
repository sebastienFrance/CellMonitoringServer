package com.seb.imonserver.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;

import com.seb.datasources.DatasourceHelper;
import com.seb.datasources.DatasourceTopology;
import com.seb.imonserver.iMonServerProperties;
import com.seb.imonserver.database.DatabaseQuery;
import com.seb.imonserver.database.DatabaseUtility;
import com.seb.imonserver.generic.CellDSInfo;
import com.seb.imonserver.generic.Zone;
import com.seb.imonserver.generic.kpidictionaries.KPIDictionary;
import com.seb.topologyMgt.GeoLocation;

public class Utilities {

	private static final Logger LOG = LogManager.getLogger(Utilities.class);

	private static final int DEVICE_IPHONE = 1;
	private static final int DEVICE_IPAD = 10;
	private static final int DEVICE_MAC = 20;
	private static final int DEVICE_JMETER = 30;
	
	 public static String getDeviceName(String deviceType) {
		  if (deviceType != null) {
			  int deviceTypeInt = Integer.parseInt(deviceType);
			  switch (deviceTypeInt) {
			  case DEVICE_IPHONE: {
				  return "iPhone";
			  }
			  case DEVICE_IPAD: {
				  return "iPad";
			  }
			  case DEVICE_MAC: {
				  return "Mac";
			  }
			  case DEVICE_JMETER: {
				  return "jmeter";
			  }
			  default: {
				  return "Unkown Device Type";
			  }
			  }
		  } else {
			  return "No Device Type";
		  }
	  }

	 public static Map<String, String>  parseMultipartParameters(String parametersString) {
			String[] paramValues = parametersString.split("&");
			
			Map<String, String> resultMap = new HashMap<String,String>();
			
			for (int i = 0; i < paramValues.length; i++) {
				String currentParamValue = paramValues[i];
				String[] paramNameAndValue = currentParamValue.split("=");
				if (paramNameAndValue.length != 2) {
					LOG.warn("parseMultipartParameters::Not right set of component for " + currentParamValue);
					continue;
				}
				resultMap.put(paramNameAndValue[0], paramNameAndValue[1]);
			}
			return resultMap;
		}

	 /**
	   * Load zones from a CSV file where each row contains name;type;techno;description;list of cells
	   */
	  public static Map<String, Zone> loadZoneFile() {
		  Map<String, Zone> zones = new HashMap<String, Zone>();

		  try {
			  FileReader zoneFile = new FileReader(iMonServerProperties.getInstance().getZonesFileName());
			  BufferedReader reader = new BufferedReader (zoneFile);
			  String currentLine;

			  while ((currentLine = reader.readLine()) != null) {
				  // split current line in username and password

				  String[] rowContent = currentLine.split(";");
				  if (rowContent.length < Zone.MIN_INDEX_ZONE) {
					  LOG.warn("loadZoneFile::loadZoneFile: ignore row with => " + currentLine);
					  continue;
				  }

				  Zone newZone = new Zone(rowContent);
				  zones.put(newZone.getName(), newZone);
			  }
			  reader.close();

		  } catch (Exception ex){
			  LOG.error(ex);
		  }
		  
		  return zones;
	  }
	  
	  private static final String ADMIN_USER = "0";
	  public static boolean isUserAdmin(String isAdminString) {
		  if (isAdminString.equals(ADMIN_USER)) {
			  return false;
		  }  else {
			  return true;
		  }
	  }

	  public static DatasourceTopology getDatasourceForCell(String cellName, String networkDatabaseName) {
		  DatasourceTopology DS = null;
		  try {
			  Connection theConnection = DatabaseUtility.openDatabaseConnection(networkDatabaseName);
			  CellDSInfo cellDatasource = DatabaseQuery.getCellDSInfoByCellName(theConnection, cellName);
			  DS = DatasourceHelper.getInstance().getDatasourceTopologyFor(cellDatasource.getTopologyDatasource());
			  if (DS == null) {
				  LOG.warn("getDatasourceForCell::Cannot find DS for cell " + cellName);
			  }		  
		  } catch (Exception e) {
			  LOG.error(e);
		  } 
		  
		  return DS;
	  }

	  
	  private static final String SEPARATOR_COORDINATE  = ";";
	  private static final String SEPARATOR_LATITUDE_LONGITUDE = "_";
	  private static final int INDEX_LATITUDE = 0;
	  private static final int INDEX_LONGITUDE = 1;

	  public static List<GeoLocation> geoLocationFromCSVString(String route) {
	  	String[] coordList = route.split(SEPARATOR_COORDINATE);

	  	List<GeoLocation> theRoute = new ArrayList<GeoLocation>();

	  	for (String currentCoordinate : coordList) {
	  		String[] pointCoordinate = currentCoordinate.split(SEPARATOR_LATITUDE_LONGITUDE);

	  		if (pointCoordinate.length != 2) {
	  			LOG.warn("getCellsAroundRoute::Warning missing point for: " + currentCoordinate);
	  		} else {
	  			double latitude = Double.parseDouble(pointCoordinate[INDEX_LATITUDE]);
	  			double longitude = Double.parseDouble(pointCoordinate[INDEX_LONGITUDE]);

	  			theRoute.add(GeoLocation.fromDegrees(latitude, longitude));
	  		}
	  	}

	  	return theRoute;
	  }
	  
	  /**
	   * Send to the Client App the KPI dictionary in JSON format
	   * 
	   * @param response To send the KPI dictionary to the client App
	   * @param isZipEncoding True when the KPI dictionnary must be zipped
	   */
	  public static void getKPIDictionarieswithJSON(OutputStream output) {
		  JSONArray KPIDico = KPIDictionary.getKPIDictionaries();

		  try {
			  output.write(KPIDico.toString().getBytes());	
			  output.flush();
			  output.close();
		  }
		  catch (Exception ex) {
			  LOG.error(ex);
		  }
	  }



}
