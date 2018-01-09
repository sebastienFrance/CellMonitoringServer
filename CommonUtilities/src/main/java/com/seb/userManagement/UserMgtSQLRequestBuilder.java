package com.seb.userManagement;

import com.seb.databaseAccess.DatabaseHelper;

public class UserMgtSQLRequestBuilder {

	private UserMgtSQLRequestBuilder() {}
	
	public static String createTable() {
		final String sqlTable = "CREATE TABLE IF NOT EXISTS USERS " +
				"(NAME VARCHAR(255) PRIMARY KEY," +
				" PASSWORD VARCHAR(255), " + 
				" DESCRIPTION VARCHAR(255)," +
				" FIRST_NAME VARCHAR(255)," +
				" LAST_NAME VARCHAR(255)," +
				" EMAIL VARCHAR(255)," +
				" CREATION_DATE INTEGER," +
				" LAST_CONNECTION_DATE INTEGER," +
				" IS_ADMIN INTEGER," +
				" IS_PASSWORD_TO_BE_CHANGED INTEGER)"; 

		return sqlTable;
	}
	
	public static String createTableIndex() {
		final String sqlIndex = "CREATE INDEX IF NOT EXISTS userName_index ON USERS (name);";
		return sqlIndex;
	}
	
	public static String insertUser(UserDescription theUser) {
		StringBuilder sql = new StringBuilder ("INSERT INTO USERS (NAME, PASSWORD, DESCRIPTION, FIRST_NAME, LAST_NAME, EMAIL, CREATION_DATE, LAST_CONNECTION_DATE, IS_ADMIN, IS_PASSWORD_TO_BE_CHANGED) VALUES (");

		DatabaseHelper.appendStringToDatabase(sql, theUser.getName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theUser.extractPassword()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getDescription()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getFirstName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getLastName()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getEMail()); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql, Long.toString(theUser.getCreationDate())); 
		sql.append(",");
		DatabaseHelper.appendStringToDatabase(sql,  Long.toString(theUser.getLastConnectionDate())); 
		sql.append(",");
		DatabaseHelper.appendBooleanToDatabase(sql, theUser.isAdmin()); 
		sql.append(",");
		DatabaseHelper.appendBooleanToDatabase(sql, theUser.isPasswordMustBeChanged()); 

		sql.append(");"); 
		
		return sql.toString();
	}
	
	public static String deleteUser(String userName) {
		StringBuilder sql = new StringBuilder ("DELETE FROM USERS WHERE NAME=");
		DatabaseHelper.appendStringToDatabase(sql, userName); 
		sql.append(";"); 
		
		return sql.toString();
	}
	
	public static String updateUserPassword(UserDescription theUser) {
		StringBuilder sql = new StringBuilder ("UPDATE USERS SET PASSWORD = ");

		DatabaseHelper.appendStringToDatabase(sql, theUser.extractPassword()); 
		sql.append(" WHERE NAME = ");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getName()); 
		sql.append(";"); 
		
		return sql.toString();
	}
	
	public static String updateUser(UserDescription theUser, boolean updatePassword) {
		// DESCRIPTION, FIRST_NAME, LAST_NAME, EMAIL, CREATION_DATE, LAST_CONNECTION_DATE, IS_ADMIN, IS_PASSWORD_TO_BE_CHANGED
		StringBuilder sql = new StringBuilder ("UPDATE USERS SET DESCRIPTION = ");

		DatabaseHelper.appendStringToDatabase(sql, theUser.getDescription()); 
		if (updatePassword == true) {
			sql.append(",");
			sql.append("PASSWORD = ");
			DatabaseHelper.appendStringToDatabase(sql, theUser.extractPassword()); 				
		}
		
		sql.append(",");
		sql.append("FIRST_NAME = ");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getFirstName()); 
		sql.append(",");
		sql.append("LAST_NAME = ");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getLastName()); 
		sql.append(",");
		sql.append("EMAIL = ");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getEMail()); 
		
		if (theUser.hasInvalidConnectionDate() == false) {
			sql.append(",");
			sql.append("LAST_CONNECTION_DATE = ");
			DatabaseHelper.appendStringToDatabase(sql, Long.toString(theUser.getLastConnectionDate())); 
		}
		sql.append(",");
		sql.append("IS_ADMIN = ");
		DatabaseHelper.appendBooleanToDatabase(sql, theUser.isAdmin()); 
		sql.append(",");
		sql.append("IS_PASSWORD_TO_BE_CHANGED = ");
		DatabaseHelper.appendBooleanToDatabase(sql, theUser.isPasswordMustBeChanged()); 
		sql.append(" WHERE NAME = ");
		DatabaseHelper.appendStringToDatabase(sql, theUser.getName()); 
		sql.append(";"); 
		
		return sql.toString();
	}
	
	public static String extractAllUsersFromDatabase() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM USERS;");
		return sql.toString();
	}
	
	public static String extractUserFromDatabase(String userName) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM USERS where name = '" + userName + "';");
		
		return sql.toString();
	}

}
