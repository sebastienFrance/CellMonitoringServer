package com.seb.datasources;

import com.seb.databaseAccess.DatabaseHelper;

public class DatasourceSQLRequestBuilder {

	private DatasourceSQLRequestBuilder() {}
	
	public static String createTableDatasource() {
		final String sql = "CREATE TABLE DATASOURCE " +
				"(ID INTEGER PRIMARY KEY," +
				" NAME VARCHAR(255), " + 
				" DESCRIPTION VARCHAR(255), " + 
				" TECHNO INTEGER, " + 
				" PRIMARY_HOST VARCHAR(255), " + 
				" SECONDARY_HOST VARCHAR(255), " + 
				" PORT_NUMBER INTEGER, " + 
				" USERNAME VARCHAR(255), " + 
				" PASSWORD VARCHAR(255))"; 

		return sql;
	}
	
	public static String createIndexTableDatasource() {
		final String sql = "CREATE INDEX datasource_id_index ON DATASOURCE (id);";
		return sql;
	}
	
	public static String createTableDatasourceTopology() {
		 final String sql = "CREATE TABLE DATASOURCE_TOPOLOGY " +
					"(ID INTEGER PRIMARY KEY," +
					" PARENT_DS INTEGER REFERENCES datasource(id) ON DELETE CASCADE, " + 
					" NAME VARCHAR(255), " + 
					" DESCRIPTION VARCHAR(255), " + 
					" PRIMARY_HOST VARCHAR(255), " + 
					" SECONDARY_HOST VARCHAR(255), " +
					" EMS_PORT_NUMBER INTEGER, " + 
					" EMS_USERNAME VARCHAR(255), " + 
					" EMS_PASSWORD VARCHAR(255), " + 					
					" FTP_USERNAME VARCHAR(255), " + 
					" FTP_PASSWORD VARCHAR(255))"; 
		 
		 return sql;
	}
	
	public static String createIndexDatasourceTopology() {
		final String sql = "CREATE INDEX datasource_topology_id_index ON DATASOURCE_TOPOLOGY (id);";
		return sql;
	}
	
	public static String insertDatasource(Datasource theDatasource) {
		StringBuilder sql = new StringBuilder ("INSERT INTO DATASOURCE (ID,NAME, DESCRIPTION,TECHNO, PRIMARY_HOST,SECONDARY_HOST,PORT_NUMBER,USERNAME,PASSWORD) VALUES (");

		sql.append("NULL,");// it's autoincremented
		DatabaseHelper.appendStringToDatabase(sql, theDatasource.getName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasource.getDescription()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theDatasource.getTechno())); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasource.getPrimaryHost()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasource.getSecondaryHost()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theDatasource.getPortNumber())); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasource.getUserName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasource.getPassword()); 

		sql.append(");"); 
		
		return sql.toString();
	}
	
	public static String insertDatasourceTopology(DatasourceTopology theDatasourceTopology) {
		StringBuilder sql = new StringBuilder ("INSERT INTO DATASOURCE_TOPOLOGY (ID, PARENT_DS,NAME, DESCRIPTION,PRIMARY_HOST,SECONDARY_HOST,EMS_PORT_NUMBER,EMS_USERNAME,EMS_PASSWORD,FTP_USERNAME,FTP_PASSWORD) VALUES (");

		sql.append("NULL,"); // it's autoincremented
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theDatasourceTopology.getParentDS().getId())); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getDescription()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getPrimaryHost()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getSecondaryHost()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Integer.toString(theDatasourceTopology.getEMSPortNumber())); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getEMSUserName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getEMSPassword()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getFTPUserName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theDatasourceTopology.getFTPPassword()); 

		sql.append(");"); 
		
		return sql.toString();
	}
		
	public static String extractDatasourceObjects() {
		final String sql = "SELECT * FROM DATASOURCE;";
		return sql;
	}
	
	public static String extractDatasourceTopologyObjects(Datasource parentDS) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM DATASOURCE_TOPOLOGY where parent_ds = '" + parentDS.getId() + "';");
		return sql.toString();
	}

}
