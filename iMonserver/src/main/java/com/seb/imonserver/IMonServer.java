package com.seb.imonserver;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.seb.imonserver.datamodel.Alarm;
import com.seb.imonserver.eql.KPIEQLPlugin;
import com.seb.imonserver.generic.Credentials;
import com.seb.imonserver.generic.DatasourceParser;
import com.seb.imonserver.generic.JSONUtilities;
import com.seb.imonserver.kpisimu.AlarmSimulator;
import com.seb.imonserver.kpisimu.KPIEmptyPlugin;
import com.seb.imonserver.siteimage.SiteImageManager;
import com.seb.imonserver.userManagement.LocalUserManagement;
import com.seb.imonserver.userManagement.UserManagementItf;
import com.seb.imonserver.utilities.ParameterUtilities;
import com.seb.imonserver.utilities.ParameterWithValues;
import com.seb.imonserver.utilities.Utilities;
import com.seb.imonserver.generic.KPIPluginItf;
import com.seb.userManagement.UserDescription;
import com.seb.userManagement.UserManagementHelper;
import com.seb.utilities.TraceUtility;
import com.seb.imonserver.generic.Zone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



import java.util.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.seb.imonserver.generic.kpidictionaries.KPIDictionary;
import com.seb.topologyMgt.GeoLocation;
import com.seb.datasources.DatasourceHelper;
import com.seb.datasources.DatasourceTopology;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Main entry point to provide WEB services 
 * 
 * @author Sebastien Brugalieres
 *
 */
