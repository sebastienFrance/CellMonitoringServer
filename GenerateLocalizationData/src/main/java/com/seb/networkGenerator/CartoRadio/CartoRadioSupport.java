package com.seb.networkGenerator.CartoRadio;

import com.seb.utilities.Technology;

public class CartoRadioSupport {
	public static final int INDEX_CARTORADIO_SUPPORT_SUPPORT_ID = 0;
	public static final int INDEX_CARTORADIO_SUPPORT_CARTORADIO_ID = 1;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_TYPE = 2;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_ID = 3;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_SIZE = 4;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_DIRECTIVITY = 5;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_AZIMUTH = 6;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_HEIGHT = 7;
	public static final int INDEX_CARTORADIO_SUPPORT_ANTENNA_SYSTEM = 8;
	public static final int INDEX_CARTORADIO_SUPPORT_SUPPORT_START_FREQUENCY = 9;
	public static final int INDEX_CARTORADIO_SUPPORT_SUPPORT_END_FREQUENCY = 10;
	public static final int INDEX_CARTORADIO_SUPPORT_SUPPORT_FREQUENCY_UNIT = 11;

	private String[] _supportValues;
	
	public CartoRadioSupport(String[] fromCsvFile) {
		_supportValues = fromCsvFile;
	}
	
	public String getAttribute(int index) {
		return _supportValues[index];
	}
	
	public Technology getCellTechno() {
		String technoSupport = _supportValues[INDEX_CARTORADIO_SUPPORT_ANTENNA_SYSTEM];
		if (technoSupport.contains("GSM")) {
			return Technology.GSM;
		} else if (technoSupport.contains("UMTS")) {
			return Technology.WCDMA;
		} else if (technoSupport.contains("LTE")) {
			return Technology.LTE;
		} else {
			return Technology.ALL;
		}
	}
}
