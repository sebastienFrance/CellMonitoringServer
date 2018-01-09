package com.seb.datasources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DatasourceHelper {
	private static final Logger LOG = LogManager.getLogger(DatasourceHelper.class);
	
	private String _databaseFullPathName;
	
	private Map<Integer, Datasource> _id2Datasource;
	private Map<Integer, DatasourceTopology> _id2DatasourceTopology;
	
	private static DatasourceHelper _instance = null;
	
	private DatasourceHelper() {
	}
	
	/**
	 * Singleton DatasourceHelper
	 * 
	 * @return The DatasourceHelper to read the content of the Datasource database
	 */
	public static DatasourceHelper getInstance() {
		if (_instance == null) {
			_instance = new DatasourceHelper();
		}
		
		return _instance;
	}
	
	
	/**
	 * Must be called before to use the accessors to get data
	 * 
	 * @param databaseFullPathName
	 * @return true when the data have been correctly loaded / initialized else it returns false
	 */
	public boolean initialize(String databaseFullPathName) {
		_databaseFullPathName = databaseFullPathName;

		return loadDatasources();
	}

	/**
	 * This method loads the datasources in Memory.
	 * @return true when the datasource have been successfully loaded else return false
	 */
	public boolean loadDatasources() {
		_id2Datasource = new HashMap<Integer, Datasource>();
		_id2DatasourceTopology = new HashMap<Integer, DatasourceTopology>();

		List<Datasource> datasourceList = DatabaseDatasourceUtility.extractDatasourceObjects(_databaseFullPathName);
		if (datasourceList == null) {
			return false;
		}
		
		for (Datasource currentDatasource : datasourceList) {
			_id2Datasource.put(currentDatasource.getId(), currentDatasource);
			
			List<DatasourceTopology> DSTopologyList = currentDatasource.getTopologyDatasources();
			if (DSTopologyList != null) {
				for (DatasourceTopology currentDatasourceTopology : DSTopologyList) {
					_id2DatasourceTopology.put(currentDatasourceTopology.getId(), currentDatasourceTopology);
				}
			} else {
				LOG.warn("loadDatasources::Warning: no topology datasource for DS:" + currentDatasource.getName());
			}
		}
		return true;
	}
	
	public Datasource getDatasourceFor(int id) {
		return _id2Datasource.get(id);
	}
	
	public DatasourceTopology getDatasourceTopologyFor(int id) {
		return _id2DatasourceTopology.get(id);
	}
}