public class IMonServer extends  HttpServlet{
	private static final Logger LOG = LogManager.getLogger(IMonServer.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 
	 private static TopologyItf _topoItf;
	 private static KPIPluginItf _KPIItf;
	 
	 private static UserManagementItf _userManagement;

	 private static Map<String, Zone> _zones;
	 
	 private static final String PARAM_LATITUDE = "lat";
	 private static final String PARAM_LONGITUDE = "long";
	 private static final String PARAM_ROUTE = "route";
	 private static final String PARAM_DISTANCE = "dist";
	 private static final String PARAM_KPIS = "KPIs";
	 private static final String PARAM_IDS = "ids";
	 private static final String PARAM_CELLS = "cells";
	 private static final String PARAM_PERIODICITY = "periodicity";
	 private static final String PARAM_START_DATE = "startDate";
	 private static final String PARAM_END_DATE = "endDate";
	 private static final String PARAM_TECHNOLOGY = "techno";
	 private static final String PARAM_ID = "id";
	 private static final String PARAM_MATCHING = "matching";
	 private static final String PARAM_MAX_RESULTS = "maxResults";
	 private static final String PARAM_METHOD_NAME = "Method";
	 private static final String PARAM_USER_NAME = "user";
	 private static final String PARAM_ZIP = "zip";
	 private static final String PARAM_ZIP_NAME = "zipName";
	 private static final String PARAM_USER_PASSWORD = "passwd";
	 private static final String PARAM_USER_OLD_PASSWORD = "oldPasswd";
	 private static final String PARAM_USER_NEW_PASSWORD = "newPasswd";
	 private static final String PARAM_DEVICE_TYPE = "device";
	 private static final String PARAM_PARAMETER_NAME = "paramName";
	 private static final String PARAM_OBJECT_TYPE = "objectType";
	 private static final String PARAM_OBJECT_IDS = "objectIds"; 
	 private static final String PARAM_PARAMETER_VALUES = "paramValues";
	 private static final String PARAM_PARAMETER_LIST_VALUES = "paramListValues";
	 private static final String PARAM_USE_GEO_INDEX = "useGeoIndex";
	 
	 //
	 private static final String PARAM_ADD_USER_USER_NAME = "userName";
	 private static final String PARAM_ADD_USER_PASSWORD = "userPassword";
	 private static final String PARAM_ADD_USER_DESCRIPTION = "userDescription";
	 private static final String PARAM_ADD_USER_IS_ADMIN = "isAdmin";
	 private static final String PARAM_ADD_USER_FIRST_NAME = "firstName";
	 private static final String PARAM_ADD_USER_LAST_NAME = "lastName";
	 private static final String PARAM_ADD_USER_EMAIL = "email";
	 
	 private static final String PARAM_IMAGE_SITE_ID = "siteId";
	 private static final String PARAM_IMAGE_QUALITY = "imageQuality";
	 private static final String PARAM_IMAGE_NAME = "imageName";
	 
	 
	 private static final String PARAM_DELETE_USER_USER_NAME = "userName"; 

	 private static final String METHOD_CELLS_AROUND_POSITION = "cellsAroundPosition";
	 private static final String METHOD_CELLS_AROUND_ROUTE = "cellsAroundRoute";
	 private static final String METHOD_GET_CELLS_STARTING_WITH = "getCellsStartingWith";
	 private static final String METHOD_GET_CELLS = "getCells";
	 private static final String METHOD_GET_CELL_KPIS = "getCellKPIs";
	 private static final String METHOD_GET_KPIS_FOR_CELLS = "getKPIsForCells";
	 private static final String METHOD_GET_KPI_DICTIONARIES = "getKPIDictionaries";
	 private static final String METHOD_ABOUT = "about";
	 private static final String METHOD_CELL_WITH_NEIGHBORS = "cellWithNeighbors";
	 private static final String METHOD_CONNECT = "connect";
	 private static final String METHOD_CHANGE_PASSWORD = "changePassword";
	 private static final String METHOD_GET_USERS = "getUsers";
	 private static final String METHOD_ADD_USER = "addUser";
	 private static final String METHOD_DELETE_USER = "deleteUser";
	 private static final String METHOD_UPDATE_USER = "updateUser";
	 private static final String METHOD_GET_ZONE_LIST = "getZoneList";
	 private static final String METHOD_GET_CELLS_OF_ZONE = "getCellsOfZone";
	 private static final String METHOD_GET_CELL_PARAMETERS = "getCellParameters";
	 private static final String METHOD_GET_CELL_PARAMETERS_HISTORICAL = "getCelParametersHistorical";
	 private static final String METHOD_GET_CELL_ALARMS = "getCellAlarms";
	 private static final String METHOD_CELL_WITH_NEIGHBORS_HISTORICAL = "cellWithNeighborsHistorical";
	 private static final String METHOD_GET_COUNT_PARAMETER = "getCountParameter";
	 private static final String METHOD_GET_COUNT_PARAMETER_LIST = "getCountParameterList";
	 
	 private static final String METHOD_ADD_IMAGE = "addImage";
	 private static final String METHOD_GET_SITE_IMAGE_LIST = "getSiteImageList";
	 private static final String METHOD_GET_SITE_IMAGE = "getSiteImage";
	 private static final String METHOD_GET_DEFAULT_SITE_IMAGE = "getDefaultSiteImage";
	 private static final String METHOD_DELETE_SITE_IMAGE = "deleteSiteImage";
	  
   
	 /**
	  * Initialize the servlet, it does:
	  * 	- Parse the property file (using IMONITORING_PROP_FILE env variable to locate the property file)
	  * 	- Parse the KPI dictionary to build a dictionary in JSON format
	  * 	- Initialize a logger to logs the user operation only
	  * 	- Load the Database Topology  that contains LTE / WCDMA and GSM network informations
	  * 	- Load a Password file to grant or to deny connection
	  * 	- Load a zone file that contains topology zone (set of cells)
	  * 	- Initialize the Datasource manager (it's not Operational)
	  * 	- create the credentials to connect to NPO if we are using EQL and not the Simulator
	  */
	 @Override
	 public void init() {	

		 iMonServerProperties instanceProperties = iMonServerProperties.getInstance();

		 KPIDictionary.initKPIDictionary(instanceProperties.getKPIDictionaryName());

		 DatasourceHelper.getInstance().initialize(instanceProperties.getDatasourceDatabaseName());

		 _userManagement = new LocalUserManagement(instanceProperties.getUsersDatabaseName());
		 boolean initUserMgt = _userManagement.initialize();
		 if (initUserMgt == false) {
			 LOG.fatal("init::Cannot initialize user management");
			 System.exit(1);		  
		 }


		 _topoItf = new TopologyFromDatabase(instanceProperties.getNetworkDataBaseName());		  

		 if (instanceProperties.getZonesFileName() != null) {
			 _zones = Utilities.loadZoneFile();
		 }

		 initializeDatasource();

		 if (instanceProperties.getIsEQLPlugIn()) {
			 Credentials LTECredentials = new Credentials(instanceProperties.getLTE_NPO_IP(), instanceProperties.getLTE_EQL_PORT(), instanceProperties.getLTE_EQL_UserName(), instanceProperties.getLTE_EQL_Password());
			 Credentials WCDMACredentials = new Credentials(instanceProperties.getWCDMA_NPO_IP(), instanceProperties.getWCDMA_EQL_PORT(), instanceProperties.getWCDMA_EQL_UserName(), instanceProperties.getWCDMA_EQL_Password());
			 Credentials GSMCredentials = new Credentials(instanceProperties.getGSM_NPO_IP(), instanceProperties.getGSM_EQL_PORT(), instanceProperties.getGSM_EQL_UserName(), instanceProperties.getGSM_EQL_Password());

			 _KPIItf = new KPIEQLPlugin(_topoItf, LTECredentials, WCDMACredentials, GSMCredentials);
		 } else {
			 _KPIItf = new KPIEmptyPlugin(instanceProperties.getKPISimuFileName(), instanceProperties.getNetworkDataBaseName());
		 }

		 SiteImageManager.getInstance().initialize(instanceProperties.getSiteImageDatabaseDirectory());
	 }
   
   private void initializeDatasource() {
	   DatasourceParser parser = new DatasourceParser();
	   parser.parse(iMonServerProperties.getInstance().getDatasourceFileName());
   }
	
   /**
    * When the servlet receives a Post we manage it like a Get
    */
   @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
	  doGet(req, resp);
  }
 
