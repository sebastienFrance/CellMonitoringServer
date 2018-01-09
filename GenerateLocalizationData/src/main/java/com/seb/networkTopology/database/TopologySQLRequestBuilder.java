package com.seb.networkTopology.database;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.seb.databaseAccess.DatabaseHelper;
import com.seb.networkGenerator.generic.AttrNameValue;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkTopology.generic.Adjacency;
import com.seb.topologyMgt.GeoIndexAllocator;

public class TopologySQLRequestBuilder {

	private TopologySQLRequestBuilder() {}
	
	public static String insertCell(Cell theCell) {
		StringBuilder sql = new StringBuilder ("INSERT INTO CELLS (ID,LATITUDE,LONGITUDE, RADIUS,AZIMUTH,SITE, SITE_ID,RELEASE,DLFREQUENCY,TELECOMID,TECHNO,QOS_DS,TOPOLOGY_DS,NUMBERINTRAFREQNR,NUMBERINTERFREQNR,NUMBERINTERRATNR,GEO_INDEX_LARGE, GEO_INDEX_MEDIUM, GEO_INDEX_SMALL) VALUES (");

		DatabaseHelper.appendStringToDatabase(sql, theCell.getCellName());
		sql.append(",");
		double latitude = latitudeLongitudeToDouble(theCell.getLatitude(), theCell.getDeltaLat());
		DatabaseHelper.appendStringToDatabase(sql,Double.toString(latitude));
		sql.append(",");
		double longitude = latitudeLongitudeToDouble(theCell.getLongitude(), theCell.getDeltaLong());
		DatabaseHelper.appendStringToDatabase(sql, Double.toString(longitude));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theCell.getRadius()));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getAzimuth());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getSite());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getSiteId());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getRelease());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getDLFrequency());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getTelecomId());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getTechno());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theCell.getQOSDatasource()));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theCell.getTopologyDatasource()));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theCell.getNumberIntraFreqNR()));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theCell.getNumberInterFreqNR()));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theCell.getNumberInterRATNR()));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Long.toString(theCell.getGeoIndex(GeoIndexAllocator.GeoIndexSize.LARGE)));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Long.toString(theCell.getGeoIndex(GeoIndexAllocator.GeoIndexSize.MEDIUM)));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Long.toString(theCell.getGeoIndex(GeoIndexAllocator.GeoIndexSize.SMALL)));

		sql.append(");"); 
		return sql.toString();
	}
	
	public static String insertCellAttrNameValue(Cell theCell, AttrNameValue currAttrNameValue) {
		StringBuilder	sql = new StringBuilder ("INSERT INTO CELLS_ATTRIBUTES (ID,NAME,VALUE,SECTION) VALUES (");
		DatabaseHelper.appendStringToDatabase(sql, theCell.getCellName());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, currAttrNameValue.getName());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, currAttrNameValue.getValue());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, currAttrNameValue.getSection());
		sql.append(");"); 
		return sql.toString();
	}
	
	public static String getInsertAttrNameValueTemplate() {
		return "INSERT INTO CELLS_ATTRIBUTES (ID,NAME,VALUE,SECTION) VALUES (?,?,?,?);";
	}
	
	public static void updateInsertAttrNameValueFor(PreparedStatement stmt, Cell theCell, AttrNameValue currAttrNameValue) throws SQLException {
		stmt.setString(1, theCell.getCellName());
		stmt.setString(2, currAttrNameValue.getName());
		stmt.setString(3, currAttrNameValue.getValue());
		stmt.setString(4, currAttrNameValue.getSection());
	}
	
	public static String insertNeighbor(Cell sourceCell, Adjacency targetAdj) {
		StringBuilder sql = new StringBuilder ("INSERT INTO CELLS_NR (ID, TARGETCELL,NOHO,NOREMOVE,DLFREQUENCY,MEASUREDBYANR,TECHNO) VALUES (");
		DatabaseHelper.appendStringToDatabase(sql, sourceCell.getCellName());
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, targetAdj._targetCell);
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, targetAdj._noHo != null ? targetAdj._noHo : "true");
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, targetAdj._noRemove != null ? targetAdj._noRemove : "true");
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, targetAdj._dlFrequency);
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, targetAdj._measuredByANR);
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, targetAdj._technoTarget);
		sql.append(");"); 
		return sql.toString();			
	}
	
	public static String getInsertNeighborTemplate() {
		return "INSERT INTO CELLS_NR (ID, TARGETCELL,NOHO,NOREMOVE,DLFREQUENCY,MEASUREDBYANR,TECHNO) VALUES (?,?,?,?,?,?,?);";
	}
	public static void updateInsertNeighborFor(PreparedStatement stmt, Cell sourceCell, Adjacency targetAdj) throws SQLException {
		stmt.setString(1, sourceCell.getCellName());
		stmt.setString(2, targetAdj._targetCell);
		stmt.setString(3, targetAdj._noHo != null ? targetAdj._noHo : "true");
		stmt.setString(4, targetAdj._noRemove != null ? targetAdj._noRemove : "true");
		stmt.setString(5, targetAdj._dlFrequency);
		stmt.setString(6, targetAdj._measuredByANR);
		stmt.setString(7, targetAdj._technoTarget);
	}
	
	private final static int COORDINATE_PRECISION = 3;

	private static BigDecimal latitudeLongitude(double longitude, double deltaLongitude) {
		if (deltaLongitude != 0) {
			longitude += deltaLongitude;
		}

		BigDecimal bigValue = new BigDecimal(longitude);
		bigValue = bigValue.setScale(COORDINATE_PRECISION, RoundingMode.FLOOR);
		return bigValue;
	}
	
	private static double latitudeLongitudeToDouble(double longitude, double deltaLongitude) {
		return latitudeLongitude(longitude, deltaLongitude).doubleValue();
	}

}
