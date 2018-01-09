package com.seb.imonserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.generic.PropertyUtility;



public  class iMonServerProperties {
	
	private static final Logger LOG = LogManager.getLogger(iMonServerProperties.class);


	private  final String PROP_KPI_DICTIONARY = "KPIDictionary";
	private  final String PROP_NETWORK_DATABASE_DIRECTORY = "NetworkDatabaseDirectory";
	private  final String PROP_SITE_IMAGE_DATABASE_DIRECTORY = "SiteImageDatabaseDirectory";
	private  final String PROP_NETWORK_DATABASE = "NetworkDatabase";
	private  final String PROP_KPI_PLUG = "KPIPlug";
	private  final String PROP_LTE_NPO_IP = "LTE_NPO_IP";
	private  final String PROP_LTE_EQL_PORT = "LTE_EQL_PORT";
	private  final String PROP_LTE_EQL_USER_NAME = "LTE_EQL_UserName";
	private  final String PROP_LTE_EQL_PASSWORD = "LTE_EQL_Password";
	private  final String PROP_WCDMA_NPO_IP = "WCDMA_NPO_IP";
	private  final String PROP_WCDMA_EQL_PORT = "WCDMA_EQL_PORT";
	private  final String PROP_WCDMA_EQL_USER_NAME = "WCDMA_EQL_UserName";
	private  final String PROP_WCDMA_EQL_PASSWORD = "WCDMA_EQL_Password";
	private  final String PROP_GSM_NPO_IP = "GSM_NPO_IP";
	private  final String PROP_GSM_EQL_PORT = "GSM_EQL_PORT";
	private  final String PROP_GSM_EQL_USER_NAME = "GSM_EQL_UserName";
	private  final String PROP_GSM_EQL_PASSWORD = "GSM_EQL_Password";
	private  final String PROP_LOG_FILE = "LOG_FILE";
	private  final String PROP_DATASOURCE_FILE = "DATASOURCE_FILE";
	private  final String PROP_ZONE_FILE = "ZONE_FILE";
	private  final String PROP_KPI_SIMU = "KPI_SIMU";
	private  final String PROP_FIND_CELLS_BY_GEO_INDEX = "FindCellsByGeoIndex";

	static private final String PROP_DATASOURCE_DATABASE ="datasourceDatabase";
	static private final String PROP_USERS_DATABASE ="usersDatabase";
	
	private  final String PROP_WEB_SERVER_PROPERTY_FILE = "WebServerPropertyFile";



	// read from the property file
	private String _NetworkDataBaseName;
	private String _NetworkDataBaseDirectory;
	private String _SiteImageDatabaseDirectory;
	private String _KPIDictionaryName;

	private boolean _isEQLPlugIn;
	private String _LTE_NPO_IP;
	private int _LTE_EQL_PORT;
	private String _LTE_EQL_UserName;
	private String _LTE_EQL_Password;

	private String _WCDMA_NPO_IP;
	private int _WCDMA_EQL_PORT;
	private String _WCDMA_EQL_UserName;
	private String _WCDMA_EQL_Password;

	private String _GSM_NPO_IP;
	private int _GSM_EQL_PORT;
	private String _GSM_EQL_UserName;
	private String _GSM_EQL_Password;

	private String _logFileName;
	private String _datasourceFileName;
	private String _zonesFileName;

	private String _KPISimuFileName;
	
	private String _datasourceDatabaseName;
	
	private String _usersDatabaseName;
	
	private boolean _isFindCellsByGeoIndex = true;
	
	private String _webServerPropertyFile;
	
	public String getNetworkDataBaseName() {
		return _NetworkDataBaseName;
	}
	
	public String getNetworkDataBaseDirectory() {
		return _NetworkDataBaseDirectory;
	}
	
	public String getSiteImageDatabaseDirectory() {
		return _SiteImageDatabaseDirectory;
	}

	public String getKPIDictionaryName() {
		return _KPIDictionaryName;
	}

	public boolean getIsEQLPlugIn() {
		return _isEQLPlugIn;
	}
	
	public boolean getIsFindCellsByGeoIndex() {
		return _isFindCellsByGeoIndex;
	}


	public String getLTE_NPO_IP() {
		return _LTE_NPO_IP;
	}

	public int getLTE_EQL_PORT() {
		return _LTE_EQL_PORT;
	}

	public String getLTE_EQL_UserName() {
		return _LTE_EQL_UserName;
	}

	public String getLTE_EQL_Password() {
		return _LTE_EQL_Password;
	}

	public String getWCDMA_NPO_IP() {
		return _WCDMA_NPO_IP;
	}

	public int getWCDMA_EQL_PORT() {
		return _WCDMA_EQL_PORT;
	}