  /**
   * Called when Client App request the list of Cells around a Latitude / longitude
   * 
   * @param request used to extract parameters of the request (Latitude / Longitude / distance)
   * @param OutputStream to send the list of cells to the client App
   * @param user Originator of the request
   */
void getCellsAroundPosition(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String latitude = request.getParameter(PARAM_LATITUDE);
	  String longitude = request.getParameter(PARAM_LONGITUDE);
	  String distance = request.getParameter(PARAM_DISTANCE);
	  String useGeoIndexString = request.getParameter(PARAM_USE_GEO_INDEX);
	  
	  boolean useGeoIndex = false;
	  if ("true".equals(useGeoIndexString)) {
		  useGeoIndex = true;
		  LOG.info("getCellsAroundPosition:: lookup with GeoIndex");
		  LOG.info("getCellsAroundPosition:: Latitude: " + latitude + " Longitude: " + longitude);
	  } else {
		  LOG.info("getCellsAroundPosition:: lookup using basic Lat/Long");		  
	  }
	  
	  double doubleLat = Double.parseDouble(latitude);
	  double doubleLong = Double.parseDouble(longitude);
	  double doubleDistance = Double.parseDouble(distance);
	  _topoItf.getCellsAroundPosition(useGeoIndex, doubleLat, doubleLong, doubleDistance, output, user);
  }


void getCellsAroundRoute(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String route = request.getParameter(PARAM_ROUTE);
	  String distance = request.getParameter(PARAM_DISTANCE);
	  
	  List<GeoLocation> theRoute = Utilities.geoLocationFromCSVString(route);
	  
	  double doubleDistance = Double.parseDouble(distance);
	  _topoItf.getCellsAroundRoute(iMonServerProperties.getInstance().getIsFindCellsByGeoIndex(), theRoute, doubleDistance, output, user);
}



/**
 * Called when Client App request the list of Cells 
 * 
 * @param request used to extract parameters of the request (list of cell)
 * @param output to send the list of cells to the client App
 * @param user Originator of the request
 */
void getCells(HttpServletRequest request,  OutputStream output, UserDescription user) {
	  String cells = request.getParameter(PARAM_CELLS);
	  
	  String[] listOfCells = ParameterUtilities.splitListOfValues(cells);
	  _topoItf.getCells(listOfCells, output, user);
}

/**
 *  Called when Client App requests value of KPIs for a set of cells
 * 
 * @param request used to extract parameters of the request (KPI list, Cell List, Periodicity, ...)
 * @param output to send the list of KPIs values per cell to the client App
 * @param user Originator of the request
 */
private void getKPIsForCells(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String kpis = request.getParameter(PARAM_KPIS);
	  String cellsId = request.getParameter(PARAM_IDS);
	  String periodicity = request.getParameter(PARAM_PERIODICITY);
	  String startDate = request.getParameter(PARAM_START_DATE);
	  String endDate = request.getParameter(PARAM_END_DATE);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  
	  _KPIItf.getKPIsForCells(cellsId, techno, kpis, periodicity, startDate, endDate, output);	
  }

/**
 *  Called when Client App requests value of KPIs for a cell
 * 
 * @param request used to extract parameters of the request (KPI list, Cell, Periodicity, ...)
 * @param output to send the list of KPIs values per cell to the client App
 * @param user Originator of the request
 */
 private void getCellKPIs(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String kpis = request.getParameter(PARAM_KPIS);
	  String cellId = request.getParameter(PARAM_ID);
	  String periodicity = request.getParameter(PARAM_PERIODICITY);
	  String startDate = request.getParameter(PARAM_START_DATE);
	  String endDate = request.getParameter(PARAM_END_DATE);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  
	  _KPIItf.getCellKPIs(cellId, techno, kpis, periodicity, startDate, endDate, output);
  }
  
