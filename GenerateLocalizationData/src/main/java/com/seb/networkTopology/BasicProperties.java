package com.seb.networkTopology;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkTopology.generic.PropertyUtility;

public abstract class BasicProperties {
	private static final Logger LOG = LogManager.getLogger(BasicProperties.class);


	private String _outputDirectory = null;
	private String _outputDatabaseFileName = null;
	
	private String _outputDatasourceDirectory = null;
	private String _outputDatasourceDatabaseFileName = null;

	
	 public String getOutputDirectory() {
		return _outputDirectory;
	}

	 public String getOutputDatabaseFileName() {
		return _outputDatabaseFileName;
	}

	 public String getOutputDatasourceDirectory() {
		return _outputDatasourceDirectory;
	}

	 public String getOutputDatasourceDatabaseFileName() {
		return _outputDatasourceDatabaseFileName;
	}

	static private final String PROP_OUTPUT_DIRECTORY ="outputDirectory";
	static private final String PROP_OUTPUT_DATABASE_FILENAME ="outputDatabaseFileName";

	static private final String PROP_OUTPUT_DS_DIRECTORY ="outputDatasourceDirectory";
	static private final String PROP_OUTPUT_DS_DATABASE_FILENAME ="outputDatasourceDatabaseFileName";

	
	protected BasicProperties() {		
	}
	
	public void initialize(String propertyFileName) {
		Properties properties = new Properties();
		try {
		    properties.load(new FileInputStream(propertyFileName));
		} catch (IOException e) {
			LOG.fatal("Cannot open property file", e);
			System.exit(1);
		}
		
		_outputDirectory = PropertyUtility.getStringProperties(properties, PROP_OUTPUT_DIRECTORY);	
		_outputDatabaseFileName = PropertyUtility.getStringProperties(properties, PROP_OUTPUT_DATABASE_FILENAME);

		_outputDatasourceDirectory = PropertyUtility.getStringProperties(properties, PROP_OUTPUT_DS_DIRECTORY);	
		_outputDatasourceDatabaseFileName = PropertyUtility.getStringProperties(properties, PROP_OUTPUT_DS_DATABASE_FILENAME);

		getSpecificProperties(properties);
		
	}
	
	abstract protected void getSpecificProperties(Properties properties);
	

}