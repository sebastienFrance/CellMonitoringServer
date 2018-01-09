package com.seb.networkGenerator;

import java.util.Properties;

import com.seb.networkTopology.BasicProperties;
import com.seb.networkTopology.generic.PropertyUtility;

public class NetworkGeneratorProperties extends BasicProperties {
	private static String _KMLFileName = null;
	private static String _NodeSourceFileName = null;
	private static String _OpenCellIdsFileName = null;
	private static int _OpenCellIdsFileVersion = 0;
	private static int _OpenCellIdsMaxCellsPerSite = 1;
	private static String _OpenCellIdsCellPrefix = null;
	private static String _NodeOpenCellIdsSourceFileName = null;
	
	private static String[] _LTEFrequencies = null;
	private static String[] _WCDMAFrequencies = null;
	private static String[] _GSMFrequencies = null;

	private static String[] _LTEReleases = null;
	private static String[] _WCDMAReleases = null;
	private static String[] _GSMReleases = null;
	
	private static int _LTEPercentageMeasuredByANR;

	private static boolean _relocate = false;
	private static double _minLatitude;
	private static double _maxLatitude;
	private static double _minLongitude;
	private static double _maxLongitude;
	private static boolean _hasMinMaxLatLong = true;
	
	private static int _maxDistanceLTENeighbors = 12000;
	private static int _maxDistanceWCDMANeighbors = 10000;
	private static int _maxDistanceGSMNeighbors = 8000;

	private static int _maxIntraRATNR = 48;
	private static int _maxInterRATNR = 48;

	
	private static int _maxAttributesPerSection = 10;
	private static String[] _attrSectionsName = null;
	
	private static String _zoneFileName = null;
	private static int _numberOfZones = 10;
	private static int _zoneMaxCellDistance = 1000; // in meters
	private static int _zoneMaxCellsPerZone = 1000;


	
	private static String _cartoRadioSupportsFileName = null;
	private static String _cartoRadioGeoCoordsFileName = null;
	
	static private final String PROP_KML_FILENAME = "KMLFileName";
	static private final String PROP_NODE_SOURCE_FILENAME ="NodeSourceFileName";
	static private final String PROP_NODE_OPEN_CELL_IDS_SOURCE_FILENAME ="NodeOpenCellIdsSourceFileName";
	static private final String PROP_OPEN_CELL_IDS_FILENAME ="OpenCellIdsFileName";
	static private final String PROP_OPEN_CELL_IDS_FILE_VERSION ="OpenCellIdsFileVersion";
	static private final String PROP_OPEN_CELL_IDS_MAX_CELLS_PER_SITE ="OpenCellIdsMaxCellsPerSite";
	static private final String PROP_OPEN_CELL_IDS_CELL_PREFIX ="OpenCellIdsCellPrefix";

	static private final String PROP_RELOCATE ="relocate";
	static private final String PROP_MIN_LATITUDE ="minLatitude";
	static private final String PROP_MAX_LATITUDE ="maxLatitude";
	static private final String PROP_MIN_LONGITUDE ="minLongitude";
	static private final String PROP_MAX_LONGITUDE ="maxLongitude";
	static private final String PROP_LTE_FREQUENCIES = "LTEFrequencies";
	static private final String PROP_WCDMA_FREQUENCIES = "WCDMAFrequencies";
	static private final String PROP_GSM_FREQUENCIES = "GSMFrequencies";
	static private final String PROP_LTE_RELEASES = "LTEReleases";
	static private final String PROP_WCDMA_RELEASES = "WCDMAReleases";
	static private final String PROP_GSM_RELEASES = "GSMReleases";
	static private final String PROP_MAX_DISTANCE_LTE_NEIGHBORS ="MaxDistanceForLTENeighborsInMeters";
	static private final String PROP_MAX_DISTANCE_WCDMA_NEIGHBORS ="MaxDistanceForWCDMANeighborsInMeters";
	static private final String PROP_MAX_DISTANCE_GSM_NEIGHBORS ="MaxDistanceForGSMNeighborsInMeters";
	static private final String PROP_MAX_INTRA_RAT_NR ="MaxNumberIntraRATNR";
	static private final String PROP_MAX_INTER_RAT_NR ="MaxNumberInterRATNR";
	static private final String PROP_LTE_PERCENTAGE_MEASURED_BY_ANR = "LTEPercentageMeasuredByANR";
	static private final String PROP_MAX_ATTRIBUTES_PER_SECTION = "MaxAttributesPerSection";
	static private final String PROP_ATTRIBUTES_SECTIONS_NAME = "AttributesSectionsName";
	
