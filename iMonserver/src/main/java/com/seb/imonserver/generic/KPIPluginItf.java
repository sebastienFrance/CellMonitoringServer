package com.seb.imonserver.generic;

import java.io.OutputStream;

/**
 * Interface that defines the web services related to KPI that must be implemented
 * 
 * @author Sebastien
 *
 */
public interface KPIPluginItf {
	  public void getCellKPIs(String cellName, String techno, String KPIs, String periodicity, String startDate, String endDate, OutputStream out);
	  public void getKPIsForCells(String cellsId, String techno, String KPIs, String periodicity, String startDate, String endDate, OutputStream out);
	  public void getWorkingZoneKPIs(String techno, String workingZoneName, String reportName, String periodicity, String startDate, String endDate, OutputStream out);
}