 /**
  *  Called when Client App requests the list of cells from a specific Zone
  * 
  * @param request used to extract parameters of the request (Zone name)
  * @param output to send the list of KPIs values per cell to the client App
  * @param user Originator of the request
  */
  private static String PARAM_ZONE_NAME = "zoneName";
  private  void getCellsOfZone(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String zoneName = request.getParameter(PARAM_ZONE_NAME);

	  Zone theZone = _zones.get(zoneName);
	  _topoItf.getCellsFromZone(theZone, output, user);
  }

  /**
   *  Called when Client App requests the list of cells with its neighbors relations and neighbors cells
   * 
   * @param request used to extract parameters of the request (Cell id, technology of source cell)
   * @param output to send the list of KPIs values per cell to the client App
   * @param user Originator of the request
   */
  private void getCellWithNeighbors(HttpServletRequest request, OutputStream output, UserDescription user) {

	  String cellId = request.getParameter(PARAM_ID);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  
	  _topoItf.getCellWithNeighbors(cellId, techno, output);
	  
  }
  
  private void getCellWithNeighborsHistorical(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String cellId = request.getParameter(PARAM_ID);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  
	  iMonServerProperties instanceProperties = iMonServerProperties.getInstance();

	  _topoItf.getCellWithNeighborsHistorical(instanceProperties.getNetworkDataBaseDirectory(), cellId, techno, output);
  }


  private void getCellParameters(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String cellId = request.getParameter(PARAM_ID);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  
	  _topoItf.getCellParameters(cellId, techno, output);
  }
  
  private void getCellParametersHistory(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String cellId = request.getParameter(PARAM_ID);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  
	  iMonServerProperties instanceProperties = iMonServerProperties.getInstance();
	  _topoItf.getCellParametersHistory(instanceProperties.getNetworkDataBaseDirectory(),cellId, techno, output);
  }

  
  private void getCountParameters(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String paramName = request.getParameter(PARAM_PARAMETER_NAME);
	  String objectType = request.getParameter(PARAM_OBJECT_TYPE);
	  String[] parameterValues = ParameterUtilities.splitListOfValues(request.getParameter(PARAM_PARAMETER_VALUES));
	  String[] objectIds = ParameterUtilities.splitListOfValues(request.getParameter(PARAM_OBJECT_IDS));
	  
	  int objectTypeInt = Integer.parseInt(objectType);
	  
	  LOG.debug("getCellParametersCount::parameterValue count: "+ parameterValues.length);
	  LOG.debug("getCellParametersCount::objectIds count: "+ objectIds.length);
	  
	  _topoItf.getCountParameters(objectTypeInt, objectIds, paramName, parameterValues, output);
  }
  
  private void getCountParameterList(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String objectType = request.getParameter(PARAM_OBJECT_TYPE);
	  String[] objectIds = ParameterUtilities.splitListOfValues(request.getParameter(PARAM_OBJECT_IDS));	  
	  
	  List<ParameterWithValues> parametersValuesMap = ParameterUtilities.parseListOfParameterValues(request.getParameter(PARAM_PARAMETER_LIST_VALUES));

	  int objectTypeInt = Integer.parseInt(objectType);
	  
	  LOG.debug("getCountParameterList::objectIds count: "+ objectIds.length);
	  
	  _topoItf.getCountParameterList(objectTypeInt, objectIds, parametersValuesMap, output);
  }
  
  
   
  private void getSiteImageList(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String siteId = request.getParameter(PARAM_IMAGE_SITE_ID);
	  String[] listOfImages = SiteImageManager.getInstance().imagesForSite(siteId);
	  
	  try {
		  JSONObject json = new JSONObject();
		  json.put("images", listOfImages);
		  JSONUtilities.sendJSONObject(json, output);
	  } catch (Exception ex) {
		  LOG.error(ex);
	  }
  }

