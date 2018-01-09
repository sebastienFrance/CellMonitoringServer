package com.seb.topologyMgt;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.databaseAccess.DatabaseHelper;

public class TopologyMgtSQLRequestBuilder {
	private static final Logger LOG = LogManager.getLogger(TopologyMgtSQLRequestBuilder.class);
	
	private TopologyMgtSQLRequestBuilder() {}
	
	public static String createAboutTable() {
		return 	"CREATE TABLE ABOUT " +
				"(LTE_CELLS_COUNT INTEGER," +
				" LTE_NEIGHBORS_COUNT INTEGER, " + 
				" WCDMA_CELLS_COUNT INTEGER, " + 
				" WCDMA_NEIGHBORS_COUNT INTEGER, " + 
				" GSM_CELLS_COUNT INTEGER, " + 
				" GSM_NEIGHBORS_COUNT INTEGER)"; 
	}

	public static String createCellsTable() {
		return 	 "CREATE TABLE CELLS " +
				"(ID 				VARCHAR(30) PRIMARY KEY NOT NULL," +
				" LATITUDE       	NUMERIC(6,3) NOT NULL, " + 
				" LONGITUDE      	NUMERIC(6,3) NOT NULL, " + 
				" RADIUS         	INTEGER, " + 
				" AZIMUTH        	VARCHAR(3), " + 
				" SITE           	VARCHAR(30)," +
				" SITE_ID        	VARCHAR(10)," +        
				" RELEASE 		 	VARCHAR(10)," +
				" DLFREQUENCY    	VARCHAR(10), " +
				" TELECOMID      	VARCHAR(30)," +
				" TECHNO         	VARCHAR(5)," +
				" QOS_DS         	INTEGER," +
				" TOPOLOGY_DS    	INTEGER," +
				" NUMBERINTRAFREQNR INTEGER," +
				" NUMBERINTERFREQNR INTEGER," +
				" NUMBERINTERRATNR  INTEGER," + 
				" GEO_INDEX_LARGE 	INTEGER NOT NULL," +
				" GEO_INDEX_MEDIUM	INTEGER NOT NULL," +
				" GEO_INDEX_SMALL   INTEGER NOT NULL" +
				")"; 
	}

	public static String createCellsAttributesTable() {
		return 	"CREATE TABLE CELLS_ATTRIBUTES " +
				"(ID VARCHAR(30) NOT NULL," +
				" NAME       VARCHAR(30), " + 
				" VALUE      VARCHAR(30), " + 
				" SECTION        VARCHAR(30))"; 
	}

	public static String createCellsNRTable() {
		return  "CREATE TABLE CELLS_NR " +
				"(ID VARCHAR(30) NOT NULL," +
				" TARGETCELL VARCHAR(30) NOT NULL, " + 
				" NOHO       VARCHAR(6), " + 
				" NOREMOVE      VARCHAR(6), " + 
				" DLFREQUENCY   VARCHAR(10), " + 
				" MEASUREDBYANR VARCHAR(6), " + 
				" TECHNO        VARCHAR(5))"; 
	}
	
	public static String createIndexCellsTableOnId() {
		return "CREATE INDEX cells_id_index ON CELLS (id);";
	}
	
	public static String createIndexCellsTableOnTelecomId() {
		return "CREATE INDEX cells_telecomid_index ON CELLS (telecomid);";
	}
	
	public static String createIndexCellsTableOnLatitude() {
		return "CREATE INDEX cells_latitude_index ON CELLS (latitude);";
	}
	public static String createIndexCellsTableOnLongitude() {
		return "CREATE INDEX cells_longitude_index ON CELLS (longitude);";
	}
	
	public static String createIndexCellsTableOnSiteId() {
		return "CREATE INDEX cells_site_id_index ON CELLS (site_id);";
	}
	
	public static String createIndexCellsAttributesTable() {
		return "CREATE INDEX param_id_index ON CELLS_ATTRIBUTES (id);";
	}

	public static String createIndexCellsNRTable() {
		return "CREATE INDEX cells_nr_id_index ON CELLS_NR (id)";
	}

