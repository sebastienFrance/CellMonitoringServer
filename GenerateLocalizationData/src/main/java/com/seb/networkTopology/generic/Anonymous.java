package com.seb.networkTopology.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Anonymous {
	
	private static boolean _anonymize = false;
	private static int _cellId = 0;
	private static int _telecomId = 1000;
	
	private static List<String> _listOfIdentifier;
	
	// Key: Original Site Name 
	// Value: new Site Name
	private static Map<String, String> _siteMapping;
	
	// Key: Original Cell Name 
	// Value: new Cell Name
	private static Map<String, String> _cellMapping;

	// Key: Original TelecomId 
	// Value: new TelecomId
	private static Map<String, String> _telecomIdMapping;

	// Key: Original DL Frequency 
	// Value: new DL Frequency
	private static Map<String, String> _dlFrequencyMapping;
	private static int _dlFrequencyId = 1800;

	private Anonymous() {}
	
	public static void initializeAnonymization(String inputFileForAnonymization) {
		_anonymize = true;
		_listOfIdentifier = Utility.parseListOfValues(inputFileForAnonymization);
		_siteMapping = new HashMap<String, String>();
		_cellMapping = new HashMap<String, String>();
		_telecomIdMapping = new HashMap<String, String>();
		_dlFrequencyMapping = new HashMap<String, String>();
	}
	

	/**
	 * Return anonymous site name based on original name. Mapping is memorized, for a given original site name
	 * it returns always the same value
	 * 
	 * @param originalSiteName original name of the LTE/WCDMA/GSM site
	 * @return anonymous site name if anonymization is activated else it returns the original value
	 */
	public static String getSiteMapping(String originalSiteName) {
		if (_anonymize == false) {
			return originalSiteName;
		}
		
		String newSiteName = _siteMapping.get(originalSiteName);
		if (newSiteName == null) {
			newSiteName = _listOfIdentifier.get(0);
			_listOfIdentifier.remove(0);
			_siteMapping.put(originalSiteName, newSiteName);
		}
		
		return newSiteName;
	}
	
	/**
	 * Return anonymous cell name based on original name. Mapping is memorized, for a given original site name + cell name
	 * it returns always the same value
	 * 
	 * @param originalSiteName original name of the LTE/WCDMA/GSM site
	 * @param originalCellName original name of the LTE/WCDMA/GSM cell
	 * @return anonymous cell name if anonymization is activated else it returns the original value
	 */
	public static String getCellMapping(String originalSiteName, String originalCellName) {
		if (_anonymize == false) {
			return originalCellName;
		}
		
		String newCellName = _cellMapping.get(originalCellName);
		if (newCellName == null) {
			// look if we have already allocated a new site name for it else allocate it!
			String newSiteName = getSiteMapping(originalSiteName);
			
			 newCellName = newSiteName + "_" + _cellId;
			_cellId++;
			
			_cellMapping.put(originalCellName, newCellName);
		}
		
		return newCellName;
	}
	
	/**
	 * Return anonymous telecomId based on original name. Mapping is memorized, for a given original telecomId
	 * it returns always the same value
	 * 
	 * @param originalTelecomId
	 * @return anonymous TelecomId if anonymization is activated else it returns the original value
	 */
	public static String getTelecomIdMapping(String originalTelecomId) {
		if (_anonymize == false) {
			return originalTelecomId;
		}
		String newTelecomId = _telecomIdMapping.get(originalTelecomId);
		if (newTelecomId == null) {
			
			newTelecomId = "id_" + _telecomId;
			_telecomId++;
			
			_telecomIdMapping.put(originalTelecomId, newTelecomId);
		}
		return newTelecomId;
	}
	
	public static String getAnonymousLTERelease(String originalRelease) {
		if (_anonymize == false) {
			return originalRelease;
		} else {
			return "LTE_V1.0";
		}
	}
	
	public static String getAnonymousWCDMARelease(String originalRelease) {
		if (_anonymize == false) {
			return originalRelease;
		} else {
			return "WCDMA_V1.0";
		}
	}
	public static String getAnonymousGSMRelease(String originalRelease) {
		if (_anonymize == false) {
			return originalRelease;
		} else {
			return "GSM_V1.0";
		}
	}
	
	/**
	 * Return anonymous DL Frequency based on original value. Mapping is memorized, for a given original DL Frequency
	 * it returns always the same value
	 * 
	 * @param originalDLFrequency
	 * @return anonymous DL Frequency if anonymization is activated else it returns the original value
	 */
	public static String getDLFrequencyMapping(String originalDLFrequency) {
		if (_anonymize == false) {
			return originalDLFrequency;
		}
		String newDLFrequency = _dlFrequencyMapping.get(originalDLFrequency);
		if (newDLFrequency == null) {
			
			newDLFrequency = Integer.toString(_dlFrequencyId);
			_dlFrequencyId += 800;
			
			_dlFrequencyMapping.put(originalDLFrequency, newDLFrequency);
		}
		return newDLFrequency;
	}

}
