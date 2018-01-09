package com.seb.networkGenerator.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seb.networkTopology.database.DatabaseUtility;
import com.seb.networkTopology.generic.Adjacency;
import com.seb.topologyMgt.GeoIndexAllocator;
import com.seb.topologyMgt.GeoLocation;

import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cell {
	private static final Logger LOG = LogManager.getLogger(Cell.class);

	private double _latitude;
	private double _longitude;
	
	private int _radius; // in meter
	
	private String _cellName;
	private String _azimuth;
	
	private String _site;
	private String _siteId;
	private String _release;
	private String _dlFrequency;
	private String _telecomId;
	
	private String _techno;
	
	private int _QOSDatasource = 0; // 0 is reserved when data was loaded from local file system
	private int _topologyDatasource = 0; // 0 is reserved when data was loaded from local file system
	
	private double _deltaLat = 0.0;
	private double _deltaLong = 0.0;
	
	private ArrayList<Adjacency> _neighborRelations;
	
	private ArrayList<AttrNameValue> _attrNameValueList;
	
	
	public void dump() {
		StringBuffer buff = new StringBuffer();
		buff.append("CellName: " + _cellName);
		buff.append("Latitude: " + _latitude + " Longitude: " + _longitude);
		
		LOG.debug("dump::" +buff.toString());

	}
	
	public static String createCellName(String site, int cellId) {
		return site + "_" + cellId;
	}
	
	public static String createTelecomId(String site, int telecomId) {
		return site + "_" + telecomId;
	}
	
	public Cell(String cellName) {
		_cellName = cellName;
		_neighborRelations = new ArrayList<Adjacency>();
		_attrNameValueList = new ArrayList<AttrNameValue>();
	}
	
	public Cell(String cellName, String site, String siteId, double latitude, double longitude, String techno, String telecomId) {
		_cellName = cellName;
		_site = site;
		_siteId = siteId;
		_latitude = latitude;
		_longitude = longitude;
		_techno = techno;
		_telecomId = telecomId;
		
		_neighborRelations = new ArrayList<Adjacency>();
		_attrNameValueList = new ArrayList<AttrNameValue>();
	}
	
	public void updateTargetNeighbor(Map<String,String> cellIdToTelecomId) {
		for (Adjacency currentAdjacency : _neighborRelations) {
			currentAdjacency.updateTargetCellWith(cellIdToTelecomId);
		}
	}
	
	public GeoLocation getGeoLocation() {
		return GeoLocation.fromDegrees(_latitude, _longitude);
	}
	
	
	
	public void addNeighborList(List<Adjacency> neighbors) {
		if (neighbors != null) {
			_neighborRelations.addAll(neighbors);
		}
	}
	
	public void addNeighbor(Adjacency neighborRelation) {
		_neighborRelations.add(neighborRelation);
	}

	public ArrayList<AttrNameValue> getAttrNameValueList() {
		return _attrNameValueList;
	}
	public void addParamValue(String attrName, String attrValue, String sectionName) {		
		AttrNameValue newAttribute = new AttrNameValue(attrName, attrValue, sectionName);
		_attrNameValueList.add(newAttribute);
	}

	public void cleanup() {
		_attrNameValueList = null;
	}

	public void addParamValueList(List<AttrNameValue> attrList) {
		if (attrList != null) {
			_attrNameValueList.addAll(attrList);	
		}
	}
	
	
	public void generateCellOnlyInDatabase(Connection theConnection) {
		DatabaseUtility.insertCell(theConnection, this);
		DatabaseUtility.insertCellAttrNameValues(theConnection, this, _attrNameValueList);
	}
	
	public void generateNeighborsInDatabase(Connection theConnection) {
		DatabaseUtility.insertNeighbors(theConnection, this, _neighborRelations);
	}
	
	public void generateInDatabase(Connection theConnection) {
		DatabaseUtility.insertCell(theConnection, this);
		DatabaseUtility.insertCellAttrNameValues(theConnection, this, _attrNameValueList);
		DatabaseUtility.insertNeighbors(theConnection, this, _neighborRelations);
	}
	
	
	public void setLatitude(String latitude) {
		_latitude = Double.parseDouble(latitude);
	}
	
	public double getLatitude() {
		return _latitude;
	}
	
	public void setLatitude(double latitude) {
		_latitude = latitude;
	}
	
	public void setLongitude(String longitude) {
		_longitude = Double.parseDouble(longitude);
	}
	
	public double getLongitude() {
		return _longitude;
	}
	
	public void setLongitude(double longitude) {
		_longitude = longitude;
	}

	public void setRadius(int radius) {
		_radius = radius;
	}
	
	public int getRadius() {
		return _radius;
	}
	
	public void setCellName(String cellName) {
		_cellName = cellName;
	}
	
	public void setAzimuth(String azimuth) {
		_azimuth = azimuth;
	}
	
	public String getAzimuth() {
		return _azimuth;
	}
	
	public void setSite(String site) {
		_site = site;
	}

	public String getSite() {
		return _site;
	}

	public void setSiteId(String siteId) {
		_siteId = siteId;
	}

	public String getSiteId() {
		return _siteId;
	}

	public void setRelease(String release) {
		_release = release;
	}
	
	public String getRelease() {
		return _release;
	}

	public void setDLFrequency(String dlFrequency) {
		_dlFrequency = dlFrequency;
	}
	
	public String getCellName() {
		return _cellName;
	}
	
	public String getDLFrequency() {
		return _dlFrequency;
	}
	
	public void setTelecomId(String telecomId) {
		_telecomId = telecomId;
	}
	
	public String getTelecomId() {
		return _telecomId;
	}
	
	public long getGeoIndex(GeoIndexAllocator.GeoIndexSize geoIndexSize) {
		return GeoIndexAllocator.getInstance().getIndex(geoIndexSize, _latitude, _longitude).longValue();
	}

	public void setTechno(String techno) {
		_techno = techno;
	}
	
	public String getTechno() {
		return _techno;
	}
	
	public void setQOSDatasource(int QOSDatasource) {
		_QOSDatasource = QOSDatasource;
	}
	
	public int getQOSDatasource() {
		return _QOSDatasource;
	}
	
	public void setTopologyDatasource(int topologyDatasource) {
		_topologyDatasource = topologyDatasource;
	}
	
	public int getTopologyDatasource() {
		return _topologyDatasource;
	}
	
	public void setDeltaLat(double deltaLat) {
		_deltaLat = deltaLat;
	}

	public double getDeltaLat() {
		return _deltaLat;
	}
	public void setDeltaLong(double deltaLong) {
		_deltaLong = deltaLong;
	}

	public double getDeltaLong() {
		return _deltaLong;
	}

	public int getNumberInterRATNR() {
		if (_neighborRelations == null) {
			return 0;
		}
		
		int numberInterRATNR = 0;
		for (Adjacency currentAdj : _neighborRelations) {
			if (currentAdj._technoTarget.equals(getTechno()) == false) {
				numberInterRATNR++;
			}
		}
		return numberInterRATNR;
	}	
	
	public int getNumberIntraFreqNR() {
		if (_neighborRelations == null) {
			return 0;
		}
		
		int numberIntraFreqNR = 0;
		for (Adjacency currentAdj : _neighborRelations) {
			if (currentAdj._technoTarget.equals(getTechno())) {
				if (currentAdj._dlFrequency.equals(getDLFrequency())) {
					numberIntraFreqNR++;
				}
			}
		}
		return numberIntraFreqNR;
	}

	public int getNumberInterFreqNR() {
		if (_neighborRelations == null) {
			return 0;
		}
		
		int numberInterFreqNR = 0;
		for (Adjacency currentAdj : _neighborRelations) {
			if (currentAdj._technoTarget.equals(getTechno())) {
				if (currentAdj._dlFrequency.equals(getDLFrequency()) == false) {
					numberInterFreqNR++;
				}
			}
		}
		return numberInterFreqNR;
	}

}
