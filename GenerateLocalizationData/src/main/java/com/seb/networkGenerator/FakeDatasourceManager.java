package com.seb.networkGenerator;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.databaseAccess.DatabaseHelper;
import com.seb.datasources.DatabaseDatasourceUtility;
import com.seb.datasources.Datasource;
import com.seb.datasources.DatasourceTopology;
import com.seb.utilities.Technology;

public class FakeDatasourceManager {
	private static final Logger LOG = LogManager.getLogger(FakeDatasourceManager.class);

    private static final FakeDatasourceManager myInstance = new FakeDatasourceManager();

	private  Datasource _LTEDatasource;
	private  DatasourceTopology _LTEDatasourceTopology;
	private  Datasource _WCDMADatasource;
	private  DatasourceTopology _WCDMADatasourceTopology;
	private  Datasource _GSMDatasource;
	private  DatasourceTopology _GSMDatasourceTopology;

	
    private FakeDatasourceManager() {}

    public static FakeDatasourceManager getInstance() {
        return myInstance; 
    }
    
    
	public void initializeWith(NetworkGeneratorProperties instanceProperties, boolean openExistingDatabase) {
		try (Connection theConnection  = intializeDatasourceDatabase(instanceProperties, openExistingDatabase)) {
			if (theConnection == null) {
				LOG.fatal("main::Cannot create the Datasource database");
				return;
			}

			insertFakeDatasource(theConnection);
			theConnection.commit();
		} catch (SQLException e) {
			LOG.error(e);
		}
	}
	
	public Datasource getDatasource(Technology techno) {
		switch (techno) {
			case LTE: {
				return _LTEDatasource;
			}
			case WCDMA: {
				return _WCDMADatasource;
			}
			case GSM: {
				return _GSMDatasource;
			}
			default:
				return null;
		}
	}
	
	public DatasourceTopology getDatasourceTopology(Technology techno) {
		switch (techno) {
			case LTE: {
				return _LTEDatasourceTopology;
			}
			case WCDMA: {
				return _WCDMADatasourceTopology;
			}
			case GSM: {
				return _GSMDatasourceTopology;
			}
			default:
				return null;
		}
	}

	
	private static Connection intializeDatasourceDatabase(NetworkGeneratorProperties instanceProperties, boolean openExistingDatabase) {
		String datasourceFullName = instanceProperties.getOutputDatasourceDirectory() + "/" + instanceProperties.getOutputDatasourceDatabaseFileName();
		if (openExistingDatabase == false) {
			return DatabaseDatasourceUtility.createDatabase(datasourceFullName);
		} else {
			return DatabaseHelper.openDatabaseConnection(datasourceFullName);
		}
	}
	
	private void insertFakeDatasource(Connection theConnection) {
		_LTEDatasource = createDatasource(theConnection, "LTE Generated", Datasource.DS_TECHNO_LTE);
		_LTEDatasourceTopology = createDatasourceTopology(theConnection, _LTEDatasource, "LTE Generated Topology");
		
		_WCDMADatasource = createDatasource(theConnection, "WCDMA Generated", Datasource.DS_TECHNO_WCDMA);
		_WCDMADatasourceTopology = createDatasourceTopology(theConnection, _WCDMADatasource, "WCDMA Generated Topology");

		_GSMDatasource = createDatasource(theConnection, "GSM Generated", Datasource.DS_TECHNO_GSM);
		_GSMDatasourceTopology = createDatasourceTopology(theConnection, _GSMDatasource, "GSM Generated Topology");
	}

	/**
	 * Create Fake datasources in database 
	 * 
	 * @param name
	 * @param techno
	 * @return Datasource
	 */
	private static Datasource createDatasource(Connection theConnection, String name, int techno) {
		Datasource newDS = DatabaseDatasourceUtility.createFakeDatasource(name, techno);
		
		DatabaseDatasourceUtility.insertDatasource(theConnection, newDS);
		int id = DatabaseDatasourceUtility.getLastRowId(theConnection);
		newDS.setId(id);
		return newDS;
	}
	
	/**
	 * Create Fake DatasourceTopology in database
	 * @param parentDS
	 * @param name
	 * @return
	 */
	private static DatasourceTopology createDatasourceTopology(Connection theConnection, Datasource parentDS, String name) {
		DatasourceTopology newDSTopology = DatabaseDatasourceUtility.createFakeDatasourceTopology(parentDS, name);
		
		DatabaseDatasourceUtility.insertDatasourceTopology(theConnection, newDSTopology);
		int id = DatabaseDatasourceUtility.getLastRowId(theConnection);
		newDSTopology.setId(id);
		return newDSTopology;
	}
	
}