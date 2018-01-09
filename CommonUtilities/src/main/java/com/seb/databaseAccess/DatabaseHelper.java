package com.seb.databaseAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;


public class DatabaseHelper {
	private static final Logger LOG = LogManager.getLogger(DatabaseHelper.class);

	private DatabaseHelper() {}
	
	/**
	 * Open the USER database
	 * 
	 * @param DatabaseFullPathName Full path of the database
	 * @return Connection when the database has been opened else it returns null
	 */
	public static Connection openDatabaseConnection(String DatabaseFullPathName) {
		Connection theConnection = null;
		try {
			 SQLiteConfig config = new SQLiteConfig();
			 config.enableLoadExtension(true);

			Class.forName("org.sqlite.JDBC");
			theConnection = DriverManager.getConnection("jdbc:sqlite:" + DatabaseFullPathName , config.toProperties());

			theConnection.setAutoCommit(false);
		}
		catch (Exception ex) {
			LOG.error(ex);;
		}
		return theConnection;
	}

	public static void appendStringToDatabase(StringBuilder buffer, String value) {
		buffer.append("'");
		buffer.append(value);
		buffer.append("'");
		
	}

	public static void appendBooleanToDatabase(StringBuilder buffer, boolean value) {
		buffer.append("'");
		if (value == true) {
			buffer.append("1");
		} else {
			buffer.append("0");
		}
		buffer.append("'");
		
	}

	public static boolean getBooleanFromDatabase(int valueInDB) {
		if (valueInDB == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static String buildInStatement(String[] inValues) {
		StringBuilder inStatement = new StringBuilder(" in (");
		
		// add list of inValues 
		for (int i = 0 ; i < (inValues.length - 1); i++) {
			inStatement.append("\"" + inValues[i] + "\"" + ",");
		}
		inStatement.append("\"" + inValues[(inValues.length -1)] + "\"" + ") ");
		
		return inStatement.toString();
	}
	
	/*
	public static String buildInStatement(List<Long> inValues) {
		StringBuilder inStatement = new StringBuilder(" in (");
		
		// add list of inValues 
		for (int i = 0 ; i < inValues.size() - 1; i++) {
			inStatement.append("\"" + inValues.get(i) + "\"" + ",");
		}
		inStatement.append("\"" + inValues.get(inValues.size() - 1) + "\"" + ") ");
		
		return inStatement.toString();
	}
	*/
	
	public static String buildInStatement(List<Long> inValues) {
		StringBuilder inStatement = new StringBuilder(" in (");
		
		// add list of inValues 
		for (int i = 0 ; i < inValues.size() - 1; i++) {
			inStatement.append(inValues.get(i) + ",");
		}
		inStatement.append(inValues.get(inValues.size() - 1) + ") ");
		
		return inStatement.toString();
	}
	
	public static String buildOrStatements(List<Long> orValues, String columnName) {
		if (orValues == null || orValues.size() == 0) {
			return "";
		} else if (orValues.size() == 1) {
			return " " + columnName + " = " + orValues.get(0); 
		} else {
			StringBuilder orStatement = new StringBuilder(" ");
			for (int i = 0; i < orValues.size() - 1; i++) {
				orStatement.append(" " + columnName + " = " + orValues.get(i) + " or ");
			}
			orStatement.append(columnName + " = " + orValues.get(orValues.size() - 1));
			return orStatement.toString();
		}
	}

}
