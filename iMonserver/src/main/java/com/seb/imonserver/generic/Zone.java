package com.seb.imonserver.generic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains the definition of a Zone (name, description, list of cells, technology...)
 * 
 * @author Sebastien Brugalieres
 *
 */
public class Zone {
	private static final Logger LOG = LogManager.getLogger(Zone.class);

	private String _name;
	private String _description;
	private int _type;
	private String _techno;
	private String[] _listOfCells;
	
	public static final int WORKING_ZONTE_TYPE = 0;
	public static final int OBJECT_ZONE_TYPE = 1;
	
	private static final String OBJECT_ZONE = "OZ";
	private static final String WORKING_ZONE = "WZ";
	
	private static final int INDEX_ZONE_NAME = 0;
	private static final int INDEX_ZONE_TYPE = 1;
	private static final int INDEX_ZONE_TECHNO = 2;
	private static final int INDEX_ZONE_DESCRIPTION = 3;
	private static final int INDEX_ZONE_CELLID_START = 4;
	
	public static final int MIN_INDEX_ZONE = 4;
	
	public Zone(String[] zoneDescription) {
		
		_name = zoneDescription[INDEX_ZONE_NAME];

		if (zoneDescription[INDEX_ZONE_TYPE].equals(OBJECT_ZONE)) {
			_type = OBJECT_ZONE_TYPE;
		} else if (zoneDescription[INDEX_ZONE_TYPE].equals(WORKING_ZONE)) {
			_type = WORKING_ZONTE_TYPE;
		} else {
			LOG.warn("Zone::Error unknown zone type");
		}

		_description = zoneDescription[INDEX_ZONE_DESCRIPTION];
		
		_techno = zoneDescription[INDEX_ZONE_TECHNO];

		if (zoneDescription.length > MIN_INDEX_ZONE) {
			_listOfCells = new String[zoneDescription.length - INDEX_ZONE_CELLID_START];
			for (int i = 0, j = INDEX_ZONE_CELLID_START; j < zoneDescription.length; i++, j++) {
				_listOfCells[i] = zoneDescription[j];
			}		  
		}
	}
	
	public void dump() {
		LOG.info("dump::Zone Name: " + _name);
		LOG.info("dump::Zone Type: " + _type);
		LOG.info("dump::Zone Techno: " + _techno);
		LOG.info("dump::Zone Description: " + _description);
		
		for (int i = 0; i < _listOfCells.length; i++) {
			LOG.info("dump::Zone Cell Id: " + _listOfCells[i]);
		}
		
	}
	
	public String getName() {
		return _name;
	}
	
	public int getType() {
		return _type;
	}
	
	public String getTechno() {
		return _techno;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public String[] extractListOfCells() {
		return _listOfCells;
	}
}