  private void getSiteImage(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String quality = request.getParameter(PARAM_IMAGE_QUALITY);
	  String siteId = request.getParameter(PARAM_IMAGE_SITE_ID);
	  String imageName = request.getParameter(PARAM_IMAGE_NAME);
	  
	  byte[] theImage = SiteImageManager.getInstance().imageForSite(siteId, imageName, quality);
	  JSONUtilities.sendImageWithJSON(theImage, output);
  }

  private void getDefaultSiteImage(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String quality = request.getParameter(PARAM_IMAGE_QUALITY);
	  String siteId = request.getParameter(PARAM_IMAGE_SITE_ID);
	  
	  byte[] theImage = SiteImageManager.getInstance().defaultImageForSite(siteId, quality);
	  JSONUtilities.sendImageWithJSON(theImage, output);
  }
 
  private static final int RESULT_OK = 0;
  private static final int RESULT_ERROR = 1;
  private static final int RESULT_NO_AUTORIZATION = 2; 
  private static final String JSON_RESULT_PARAM = "Result";
  
  private void deleteSiteImage(HttpServletRequest request, OutputStream output, UserDescription user) {
	  JSONObject json = new JSONObject();
	  int theResult = RESULT_NO_AUTORIZATION;
	  if (user.isAdmin()) {
		  String siteId = request.getParameter(PARAM_IMAGE_SITE_ID);
		  String imageName = request.getParameter(PARAM_IMAGE_NAME);

		 boolean result = SiteImageManager.getInstance().deleteImageForSite(siteId, imageName);
		 theResult = result ? RESULT_OK : RESULT_ERROR;  
	  }
	  
	  json.put(JSON_RESULT_PARAM, theResult);
	  JSONUtilities.sendJSONObject(json, output);
  }

  private void getCellAlarms(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String cellId = request.getParameter(PARAM_ID);
	  
	  iMonServerProperties instanceProperties = iMonServerProperties.getInstance();
	  DatasourceTopology DS = Utilities.getDatasourceForCell(cellId, instanceProperties.getNetworkDataBaseName());

	  try {
		  JSONArray newArray  = new JSONArray();
		  int maxAlarms = AlarmSimulator.generateRandomInteger(10,40);
		  for (int i = 0 ; i < maxAlarms; i++) {
			  Alarm currentAlarm = AlarmSimulator.getRandomAlarm(i*3);
			  newArray.add(currentAlarm);
		  }

		  JSONUtilities.sendJSONObject(newArray, output);
	  }
	  catch (Exception ex) {
		  LOG.error(ex);
	  }
  }
  
  /**
   *  Called when Client App requests the list of cells starting with a given sequence of characters
   * 
   * @param request used to extract parameters of the request (string pattern, max number of cells in the response, ...)
   * @param output to send the list of KPIs values per cell to the client App
   * @param user Originator of the request
   */
  private void getCellsStartingWith(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String matching = request.getParameter(PARAM_MATCHING);
	  String maxResults = request.getParameter(PARAM_MAX_RESULTS);
	  String techno = request.getParameter(PARAM_TECHNOLOGY);
	  int intMaxResults = Integer.parseInt(maxResults);

	  _topoItf.getCellsStartingWith(matching, techno, intMaxResults, output, user);
  }
  
  private void addUser(HttpServletRequest request, OutputStream output, UserDescription user) {
	  addOrUpdateUser(request,output,user, true);
  }
  
  private void updateUser(HttpServletRequest request, OutputStream output, UserDescription user) {
	  addOrUpdateUser(request,output,user, false);
  }
  
