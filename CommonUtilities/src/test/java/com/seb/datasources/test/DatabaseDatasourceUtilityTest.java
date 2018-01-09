package com.seb.datasources.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.seb.databaseAccess.*;


import com.seb.datasources.*;


public class DatabaseDatasourceUtilityTest {
	
	private static final String databasePathName = "/Users/Sebastien/Desktop/test.db"; 
	
	private static Datasource[] _DS;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void databaseTest() {
		checkDatabaseCreation();
		checkInsertDatasource();
		checkExtractDatasources();
	}
	
	private void checkDatabaseCreation() {
		Connection theConnection = DatabaseDatasourceUtility.createDatabase(databasePathName);
		assertNotNull("Database connection should be not null", theConnection);	

		try {
			theConnection.commit();
			theConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void checkInsertDatasource() {
		Connection theConnection = DatabaseHelper.openDatabaseConnection(databasePathName);
		
		_DS = new Datasource[2];
		
		_DS[0] = new Datasource("Name", "Description", Datasource.DS_TECHNO_LTE, "192.168.0.1", "192.168.0.2", 8080, "userName", "password");
		
		DatabaseDatasourceUtility.insertDatasource(theConnection, _DS[0]);

		_DS[1] = new Datasource("Name2", "Description2", 1, "192.168.0.4", "192.168.0.5", 8080, "userName2", "password2");
		DatabaseDatasourceUtility.insertDatasource(theConnection, _DS[1]);

		try {
			theConnection.commit();
			theConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void checkExtractDatasources() {
		
		List<Datasource> dsList = DatabaseDatasourceUtility.extractDatasourceObjects(databasePathName);
		assertTrue("Datasource in DB", dsList.size() == 2);
		DatabaseDatasourceUtility.deleteDatabase(databasePathName);

		for (Datasource currentDatasource : dsList) {
			Datasource matchingDS = null;
			for (int i = 0; (i < _DS.length) && (matchingDS == null); i++) {
				if (_DS[i].getName().equals(currentDatasource.getName())) {
					matchingDS = _DS[i];
				}
			}
			assertNotNull("DS Found", matchingDS);
			assertEquals("DS equality", matchingDS, currentDatasource);
		}
		
	}

}
