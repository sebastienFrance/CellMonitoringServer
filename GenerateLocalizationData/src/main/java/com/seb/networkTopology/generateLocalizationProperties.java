package com.seb.networkTopology;


import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkTopology.generic.Anonymous;
import com.seb.networkTopology.generic.PropertyUtility;
import com.seb.utilities.Technology;

public class generateLocalizationProperties extends BasicProperties {
	private static final Logger LOG = LogManager.getLogger(generateLocalizationProperties.class);
	
	private String _LTESnapshotDirectoryName = null;
	private String _LTESnapshotFileExtension = null;
	private String _LTEAttributeFileName = null;
	private String _WCDMASnapshotDirectoryName = null;
	private String _WCDMASnapshotFileExtension = null;
	private String _WCDMAAttributeFileName = null;
	private String _GSMSnapshotName = null;
	private String _GSMSnapshotDirectoryName = null;
	private String _GSMSnapshotAdj = null;

	private boolean _relocate = false;
	private boolean _relocateOnly = false;
	private double[] _relocateSrcCoordinates = null;
	private double[] _relocateTargetCoordinates = null;
	
	public String getSnapshotDirectoryName(Technology techno) {
		switch (techno) {
		case LTE: return getLTESnapshotDirectoryName();
		case WCDMA: return getWCDMASnapshotDirectoryName();
		case GSM: return getGSMSnapshotDirectoryName();
		default: return null;
		}
	}
	public String getSnapshotFileExtension(Technology techno) {
		switch (techno) {
		case LTE: return getLTESnapshotFileExtension();
		case WCDMA: return getWCDMASnapshotFileExtension();
		case GSM: return "";
		default: return null;
		}
	}
	public String getAttributeFileName(Technology techno) {
		switch (techno) {
		case LTE: return getLTEAttributeFileName();
		case WCDMA: return getWCDMAAttributeFileName();
		default: return null;
		}
	}

	 public String getLTESnapshotDirectoryName() {
		return _LTESnapshotDirectoryName;
	}

	 public String getLTESnapshotFileExtension() {
		return _LTESnapshotFileExtension;
	}

	 public String getLTEAttributeFileName() {
		return _LTEAttributeFileName;
	}

	 public String getWCDMASnapshotDirectoryName() {
		return _WCDMASnapshotDirectoryName;
	}

	 public String getWCDMASnapshotFileExtension() {
		return _WCDMASnapshotFileExtension;
	}

	 public String getWCDMAAttributeFileName() {
		return _WCDMAAttributeFileName;
	}

	 public String getGSMSnapshotName() {
		return _GSMSnapshotName;
	}

	 public String getGSMSnapshotDirectoryName() {
		return _GSMSnapshotDirectoryName;
	}
	
	 public String getGSMSnapshotAdj() {
		return _GSMSnapshotAdj;
	}
	
	 public boolean getRelocate() {
		return _relocate;
	}

	 public double getRelocateSourceLatitude() {
		return _relocateSrcCoordinates[0];
	}
	 public double getRelocateSourceLongitude() {
		return _relocateSrcCoordinates[1];
	}

	 public double getRelocateTargetLatitude() {
		return _relocateTargetCoordinates[0];
	}
	 public double getRelocateTargetLongitude() {
		return _relocateTargetCoordinates[1];
	}
	
	 public boolean getRelocateOnly() {
		return _relocateOnly;
	}