  private void addOrUpdateUser(HttpServletRequest request, OutputStream output, UserDescription user, boolean addUser) {
	  String userName = request.getParameter(PARAM_ADD_USER_USER_NAME);
	  String userPassword = request.getParameter(PARAM_ADD_USER_PASSWORD);
	  String userDescription = request.getParameter(PARAM_ADD_USER_DESCRIPTION);
	  String isAdminString = request.getParameter(PARAM_ADD_USER_IS_ADMIN);

	  String firstName = request.getParameter(PARAM_ADD_USER_FIRST_NAME);
	  String lastName = request.getParameter(PARAM_ADD_USER_LAST_NAME);
	  String EMail = request.getParameter(PARAM_ADD_USER_EMAIL);


	  boolean isAdmin = Utilities.isUserAdmin(isAdminString);

	  UserDescription newUser = new UserDescription(userName, userPassword, userDescription, isAdmin, true);
	  newUser.updateUserInfos(firstName, lastName, EMail);

	  if (addUser == true) {
		  _userManagement.addUser(newUser, output);
	  } else {
		  newUser.setInvalidConnectionDate(); // To not reset it!
		  _userManagement.updateUser(newUser, output);
	  }
  }
  

  private void deleteUser(HttpServletRequest request, OutputStream output, UserDescription user) {
	  String userName = request.getParameter(PARAM_DELETE_USER_USER_NAME);
	  
	  _userManagement.deleteUser(userName, output);
  }
  
  /**
   *  Called when Client App requests information about the iMonserver data
   * 
   * @param response to send the list of KPIs values per cell to the client App
   * @param isZipEncoding True when Client App except to receive JSON data in Zip file otherwise raw data is returned (JSON)
   */
   private void getAbout(OutputStream output) {
	   _topoItf.getAbout(output);
  }
  
   /**
    *  Called when Client App requests the list of zone
    * 
    * @param output to send the list of KPIs values per cell to the client App
    */
  private void getZoneList(OutputStream output) {
	  JSONArray newArray = new JSONArray();
	  Collection<Zone> zones = _zones.values();
	  for (Zone currZone : zones) {
		  JSONObject newObject = JSONObject.fromObject(currZone);
		  newArray.add(newObject);

	  }

	  JSONUtilities.sendJSONObject(newArray, output);
 }
  