	public String getWCDMA_EQL_UserName() {
		return _WCDMA_EQL_UserName;
	}

	public String getWCDMA_EQL_Password() {
		return _WCDMA_EQL_Password;
	}

	public String getGSM_NPO_IP() {
		return _GSM_NPO_IP;
	}

	public int getGSM_EQL_PORT() {
		return _GSM_EQL_PORT;
	}

	public String getGSM_EQL_UserName() {
		return _GSM_EQL_UserName;
	}

	public String getGSM_EQL_Password() {
		return _GSM_EQL_Password;
	}

	public String getLogFileName() {
		return _logFileName;
	}

	public String getDatasourceFileName() {
		return _datasourceFileName;
	}

	public String getZonesFileName() {
		return _zonesFileName;
	}

	public String getKPISimuFileName() {
		return _KPISimuFileName;
	}
	
	public String getDatasourceDatabaseName() {
		return _datasourceDatabaseName;
	}

	public String getUsersDatabaseName() {
		return _usersDatabaseName;
	}
	
	public String getWebServerPropertyFile() {
		return _webServerPropertyFile;
	}

	private iMonServerProperties() {	
	}

	private static iMonServerProperties INSTANCE = new iMonServerProperties();

	public static iMonServerProperties getInstance() {
		return INSTANCE;
	}

	public void initialize(String propertyFileName) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertyFileName));
		} catch (IOException e) {
			LOG.fatal("Cannot open the property file", e);
			System.exit(1);
		}

		_KPIDictionaryName = PropertyUtility.getStringProperties(properties, PROP_KPI_DICTIONARY);
		_NetworkDataBaseName = PropertyUtility.getStringProperties(properties, PROP_NETWORK_DATABASE);
		_NetworkDataBaseDirectory = PropertyUtility.getStringProperties(properties, PROP_NETWORK_DATABASE_DIRECTORY);
		_SiteImageDatabaseDirectory = PropertyUtility.getStringProperties(properties, PROP_SITE_IMAGE_DATABASE_DIRECTORY);
		_isEQLPlugIn = PropertyUtility.getBooleanProperties(properties, PROP_KPI_PLUG);
		_LTE_NPO_IP = PropertyUtility.getStringProperties(properties, PROP_LTE_NPO_IP);
		_LTE_EQL_PORT = PropertyUtility.getIntProperties(properties, PROP_LTE_EQL_PORT);
		_LTE_EQL_UserName = PropertyUtility.getStringProperties(properties, PROP_LTE_EQL_USER_NAME);
		_LTE_EQL_Password = PropertyUtility.getStringProperties(properties, PROP_LTE_EQL_PASSWORD);

		_WCDMA_NPO_IP = PropertyUtility.getStringProperties(properties, PROP_WCDMA_NPO_IP);
		_WCDMA_EQL_PORT = PropertyUtility.getIntProperties(properties, PROP_WCDMA_EQL_PORT);
		_WCDMA_EQL_UserName = PropertyUtility.getStringProperties(properties, PROP_WCDMA_EQL_USER_NAME);
		_WCDMA_EQL_Password = PropertyUtility.getStringProperties(properties, PROP_WCDMA_EQL_PASSWORD);

		_GSM_NPO_IP = PropertyUtility.getStringProperties(properties, PROP_GSM_NPO_IP);
		_GSM_EQL_PORT = PropertyUtility.getIntProperties(properties, PROP_GSM_EQL_PORT);
		_GSM_EQL_UserName = PropertyUtility.getStringProperties(properties, PROP_GSM_EQL_USER_NAME);
		_GSM_EQL_Password = PropertyUtility.getStringProperties(properties, PROP_GSM_EQL_PASSWORD);

		_logFileName = PropertyUtility.getStringProperties(properties, PROP_LOG_FILE);
		_datasourceFileName = PropertyUtility.getStringProperties(properties, PROP_DATASOURCE_FILE);
		_zonesFileName = PropertyUtility.getStringProperties(properties, PROP_ZONE_FILE);
		_KPISimuFileName = PropertyUtility.getStringProperties(properties, PROP_KPI_SIMU);
		_datasourceDatabaseName = PropertyUtility.getStringProperties(properties, PROP_DATASOURCE_DATABASE);
		_usersDatabaseName = PropertyUtility.getStringProperties(properties, PROP_USERS_DATABASE);
		
		_isFindCellsByGeoIndex = PropertyUtility.getBooleanProperties(properties, PROP_FIND_CELLS_BY_GEO_INDEX);
		
		_webServerPropertyFile = PropertyUtility.getStringProperties(properties, PROP_WEB_SERVER_PROPERTY_FILE);
	}



}
