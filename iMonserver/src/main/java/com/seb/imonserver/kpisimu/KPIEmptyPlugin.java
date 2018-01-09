package com.seb.imonserver.kpisimu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.datasources.Datasource;
import com.seb.datasources.DatasourceHelper;
import com.seb.imonserver.database.DatabaseQuery;
import com.seb.imonserver.database.DatabaseUtility;
import com.seb.imonserver.generic.CellDSInfo;
import com.seb.imonserver.generic.CellKPIRow;
import com.seb.imonserver.generic.KPIPluginItf;
import com.seb.imonserver.generic.KPISimpleRow;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sebastien Brugalieres
 *
 */
public class KPIEmptyPlugin implements KPIPluginItf {
	private static final Logger LOG = LogManager.getLogger(KPIEmptyPlugin.class);

	private Map<String, KPISimulator> _KPIsSimulator;
	private String _networkDatabaseName;
	
	private final static int ROW_FIELD_NUMBER = 4;
	
	public KPIEmptyPlugin(String KPISimuFileName, String networkDatabaseName) {
		loadKPISimuFile(KPISimuFileName);
		_networkDatabaseName = networkDatabaseName;
	}
	
	  private void loadKPISimuFile(String KPISimuFileName) {
		  LOG.info("loadKPISimuFile::Start");

		  _KPIsSimulator = new HashMap<String, KPISimulator>();

		  try {
			  FileReader zoneFile = new FileReader(KPISimuFileName);
			  BufferedReader reader = new BufferedReader (zoneFile);
			  String currentLine;
			  
			  while ((currentLine = reader.readLine()) != null) {
				  // split current line 
				  
				  String[] rowContent = currentLine.split(";");
				  if (rowContent.length != ROW_FIELD_NUMBER) {
					  LOG.warn("loadKPISimuFile::loadKPISimuFile: ignore row with => " + currentLine);
					  continue;
				  }
				  
				  KPISimulator newKPISimu = new KPISimulator(rowContent);
				  _KPIsSimulator.put(newKPISimu.getName(), newKPISimu);
			  }
			  reader.close();
			  
		  } catch (Exception ex){
			  LOG.error(ex);
		  }
		  LOG.info("loadKPISimuFile::End");
	  }


	  @Override
	  public void getCellKPIs(String cellName, String techno, String KPIs, String periodicity, String startDate, String endDate, OutputStream out) {

		  Datasource DS = getDatasourceForCell(cellName);	  

		  JSONArray newArray = new JSONArray();

			String[] KPINames = KPIs.split(",");
			for (String KPIName : KPINames) {

				KPISimulator currentSimu = _KPIsSimulator.get(KPIName);
				if (currentSimu == null) {
					LOG.warn("getCellKPIs::Error: cannot find KPISimulator for " + KPIName);
				}

				ArrayList<Float> KPIValues = currentSimu.generateSimpleRandomValue(periodicity);
				KPISimpleRow currentRow = new KPISimpleRow(KPIName, KPIValues);			
				JSONObject newObject = JSONObject.fromObject(currentRow);
				newArray.add(newObject);	
			}
			
			LOG.info("getCellKPIs::End generating data");

			try {
				out.write(newArray.toString().getBytes());	
				out.flush();
				out.close();
			}
			catch (Exception ex) {
				  LOG.error(ex);
			}
			LOG.info("getCellKPIs::End sending data");
		  
	  }
	  
	  
	  private  Datasource getDatasourceForCell(String cellName) {
		  Datasource DS = null;
		  try (Connection theConnection = DatabaseUtility.openDatabaseConnection(_networkDatabaseName)) {
			  
			  CellDSInfo cellDatasource = DatabaseQuery.getCellDSInfoByCellName(theConnection, cellName);
			  DS = DatasourceHelper.getInstance().getDatasourceFor(cellDatasource.getQOSDatasource());
			  if (DS == null) {
				  LOG.warn("getDatasourceForCell::Cannot find QOS datasource for Cell: " + cellName);
			  } 
			  
		  } catch (Exception ex) {
			  LOG.error(ex);
		  } 
		  
		  return DS;
	  }
	  
	  private void getDatasourcesForCells(String[] cellNames) {
		  try (Connection theConnection = DatabaseUtility.openDatabaseConnection(_networkDatabaseName)) {
			  
			  List<CellDSInfo> cellDatasources = DatabaseQuery.getCellDSInfoByCellNames(theConnection, cellNames);
			  
			  for (CellDSInfo currentDSInfo : cellDatasources) {
				  
				  Datasource DS = DatasourceHelper.getInstance().getDatasourceFor(currentDSInfo.getQOSDatasource());
				  if (DS == null) {
					  LOG.warn("getDatasourcesForCells::Cannot find QOS datasource for Cell: " + currentDSInfo.getCellName());
				  } 
			  }
		  } catch (Exception ex) {
			  LOG.error(ex);
		  } 
		  
	  }

	  
	  
	  @Override
	  public void getKPIsForCells(String cellsId, String techno, String KPIs, String periodicity, String startDate, String endDate, OutputStream out) {

		  JSONArray newArray = new JSONArray();
		  String[] cellNames = cellsId.split(",");
		  String[] KPINames = KPIs.split(",");

		  LOG.info("getKPIsForCells::Start get datasources");

		  getDatasourcesForCells(cellNames);

		  LOG.info("getKPIsForCells::End get datasources");

		  for (String currentCellName : cellNames) {
			  for (String currentKPIName : KPINames) {
				  KPISimulator currentSimu = _KPIsSimulator.get(currentKPIName);
				  if (currentSimu == null) {
					  LOG.warn("getKPIsForCells::Error: cannot find KPISimulator for " + currentKPIName);
				  }

				  String KPIValues = currentSimu.generateRandomValue(periodicity, false);

				  CellKPIRow currentRow = new CellKPIRow(currentCellName, currentKPIName, KPIValues);
				  JSONObject newObject = JSONObject.fromObject(currentRow);
				  newArray.add(newObject);	
			  }

		  }
		  LOG.info("getKPIsForCells::End generating data");

		  try {
			  out.write(newArray.toString().getBytes());	
			  out.flush();
			  out.close();
		  }
		  catch (Exception ex) {
			  LOG.error(ex);
		  }
		  LOG.info("getKPIsForCells::End sending data");
	  }

	  @Override
	  public void getWorkingZoneKPIs(String techno, String workingZoneName, String reportName, String periodicity, String startDate, String endDate, OutputStream out) {
		  LOG.info("getWorkingZoneKPIs::Start");
		  try {
			  out.close();
		  } 
		  catch (Exception ex) {
			  LOG.error(ex);
		  }
		  LOG.info("getWorkingZoneKPIs::End");
	  }
}