	static private final String PROP_ZONE_FILENAME = "ZoneFileName";
	static private final String PROP_ZONE_NUMBER_ZONE = "NumberOfZonesPerTechno";
	static private final String PROP_ZONE_MAX_CELL_DISTANCE = "ZoneCellMaxDistance";
	static private final String PROP_ZONE_MAX_CELLS_PER_ZONE ="ZoneMaxCellsPerZone";
	
	static private final String PROP_CARTORADIO_SUPPORTS = "CartoRadioSupportFileName";
	static private final String PROP_CARTORADIO_GEO_COORDS = "CartoRadioGeoCoordstFileName";
	
	
		
	static public String getKMLFileName() {
		return _KMLFileName;
	}
	
	static public String getNodeSourceFileName() {
		return _NodeSourceFileName;
	}

	static public String getOpenCellIdsNodeSourceFileName() {
		return _NodeOpenCellIdsSourceFileName;
	}

	static public String getOpenCellIdsFileName() {
		return _OpenCellIdsFileName;
	}

	static public int getOpenCellIdsFileVersion() {
		return _OpenCellIdsFileVersion;
	}

	static public int getOpenCellIdsMaxCellsPerSite() {
		return _OpenCellIdsMaxCellsPerSite;
	}
	static public String getOpenCellIdsCellPrefix() {
		return _OpenCellIdsCellPrefix;
	}

	static public String[] getLTEFrequencies() {
		return _LTEFrequencies;
	}
	static public String[] getWCDMAFrequencies() {
		return _WCDMAFrequencies;
	}
	static public String[] getGSMFrequencies() {
		return _GSMFrequencies;
	}
	static public String[] getLTEReleases() {
		return _LTEReleases;
	}
	static public String[] getWCDMAReleases() {
		return _WCDMAReleases;
	}
	static public String[] getGSMReleases() {
		return _GSMReleases;
	}
	
	static public int getMaxDistanceLTENeighborsInMeters() {
		return _maxDistanceLTENeighbors;
	}
	static public int getMaxDistanceWCDMANeighborsInMeters() {
		return _maxDistanceWCDMANeighbors;
	}
	static public int getMaxDistanceGSMNeighborsInMeters() {
		return _maxDistanceGSMNeighbors;
	}

	static public int getMaxNumberIntraRATNR() {
		return _maxIntraRATNR;
	}

	static public int getMaxNumberInterRATNR() {
		return _maxInterRATNR;
	}

	static public int getMaxAttributesPerSection() {
		return _maxAttributesPerSection;
	}
	
	static public String[] getAttributesSectionsName() {
		return _attrSectionsName;
	}
	
	static public boolean isRelocate() {
		return _relocate;
	}
	
	static public double getMinLatitude() {
		return _minLatitude;
	}
	static public double getMaxLatitude() {
		return _maxLatitude;
	}
	static public double getMinLongitude() {
		return _minLongitude;
	}
	static public double getMaxLongitude() {
		return _maxLongitude;
	}
	static public boolean hasMinMaxLatLong() {
		return _hasMinMaxLatLong;
	}

	static public int getLTEPercentageMeasuredByANR() {
		return _LTEPercentageMeasuredByANR;
	}

	static public String getZoneFileName() {
		return _zoneFileName;
	}

	static public int getNumberOfZones() {
		return _numberOfZones;
	}
	static public int getZoneMaxCellDistance() {
		return _zoneMaxCellDistance;
	}
	static public int getZoneMaxCellsPerZone() {
		return _zoneMaxCellsPerZone;
	}

	static public String getCartoRadioSupportsFileName() {
		return _cartoRadioSupportsFileName;
	}
	static public String getCartoRadioGeoCoordsFileName() {
		return _cartoRadioGeoCoordsFileName;
	}

	
	private NetworkGeneratorProperties() {	
	}
	
	private static NetworkGeneratorProperties INSTANCE = new NetworkGeneratorProperties();
	
	public static NetworkGeneratorProperties getInstance() {
		return INSTANCE;
	}

	@Override
	protected  void getSpecificProperties(Properties properties) {
		_KMLFileName = PropertyUtility.getStringProperties(properties, PROP_KML_FILENAME);
		
		getObjectCellIdsProperties(properties);
		getMixMaxCoordinates(properties);
		getRadioConfigurationProperties(properties);
		getZoneProperties(properties);
		getCartoRadioProperties(properties);
	}
	