	public static String createIndexCellsTableOnGeoIndex(GeoIndexAllocator.GeoIndexSize geoIndexSize) {
		switch  (geoIndexSize) {
		case LARGE: return "CREATE INDEX cells_geo_index_large_index ON CELLS (geo_index_large);";
		case MEDIUM: return "CREATE INDEX cells_geo_index_medium_index ON CELLS (geo_index_medium);";
		case SMALL: return "CREATE INDEX cells_geo_index_small_index ON CELLS (geo_index_small);";
		default: {
			LOG.warn("TopologyMgtSQLRequestBuilder::createIndexCellsTableOnGeoIndex unknown index");
			return null;
		}

		}

	}

	public static String insertAbout(int LTECellCount, int LTENRCount, int WCDMACellCount, int WCDMANRCount, int GSMCellCount, int GSMNRCount) {
		StringBuilder sql = new StringBuilder ("INSERT INTO ABOUT (LTE_CELLS_COUNT,LTE_NEIGHBORS_COUNT,WCDMA_CELLS_COUNT,WCDMA_NEIGHBORS_COUNT,GSM_CELLS_COUNT,GSM_NEIGHBORS_COUNT) VALUES (");

		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(LTECellCount));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(LTENRCount));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(WCDMACellCount));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(WCDMANRCount));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(GSMCellCount));
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(GSMNRCount));
		sql.append(");");

		return sql.toString();
	}
	
	public static String updateAbout(int LTECellCount, int LTENRCount, int WCDMACellCount, int WCDMANRCount, int GSMCellCount, int GSMNRCount) {
		StringBuilder sql = new StringBuilder ("UPDATE ABOUT SET LTE_CELLS_COUNT = "); 
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(LTECellCount));
		sql.append(",");
		sql.append("LTE_NEIGHBORS_COUNT = ");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(LTENRCount));
		sql.append(",");
		sql.append("WCDMA_CELLS_COUNT = ");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(WCDMACellCount));
		sql.append(",");
		sql.append("WCDMA_NEIGHBORS_COUNT = ");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(WCDMANRCount));
		sql.append(",");
		sql.append("GSM_CELLS_COUNT = ");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(GSMCellCount));
		sql.append(",");
		sql.append("GSM_NEIGHBORS_COUNT = ");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(GSMNRCount));
		sql.append(";");

		return sql.toString();
	}

	
	public static String getAbout() {
		return "select * from ABOUT";
	}

	public static String getCell(String cellName, String techno) {
		return "SELECT * FROM CELLS WHERE ID = '" + cellName + "' AND TECHNO LIKE '" + techno + "';";
	}
	
	public static String getCellByName(String cellName) {
		return "SELECT * FROM CELLS WHERE ID = '" + cellName + "';";
	}
	
	public static String getCellDSInfoByCellName(String cellName) {
		return "SELECT QOS_DS, TOPOLOGY_DS, ID FROM CELLS WHERE ID = '" + cellName + "';" ;
	}
	
	public static String getCellDSInfoByCellNames(String[] cellNames) {
		StringBuilder buffer = new StringBuilder("SELECT QOS_DS, TOPOLOGY_DS, ID FROM CELLS WHERE ID in (");

		for (int i = 0 ; i < cellNames.length; i++) {
			if (i > 0) {

				// To improved maybe we can use something like IN (x1, X2, ...)
				buffer.append(",");
			}

			buffer.append("'" + cellNames[i] + "'");
		}
		buffer.append(");");
		return buffer.toString();
	}

	public static String getCellbyTelecomId(String telecomId) {
		return "SELECT * FROM CELLS WHERE TELECOMID = '" + telecomId + "';";
	}
	
	public static String getCellsStartingWith(String matching, String techno, int maxResults) {
		StringBuilder sql = new StringBuilder();

		if (matching.startsWith(":")) {
			// search using the siteId
			String realSiteId = matching.substring(1);
			sql.append("SELECT * FROM CELLS where site_id like \"" + realSiteId + "%\"");
		} else {
			sql.append("SELECT * FROM CELLS where id like \"" + matching + "%\"");
		}


		if (techno.equals(TopologyCste.TECHNO_ALL) == false) {
			sql.append(" AND techno = \"" + techno + "\"");
		} // else no filter on Techno

		sql.append(" LIMIT " + Integer.toString(maxResults)+ ";");

		return sql.toString();
	}

	public static String getCellsAroundPosition(boolean findCellsByGeoIndex, GeoLocation[] boundingBox) {
		if (findCellsByGeoIndex) {
			return getCellsAroundPositionByGeoIndex(boundingBox);
		} else {
			return getCellsAroundPositionByLatitudeLongitude(boundingBox);
		}
		
	}
	
	private static String getCellsAroundPositionByGeoIndex(GeoLocation[] boundingBox) {
		GeoIndexAllocator.GeoIndexSize indexSize = GeoIndexAllocator.getInstance().getBestGeoIndexSizeFor(boundingBox);
		List<Long> indexes = GeoIndexAllocator.getInstance().indexesForBoundingBox(indexSize, boundingBox);

		String indexName = getIndexNameForGeoIndexForSize(indexSize);
		
		StringBuilder sql = new StringBuilder("SELECT *  FROM CELLS WHERE ");
		sql.append(DatabaseHelper.buildOrStatements(indexes, indexName));
		sql.append(";");
		
		return sql.toString();
	}
	
	private static String getIndexNameForGeoIndexForSize(GeoIndexAllocator.GeoIndexSize indexSize) {
		switch (indexSize) {
		case LARGE: {
			return "GEO_INDEX_LARGE";
		}
		case MEDIUM: {
			return "GEO_INDEX_MEDIUM";
		}
		case SMALL: {
			return "GEO_INDEX_SMALL";
		}
		default: {
			LOG.warn("getCellsAroundPosition::getIndexNameForGeoIndexForSize unknown index for GeoIndex");
			return "GEO_INDEX_LARGE";
		}
		}
	}
	
	
	public static String getCellsInGeoIndex(Long geoIndex, GeoIndexAllocator.GeoIndexSize indexSize) {

		String indexName = getIndexNameForGeoIndexForSize(indexSize);
		
		StringBuilder sql = new StringBuilder("SELECT *  FROM CELLS WHERE ");
		sql.append(indexName + " = " + geoIndex.longValue() + ";");
		
		return sql.toString();
	}
	
	
	
	public static String getCellsAroundPositionByLatitudeLongitude(GeoLocation[] boundingBox) {

		double maxNorthLat = boundingBox[1].getLatitudeInDegrees();
		double maxEastLong = boundingBox[1].getLongitudeInDegrees();
		double maxSouthLat = boundingBox[0].getLatitudeInDegrees();
		double maxWestLong = boundingBox[0].getLongitudeInDegrees();

		StringBuilder sql = new StringBuilder("SELECT *  FROM CELLS WHERE ");
		sql.append("latitude <= " + maxNorthLat);
		sql.append(" AND latitude >= " + maxSouthLat);
		sql.append(" AND longitude <= " + maxEastLong);
		sql.append(" AND longitude >= " + maxWestLong);
		sql.append(";");
		
		return sql.toString();
	}
	
	
	public static String getCellNeighbors(String cellName) {
		return "SELECT * FROM CELLS_NR WHERE ID = '" + cellName + "';";
	}

	public static String getCellAttrNameValue(String cellName) {
		return "SELECT * FROM CELLS_ATTRIBUTES WHERE ID = '" + cellName + "';";
	}
	
	public static String getCountParametersForCellAttributes(String[] objectIds, String paramName, String[] paramValues) {
		StringBuilder sqlRequest = new StringBuilder("Select COUNT(ID) FROM CELLS_ATTRIBUTES WHERE ID");
		
		// add objectIds list
		sqlRequest.append(DatabaseHelper.buildInStatement(objectIds));
		
		// add parameterName
		sqlRequest.append("AND NAME = \"" + paramName + "\" AND VALUE");
		
		// add paramValue list
		sqlRequest.append(DatabaseHelper.buildInStatement(paramValues));
		
		return sqlRequest.toString();
	}
	
	public static String getCountParametersForNRs(String[] objectIds, String paramName, String[] paramValues) {
		StringBuilder sqlRequest = new StringBuilder("Select COUNT(ID) FROM CELLS_NR WHERE ID");
		
		// add objectIds list
		sqlRequest.append(DatabaseHelper.buildInStatement(objectIds));
		
		// add parameterName
		sqlRequest.append("AND " + paramName);
		
		// add paramValue list
		sqlRequest.append(DatabaseHelper.buildInStatement(paramValues));		
		sqlRequest.append("AND TECHNO=\"LTE\"");
		
		return sqlRequest.toString();
	}
}