 private OutputStream getOutputStreamFromRequest(HttpServletRequest request, HttpServletResponse response) {
	  String zipEncoding = request.getParameter(PARAM_ZIP);
	  if (zipEncoding == null) {
		  zipEncoding ="false";
	  }
	  boolean isZipEncoding = Boolean.parseBoolean(zipEncoding);

	  String zipName = null;
	  if (isZipEncoding) {
		  zipName = request.getParameter(PARAM_ZIP_NAME);
		  if (zipName == null) {
			  zipName = "dummy name";
		  }
	  }
	  
	  OutputStream stream = getOutputStream(response, isZipEncoding, zipName); 
	  return stream;
 }
	
/**
 * Return either a ZipOuputStream or a standard outputStream depending on input parameters
 * 
 * @param response Output stream from the request
 * @param isZipEncoding True when a Zipped Output Stream is needed else False
 * @param zipName name of the zip file that will be created
 * @return Either a ZipOutputStream or a standard OutputStream
 */
private OutputStream getOutputStream(HttpServletResponse response, boolean isZipEncoding, String zipName) {
	  try {
		  OutputStream output = response.getOutputStream();
		  if (isZipEncoding) {
			  response.setHeader("Content-Encoding", "x-compress");

			  output = new ZipOutputStream(output);
			  ((ZipOutputStream)output).putNextEntry(new ZipEntry(zipName));
		  }
		  return output;
	  } 
	  catch (Exception ex) {
		  LOG.error(ex);
	  }
	  return null;
}
	
private void manageUploadRequest(HttpServletRequest request,  HttpServletResponse response) {
	LOG.debug("manageUploadRequest::It's a multipart content ");
	Date startDate = new Date();

	// Create a factory for disk-based file items
	DiskFileItemFactory factory = new DiskFileItemFactory();

	// Configure a repository (to ensure a secure temp location is used)
	ServletContext servletContext = this.getServletConfig().getServletContext();
	File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
	factory.setRepository(repository);

	// Create a new file upload handler
	ServletFileUpload upload = new ServletFileUpload(factory);

	// Parse the request
	try {
		List<FileItem> items = upload.parseRequest(request);
		if (items.size() < 2) {
			LOG.warn("manageUploadRequest::Warning, multipart has less than 2 parts, it's an error, ignore the request");
			return;
		} else if (items.size() > 2) {
			LOG.warn("manageUploadRequest::Warning, multipart has more than 2 parts, parse the request to check the content");				
		}
		// Process the uploaded items
		Iterator<FileItem> iter = items.iterator();

		String parametersString = null;
		FileItem theImageItem = null;

		while (iter.hasNext()) {
			FileItem item = iter.next();

			if (item.isFormField()) {
				if ("Parameters".equals(item.getFieldName())) {
					LOG.info("manageUploadRequest::Parameters has been found");
					parametersString = item.getString();
				}
			} else {
				theImageItem = item;
				
				// Not used.
				String fieldName = item.getFieldName();
				String fileName = item.getName();
				String contentType = item.getContentType();
				boolean isInMemory = item.isInMemory();
				long sizeInBytes = item.getSize();
				LOG.debug("manageUploadRequest::is File fieldName: " + fieldName + " fileName: " + fileName + " ContentType: " + contentType + " inMemory: " + isInMemory + " size: " + sizeInBytes);
			}
		}
		if (parametersString == null) {
			LOG.warn("manageUploadRequest::Parameters has not been found");
			return;
		}
		
		Map<String, String> parametersValue = Utilities.parseMultipartParameters(parametersString);

		String method = parametersValue.get(PARAM_METHOD_NAME);

		OutputStream stream = getOutputStreamFromRequest(request, response);
		if (stream == null) {
			return;
		}

		String deviceType = parametersValue.get(PARAM_DEVICE_TYPE);
		LOG.info("manageUploadRequest::(" + Utilities.getDeviceName(deviceType) + ") Start manageUploadRequest for " + method);

		// Check user credentials
		String userName = parametersValue.get(PARAM_USER_NAME);
		String userPassword = parametersValue.get(PARAM_USER_PASSWORD);

		UserDescription user = checkUserCredentials(userName, userPassword);
		if (user == null) {
			LOG.info("manageUploadRequest::(" + Utilities.getDeviceName(deviceType) + ") abort request, user is unknown ");
			return;
		}

		if ((userName != null) && (method != null)) {
			LOG.info("Method: " + method + " called by " + userName);
		}

		if (method.equals(METHOD_ADD_IMAGE)) {
			String siteId = parametersValue.get(PARAM_IMAGE_SITE_ID);
			boolean status = SiteImageManager.getInstance().saveImageForSite(siteId, theImageItem);
			
			JSONObject newObject = new JSONObject();
			newObject.put("Status", status);	
			JSONUtilities.sendJSONObject(newObject, stream);

		} else {
			LOG.warn("manageUploadRequest::unknown method called " + method);			
		}
		
		LOG.info("manageUploadRequest::End manageUploadRequest for " + method + TraceUtility.duration(startDate));

	} catch (Exception ex) {
		LOG.error(ex);
	}


	return;
}
 
/**
 * Main entry point for all Client App request
 * 
 * @param request request sent by the Client App
 * @param response used to send result to the Client App
 */
  @Override
  public void doGet(HttpServletRequest request,  HttpServletResponse response)  throws ServletException,IOException{
	  
		if (ServletFileUpload.isMultipartContent(request)) {
			manageUploadRequest(request, response);
			return;
		} 
	  
	  Date startDate = new Date();
	  String method = request.getParameter(PARAM_METHOD_NAME);
	  if (method == null) {
		  OutputStream output = response.getOutputStream();
		  output.close();
	  }
	  
	  OutputStream stream = getOutputStreamFromRequest(request, response);
	  if (stream == null) {
		  return;
	  }
	  

	  String deviceType = request.getParameter(PARAM_DEVICE_TYPE);
	  LOG.info("doGet::(" + Utilities.getDeviceName(deviceType) + " / " + request.getProtocol() +") Start doGet for " + method);

	  // Check user credentials
	  String userName = request.getParameter(PARAM_USER_NAME);
	  String userPassword = request.getParameter(PARAM_USER_PASSWORD);
	  
	  UserDescription user = null;
	  if (method.equals(METHOD_CONNECT)) {
		  _userManagement.connect(userName, userPassword, stream);
		  LOG.info("doGet::End doGet for " + method + TraceUtility.duration(startDate));
		  return;
	  } else {
		  user = checkUserCredentials(userName, userPassword);
		  if (user == null) {
			  return;
		  }
	  }
	  
	  if ((userName != null) && (method != null)) {
		  LOG.info("Method: " + method + " called by " + userName);
	  }
	  
	   if (method.equals(METHOD_CELLS_AROUND_POSITION)) {
		   getCellsAroundPosition(request, stream, user);
	   } else if (method.equals(METHOD_CELLS_AROUND_ROUTE)) {
		   getCellsAroundRoute(request, stream, user);
	  } else if (method.equals(METHOD_GET_CELLS_STARTING_WITH)) {
		  getCellsStartingWith(request, stream, user);
	  } else if (method.equals(METHOD_GET_CELLS)) {
		  getCells(request, stream, user);
	  } else if (method.equals(METHOD_GET_CELL_KPIS)) {
		  getCellKPIs(request, stream, user);
	  } else if (method.equals(METHOD_GET_KPIS_FOR_CELLS)) {
		  getKPIsForCells(request, stream, user);
	  } else if (method.equals(METHOD_GET_KPI_DICTIONARIES)) {
		  getKPIDictionaries(stream);	  
	  } else if (method.equals(METHOD_ABOUT)) {
		  getAbout(stream);
	  } else if (method.equals(METHOD_CELL_WITH_NEIGHBORS)) {
		  getCellWithNeighbors(request, stream, user);
	  } else if (method.equals(METHOD_CELL_WITH_NEIGHBORS_HISTORICAL)) {
		  getCellWithNeighborsHistorical(request, stream, user);
	  } else if (method.equals(METHOD_CHANGE_PASSWORD)) {
		  String oldPassword = request.getParameter(PARAM_USER_OLD_PASSWORD);		  
		  String newPassword = request.getParameter(PARAM_USER_NEW_PASSWORD);		  
		  _userManagement.changePassword(userName, oldPassword, newPassword, stream);		  
	  } else if (method.equals(METHOD_GET_USERS)) {
		  _userManagement.getUsers(userName, stream);		  
	  } else if (method.equals(METHOD_ADD_USER)) {
		  addUser(request, stream, user);		  
	  } else if (method.equals(METHOD_UPDATE_USER)) {
		  updateUser(request, stream, user);		  
	  } else if (method.equals(METHOD_DELETE_USER)) {
		  deleteUser(request, stream, user);		  
	  } else if (method.equals(METHOD_GET_ZONE_LIST)) {
		  getZoneList(stream);		  
	  } else if (method.equals(METHOD_GET_CELLS_OF_ZONE)) {
		  getCellsOfZone(request, stream, user);		  
	  } else if (method.equals(METHOD_GET_CELL_PARAMETERS)) {
		  getCellParameters(request, stream, user);
	  } else if (method.equals(METHOD_GET_CELL_PARAMETERS_HISTORICAL)) {
		  getCellParametersHistory(request, stream, user);
	  } else if (method.equals(METHOD_GET_CELL_ALARMS)) {
		  getCellAlarms(request, stream, user);
	  } else if (method.equals(METHOD_GET_COUNT_PARAMETER)) {
		  getCountParameters(request, stream, user);
	  } else if (method.equals(METHOD_GET_COUNT_PARAMETER_LIST)) {
		  getCountParameterList(request, stream, user);
	  } else if (method.equals(METHOD_GET_SITE_IMAGE_LIST)) {
		 getSiteImageList(request, stream, user);
	  } else if (method.equals(METHOD_GET_DEFAULT_SITE_IMAGE)) {
		 getDefaultSiteImage(request, stream, user);
	  } else if (method.equals(METHOD_GET_SITE_IMAGE)) {
		 getSiteImage(request, stream, user);
	  } else if (method.equals(METHOD_DELETE_SITE_IMAGE)) {
		 deleteSiteImage(request, stream, user);
	  } else {
		  LOG.warn("doGet::Uknown request: " + method);
	  }

	  LOG.info("doGet::End doGet for " + method + TraceUtility.duration(startDate));
  }
  
   
  
  private UserDescription checkUserCredentials(String userName, String userPassword) {
	  UserDescription user = UserManagementHelper.getInstance().getUser(userName);
	  if (user == null) {
		  LOG.warn("checkUserCredentials", "Uknown user: " + userName);
		  return null;
	  }

	  if (_userManagement.isAuthenticatedUser(userName, userPassword) == false) {
		  return null;
	  }
	  
	  return user;
  }


  /**
   * 
   * Called by the ClientApp to get the KPI dictionary in JSON or XML format
   * 
   * @param request client App request
   * @param response to send KPI dictionary to the client App
   * @param isZipEncoding True when client App except the data in zipped format otherwise raw data is returned
   */
  private void getKPIDictionaries(OutputStream output) {
	  Utilities.getKPIDictionarieswithJSON(output);
  }
  

 
}