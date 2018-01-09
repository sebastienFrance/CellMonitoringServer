package com.seb.imonserver.datamodel;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONObject;



import com.seb.topologyMgt.GeoLocation;
import com.seb.imonserver.datamodel.AttrNameValue;
import com.seb.imonserver.eql.MultiLineEQLHandler;


/**
 * Used to build a JSON object that contains cell parameters, neighbor relations and generic attributes
 * 
 * @author Sebastien Brugalieres
 *
 */
public class Cell {
	private static final Logger LOG = LogManager.getLogger(Cell.class);

	public static final String LTE_TECHNO = "LTE";
	public static final String WCDMA_TECHNO = "WCDMA";
	public static final String GSM_TECHNO = "GSM";
	static final String ALL_TECHNO = "ALL";

	
	private String _cellName;
	private double _longitude;
	private double _latitude;
	private int _radius;
	private String _azimuth;
	private String _techno;
	private int _technoType;
	private String _site;
	private String _siteId;
	private String _release;
	private String _dlFrequency;
	private String _telecomId;
	private int _NumberIntraFreqNR;
	private int _NumberInterFreqNR;
	private int _NumberInterRATNR;
	
	static public final int TYPE_LTE = 0;
	static public final int TYPE_WCDMA = 1;
	static public final int TYPE_GSM = 2;
	static public final int TYPE_UNKNOWN = 99;
	
	private ArrayList<Adjacency> _adjList;
	private ArrayList<AttrNameValue> _attrNameValueList;
	
	private GeoLocation _geoLocation;
	
	private JSONObject _cellJSONEncoding = null;
	
	public Cell(String cellName, String longitude, String latitude, String radius, String azimuth, String techno, String site, String siteId, String release, String dlFrequency, String telecomId) {
		_cellName = cellName;
		_longitude = Double.parseDouble(longitude);
		_latitude = Double.parseDouble(latitude);
		_radius = Integer.parseInt(radius);
		_azimuth = azimuth;
		_techno = techno;
		_site = site;
		_siteId = siteId;
		_release = release;
		_dlFrequency = dlFrequency;
		_telecomId = telecomId;
		
		initializeCell();
		
	}
	
	public Cell(String cellName, double longitude, double latitude, int radius, String azimuth, String techno, String site, String siteId, String release, String dlFrequency, String telecomId) {
		_cellName = cellName;
		_longitude = longitude;
		_latitude = latitude;
		_radius = radius;
		_azimuth = azimuth;
		_techno = techno;
		_site = site;
		_siteId = siteId;
		_release = release;
		_dlFrequency = dlFrequency;
		_telecomId = telecomId;
		
		initializeCell();		
	}

	private void initializeCell() {
		try {
			_geoLocation = GeoLocation.fromDegrees(_latitude , _longitude);
		}
		catch (Exception ex) {
			LOG.error("Warning: Cell: " + _cellName + " has invalid Latitude: " + _latitude + " or longitude:" + _longitude, ex);
		}

		_adjList = new ArrayList<Adjacency>();
		_attrNameValueList = new ArrayList<AttrNameValue>();

		_technoType = techno2TechnoType(_techno);		
	}
	
	private static int techno2TechnoType(String techno) {
		if (techno.equalsIgnoreCase(Cell.LTE_TECHNO)) {
			return TYPE_LTE;
		} else if (techno.equalsIgnoreCase(Cell.WCDMA_TECHNO)) {
			return TYPE_WCDMA;			
		} else if (techno.equalsIgnoreCase(Cell.GSM_TECHNO)) {
			return TYPE_GSM;			
		} else {
			return TYPE_UNKNOWN;			
		}
		
	}
	
	
	public void storeJSONEncoding(JSONObject JSONCell) {
		_cellJSONEncoding = JSONCell;
	}
	
	public JSONObject extractJSONEncoding() {
		return _cellJSONEncoding;
	}
	
	
	public void addAdjacency(Adjacency adj) {
		_adjList.add(adj);
	}
	
	public ArrayList<AttrNameValue> extractAttrNameValueList() {
		return _attrNameValueList;
	}
	
	public void addParamValue(String attrName, String attrValue, String sectionName) {
		AttrNameValue newAttribute = new AttrNameValue(attrName, attrValue, sectionName);
		_attrNameValueList.add(newAttribute);	
	}

	
	public ArrayList<Adjacency> listOfAdj() {
	return _adjList;
}

	public String getCellName() {
		return _cellName;
	}
	
	public double getLongitude() {
		return _longitude;
	}
	
	public int getRadius() {
		return _radius;
	}

	public String getTechno() {
		return _techno;
	}
	
	public int getTechnoType() {
		return _technoType;
	}

	public double getLatitude() {
		return _latitude;
	}

	public String getAzimuth() {
		return _azimuth;
	}
	
	public GeoLocation extractLocation() {
		return _geoLocation;
	}
	
	public String getSite() {
		return _site;
	}

	public String getSiteId() {
		return _siteId;
	}
	
	public String getRelease() {
		return _release;
	}
	
	public String getDlFrequency() {
		return _dlFrequency;
	}
	
	public String getTelecomId() {
		return _telecomId;
	}

	public int getNumberInterRATNR() {
		return _NumberInterRATNR;
	}	
	
	public int getNumberIntraFreqNR() {
		return _NumberIntraFreqNR;
	}

	public int getNumberInterFreqNR() {
		return _NumberInterFreqNR;
	}
	
	public void setNumberInterRATNR(int NumberInterRATNR) {
		_NumberInterRATNR = NumberInterRATNR;		
	}

	public void setNumberIntraFreqNR(int NumberIntraFreqNR) {
		_NumberIntraFreqNR = NumberIntraFreqNR;
	}

	public void setNumberInterFreqNR(int NumberInterFreqNR) {
		 _NumberInterFreqNR = NumberInterFreqNR;
	}

}