	static private final String PROP_LTE_SNAPSHOT_DIRECTORY = "LTESnapshotDirectory";
	static private final String PROP_LTE_SNAPSHOT_FILE_EXTENSION = "LTESnapshotFileExtension";
	static private final String PROP_LTE_ATTRIBUTE_FILE_NAME = "LTEAttributeFileName";
	static private final String PROP_WCDMA_SNAPSHOT_DIRECTORY ="WCDMASnapshotDirectory";
	static private final String PROP_WCDMA_SNAPSHOT_FILE_EXTENSION = "WCDMASnapshotFileExtension";
	static private final String PROP_WCDMA_ATTRIBUTE_FILE_NAME = "WCDMAAttributeFileName";
	static private final String PROP_GSM_SNAPSHOT ="GSMSnapshot";
	static private final String PROP_GSM_SNAPSHOT_DIRECTORY ="GSMSnapshotDirectory";
	static private final String PROP_GSM_SNAPSHOT_ADJ ="GSMSnapshotAdj";
	static private final String PROP_RELOCATE ="relocate";
	static private final String PROP_RELOCATE_ONLY ="relocateOnly";
	static private final String PROP_RELOCATE_SOURCE_COORDINATES ="relocateSourceCoordinates";
	static private final String PROP_RELOCATE_TARGET_COORDINATES ="relocateTargetCoordinates";
	static private final String PROP_ANONYMIZE ="anonymize";
	static private final String PROP_INPUT_FILENAME_ANONYMIZE ="inputFileNameAnonymize";

	
	private generateLocalizationProperties() {	
	}
	
	private static generateLocalizationProperties INSTANCE = new generateLocalizationProperties();
	
	public static generateLocalizationProperties getInstance() {
		return INSTANCE;
	}

	@Override
	protected  void getSpecificProperties(Properties properties) {
		_LTESnapshotDirectoryName = PropertyUtility.getStringProperties(properties, PROP_LTE_SNAPSHOT_DIRECTORY);	
		_LTESnapshotFileExtension = PropertyUtility.getStringProperties(properties, PROP_LTE_SNAPSHOT_FILE_EXTENSION);	
		_LTEAttributeFileName = PropertyUtility.getStringProperties(properties, PROP_LTE_ATTRIBUTE_FILE_NAME);	
		_WCDMASnapshotDirectoryName = PropertyUtility.getStringProperties(properties, PROP_WCDMA_SNAPSHOT_DIRECTORY);	
		_WCDMASnapshotFileExtension = PropertyUtility.getStringProperties(properties, PROP_WCDMA_SNAPSHOT_FILE_EXTENSION);	
		_WCDMAAttributeFileName = PropertyUtility.getStringProperties(properties, PROP_WCDMA_ATTRIBUTE_FILE_NAME);	
		_GSMSnapshotName = PropertyUtility.getStringProperties(properties, PROP_GSM_SNAPSHOT);	
		_GSMSnapshotDirectoryName = PropertyUtility.getStringProperties(properties, PROP_GSM_SNAPSHOT_DIRECTORY);	
		_GSMSnapshotAdj = PropertyUtility.getStringProperties(properties, PROP_GSM_SNAPSHOT_ADJ);	

		_relocate = PropertyUtility.getBooleanProperties(properties, PROP_RELOCATE);
		_relocateOnly = PropertyUtility.getBooleanProperties(properties, PROP_RELOCATE_ONLY);
		_relocateSrcCoordinates = PropertyUtility.getDoubleArrayProperties(properties, PROP_RELOCATE_SOURCE_COORDINATES);
		_relocateTargetCoordinates = PropertyUtility.getDoubleArrayProperties(properties, PROP_RELOCATE_TARGET_COORDINATES);

		if (_relocate == true) {
			if (_relocateSrcCoordinates == null || _relocateTargetCoordinates == null) {
				LOG.error("Error, missing properties: " + PROP_RELOCATE_SOURCE_COORDINATES + " or " + PROP_RELOCATE_TARGET_COORDINATES);
				System.exit(1);
			}
			
			if (_relocateSrcCoordinates.length != 2 || _relocateTargetCoordinates.length != 2) {
				LOG.error("Error, format of properties: " + PROP_RELOCATE_SOURCE_COORDINATES + " or " + PROP_RELOCATE_TARGET_COORDINATES);
				System.exit(1);				
			}
		}

		boolean anonymize = PropertyUtility.getBooleanProperties(properties, PROP_ANONYMIZE);
		String inputFileNameAnonymize = PropertyUtility.getStringProperties(properties, PROP_INPUT_FILENAME_ANONYMIZE);
		
		if ((anonymize == true) && (inputFileNameAnonymize != null)) {
			Anonymous.initializeAnonymization(inputFileNameAnonymize);
		}
	}
	

}
