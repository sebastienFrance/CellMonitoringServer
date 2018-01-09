package com.seb.networkGenerator.OpenCellIds;



public class OpenCellIds {
	/*
	 * Old OpenCellIds file format
	 */
	private static final int INDEX_OPEN_CELL_IDS_ID = 0;
	private static final int INDEX_OPEN_CELL_IDS_LATITUDE = 1;
	private static final int INDEX_OPEN_CELL_IDS_LONGITUDE = 2;
	private static final int INDEX_OPEN_CELL_IDS_MCC = 3;
	private static final int INDEX_OPEN_CELL_IDS_MNC = 4;
	private static final int INDEX_OPEN_CELL_IDS_LAC = 5;
	private static final int INDEX_OPEN_CELL_IDS_CELLID = 6;
	private static final int INDEX_OPEN_CELL_IDS_RANGE = 7;
	private static final int INDEX_OPEN_CELL_IDS_NB_SAMPLES = 8;
	private static final int INDEX_OPEN_CELL_IDS_CREATION_DATE = 9;
	private static final int INDEX_OPEN_CELL_IDS_UPDATE_DATE = 10;
	private static final int INDEX_OPEN_CELL_IDS_COUNTRY = 11;
	private static final int INDEX_OPEN_CELL_IDS_OPERATOR = 11;

	/*
	 * New OpenCellIds file format
	 */
	private static final int INDEX_NEW_OPEN_CELL_IDS_TECHNO = 0;
	private static final int INDEX_NEW_OPEN_CELL_IDS_MCC = 1;
	private static final int INDEX_NEW_OPEN_CELL_IDS_MNC = 2;
	private static final int INDEX_NEW_OPEN_CELL_IDS_AREA = 3;
	private static final int INDEX_NEW_OPEN_CELL_IDS_CELL_ID = 4;
	private static final int INDEX_NEW_OPEN_CELL_IDS_CELL_CODE = 5;
	private static final int INDEX_NEW_OPEN_CELL_IDS_LONGITUDE = 6;
	private static final int INDEX_NEW_OPEN_CELL_IDS_LATITUDE = 7;
	private static final int INDEX_NEW_OPEN_CELL_IDS_RANGE = 8;
	private static final int INDEX_NEW_OPEN_CELL_IDS_SAMPLES = 9;
	private static final int INDEX_NEW_OPEN_CELL_IDS_CHANGEABLE = 10;
	private static final int INDEX_NEW_OPEN_CELL_IDS_CREATED = 11;
	private static final int INDEX_NEW_OPEN_CELL_IDS_UPDATED = 12;
	private static final int INDEX_NEW_OPEN_CELL_IDS_AVERAGE_SIGNAL_POWER = 13;

	public static final int OPEN_CELL_IDS_VERSION_OLD = 0;
	public static final int OPEN_CELL_IDS_VERSION_NEW = 1;

	private String[] _fieldValues;
	private int _version = 0;

	public OpenCellIds(int version, String[] fromCsvFile) {
		_version = version;
		_fieldValues = fromCsvFile;
	}

	private String getAttribute(int index) {
		return _fieldValues[index];
	}

	public String getLatitude() {
		if (_version == OPEN_CELL_IDS_VERSION_NEW) {
			return getAttribute(INDEX_NEW_OPEN_CELL_IDS_LATITUDE);
		} else {
			return getAttribute(INDEX_OPEN_CELL_IDS_LATITUDE);
		}
	}

	public String getLongitude() {
		if (_version == OPEN_CELL_IDS_VERSION_NEW) {
			return getAttribute(INDEX_NEW_OPEN_CELL_IDS_LONGITUDE);
		} else {
			return getAttribute(INDEX_OPEN_CELL_IDS_LONGITUDE);
		}
	}

}