	private void getMixMaxCoordinates(Properties properties) {
		_minLatitude = PropertyUtility.getDoubleProperties(properties, PROP_MIN_LATITUDE);
		_maxLatitude = PropertyUtility.getDoubleProperties(properties, PROP_MAX_LATITUDE);
		_minLongitude = PropertyUtility.getDoubleProperties(properties, PROP_MIN_LONGITUDE);
		_maxLongitude = PropertyUtility.getDoubleProperties(properties, PROP_MAX_LONGITUDE);

		if ((_minLatitude == 0) || (_maxLatitude == 0) || (_minLongitude == 0) || (_maxLongitude == 0)) {
			_hasMinMaxLatLong = false;
		}
	}
	
	private void getObjectCellIdsProperties(Properties properties) {
		_NodeOpenCellIdsSourceFileName = PropertyUtility.getStringProperties(properties, PROP_NODE_OPEN_CELL_IDS_SOURCE_FILENAME);
		_OpenCellIdsFileName = PropertyUtility.getStringProperties(properties, PROP_OPEN_CELL_IDS_FILENAME); 
		_OpenCellIdsFileVersion = PropertyUtility.getIntProperties(properties, PROP_OPEN_CELL_IDS_FILE_VERSION); 
		_OpenCellIdsMaxCellsPerSite = PropertyUtility.getIntProperties(properties, PROP_OPEN_CELL_IDS_MAX_CELLS_PER_SITE);
		_OpenCellIdsCellPrefix = PropertyUtility.getStringProperties(properties, PROP_OPEN_CELL_IDS_CELL_PREFIX); 
	}
	
	private void getZoneProperties(Properties properties) {
		_zoneFileName = PropertyUtility.getStringProperties(properties, PROP_ZONE_FILENAME);
		_numberOfZones = PropertyUtility.getIntProperties(properties, PROP_ZONE_NUMBER_ZONE);
		_zoneMaxCellDistance = PropertyUtility.getIntProperties(properties, PROP_ZONE_MAX_CELL_DISTANCE);
		_zoneMaxCellsPerZone = PropertyUtility.getIntProperties(properties, PROP_ZONE_MAX_CELLS_PER_ZONE);		
	}
	
	private void getCartoRadioProperties(Properties properties) {
		_NodeSourceFileName = PropertyUtility.getStringProperties(properties, PROP_NODE_SOURCE_FILENAME);
		_cartoRadioSupportsFileName = PropertyUtility.getStringProperties(properties, PROP_CARTORADIO_SUPPORTS);
		_cartoRadioGeoCoordsFileName = PropertyUtility.getStringProperties(properties, PROP_CARTORADIO_GEO_COORDS);
	}
	
	private void getRadioConfigurationProperties(Properties properties) {
		_LTEFrequencies = PropertyUtility.getStringArrayProperties(properties, PROP_LTE_FREQUENCIES);
		_WCDMAFrequencies = PropertyUtility.getStringArrayProperties(properties, PROP_WCDMA_FREQUENCIES);
		_GSMFrequencies = PropertyUtility.getStringArrayProperties(properties, PROP_GSM_FREQUENCIES);

		_LTEReleases =  PropertyUtility.getStringArrayProperties(properties, PROP_LTE_RELEASES);
		_WCDMAReleases =  PropertyUtility.getStringArrayProperties(properties, PROP_WCDMA_RELEASES);
		_GSMReleases =  PropertyUtility.getStringArrayProperties(properties, PROP_GSM_RELEASES);
		_maxDistanceLTENeighbors = PropertyUtility.getIntProperties(properties, PROP_MAX_DISTANCE_LTE_NEIGHBORS);
		_maxDistanceWCDMANeighbors = PropertyUtility.getIntProperties(properties, PROP_MAX_DISTANCE_WCDMA_NEIGHBORS);
		_maxDistanceGSMNeighbors = PropertyUtility.getIntProperties(properties, PROP_MAX_DISTANCE_GSM_NEIGHBORS);

		_maxIntraRATNR = PropertyUtility.getIntProperties(properties, PROP_MAX_INTRA_RAT_NR);
		_maxInterRATNR = PropertyUtility.getIntProperties(properties, PROP_MAX_INTER_RAT_NR);

		_LTEPercentageMeasuredByANR = PropertyUtility.getIntProperties(properties, PROP_LTE_PERCENTAGE_MEASURED_BY_ANR);
		_relocate = PropertyUtility.getBooleanProperties(properties, PROP_RELOCATE);
		_maxAttributesPerSection = PropertyUtility.getIntProperties(properties, PROP_MAX_ATTRIBUTES_PER_SECTION);
		_attrSectionsName =  PropertyUtility.getStringArrayProperties(properties,PROP_ATTRIBUTES_SECTIONS_NAME);

	}
	

}
