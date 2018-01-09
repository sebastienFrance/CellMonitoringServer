package com.seb.imonserver.eql;

import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.topologyMgt.TopologyCste;
import com.seb.imonserver.TopologyItf;
import com.seb.imonserver.datamodel.Cell;
import com.seb.imonserver.generic.Credentials;
import com.seb.imonserver.generic.KPIPluginItf;


public class KPIEQLPlugin implements KPIPluginItf{
	private static final Logger LOG = LogManager.getLogger(KPIEQLPlugin.class);

	
	private SimpleEQLRequests _WCDMA_mgr;
	private SimpleEQLRequests _LTE_mgr;
	private SimpleEQLRequests _GSM_mgr;
	
	private TopologyItf _topoItf;
	
	private final static String OTYPE_CELL_LTE = "otype=CELLLTE";
	private final static String OTYPE_CELL_WCDMA = "otype=CELL3G";
	private final static String OTYPE_CELL_GSM = "otype=CELL2G";
	
	private final static String EQL_OPTS_EIDS = "&eids=";
	private final static String EQL_OPTS_PERIODICITY = "&periodicity=";
	private final static String EQL_OPTS_START_DATE = "&firstdate=";
	private final static String EQL_OPTS_END_DATE = "&seconddate=";
	private final static String EQL_OPTS_KPI_LIST = "&datalist=";
	private final static String EQL_OPTS_CSV_EXPORT = "&format=csv";
	private final static String EQL_OPTS_REPORT = "&report=";
	
	
	public  KPIEQLPlugin(TopologyItf topoItf, Credentials LTECredentials, Credentials WCDMACredentials, Credentials GSMCredentials) {
		
		_topoItf = topoItf;
		
		_WCDMA_mgr = new SimpleEQLRequests(WCDMACredentials);

		_LTE_mgr = new SimpleEQLRequests(LTECredentials);

		_GSM_mgr = new SimpleEQLRequests(GSMCredentials);
	}
	
	@Override
	public void getCellKPIs(String cellName, String techno, String KPIs, String periodicity, String startDate, String endDate, OutputStream out) {
		// Look in LTE cell

		Cell currentCell = _topoItf.findCell(cellName, techno);
		
		if (currentCell != null) {

			StringBuilder uriOpt = new StringBuilder();

			SimpleEQLRequests mgr = null;

			addTechno(uriOpt, techno);
			mgr = getManager(techno);

			// To be removed, it's just to relocate data!
			  if (cellName.startsWith("Vr") || cellName.startsWith("Vx") || cellName.startsWith("Po")) {
				  cellName = cellName.substring(2);
			  }



			addListOfCells(uriOpt, cellName);

			addPeriodicity(uriOpt, periodicity);

			addStartEndDate(uriOpt, startDate, endDate);

			addKPIList(uriOpt, KPIs);

			addCSVExport(uriOpt);

			LOG.debug("getCellKPIs::Uri string: " + uriOpt.toString());

			SimpleEQLHandlerItf handler = new LineEQLHandler(out);

			mgr.makeQuery(SimpleEQLRequests.EQL_CMD_REPORT, 
					uriOpt.toString(), 
					SimpleEQLRequests.METHOD_GET, 
					handler);
		}
	}

	@Override
	public void getKPIsForCells(String cellsId, String techno, String KPIs, String periodicity, String startDate, String endDate, OutputStream out) {
		// Look in LTE cell

		LOG.info("getKPIsForCells::start");
		
		SimpleEQLRequests mgr = null;


		StringBuilder uriOpt = new StringBuilder();
		
		addTechno(uriOpt, techno);
		mgr = getManager(techno);


		// To be removed, it's just to relocate data!
		String originalCellsIds = cellsId;
		if (cellsId.startsWith("Vr") || cellsId.startsWith("Vx") || cellsId.startsWith("Po")) {
			cellsId = cellsId.replaceAll("Vr","");
			cellsId = cellsId.replaceAll("Vx","");		  
			cellsId = cellsId.replaceAll("Po","");		  
		}

		addListOfCells(uriOpt, cellsId);

		addPeriodicity(uriOpt, periodicity);

		addStartEndDate(uriOpt, startDate, endDate);

		addKPIList(uriOpt, KPIs);
		
		addCSVExport(uriOpt);

		LOG.debug("getKPIsForCells", "Uri string: " + uriOpt.toString());

		LOG.debug("getKPIsForCells::getKPIsForCells : multiline");
		SimpleEQLHandlerItf handler = new MultiLineEQLHandler(out, originalCellsIds);
		LOG.debug("getKPIsForCells::getKPIsForCells : query");

		//public boolean makeQuery(String cmd, String uriOpt, String method,ILineResponseHandler handler) {
		mgr.makeQuery(SimpleEQLRequests.EQL_CMD_REPORT, 
				uriOpt.toString(), 
				SimpleEQLRequests.METHOD_POST, 
				handler);
	}

	@Override
	public void getWorkingZoneKPIs(String techno, String workingZoneName, String reportName, String periodicity, String startDate, String endDate, OutputStream out) {
		// Look in LTE cell

		StringBuilder uriOpt = new StringBuilder();

		SimpleEQLRequests mgr = null;

		addTechno(uriOpt, techno);
		mgr = getManager(techno);

		addReport(uriOpt, reportName);

		addPeriodicity(uriOpt, periodicity);

		addStartEndDate(uriOpt, startDate, endDate);

		addCSVExport(uriOpt);

		LOG.debug("getWorkingZoneKPIs::Uri string: " + uriOpt.toString());


		SimpleEQLHandlerItf handler = new LineEQLHandler(out);

		mgr.makeQuery(SimpleEQLRequests.EQL_CMD_REPORT, 
				uriOpt.toString(), 
				SimpleEQLRequests.METHOD_GET, 
				handler);
	}

	static private void addPeriodicity(StringBuilder buffer, String periodicity) {
		buffer.append(EQL_OPTS_PERIODICITY);
		buffer.append(periodicity);

	}
	
	static private void addListOfCells(StringBuilder buffer, String listOfCells) {
		buffer.append(EQL_OPTS_EIDS);
		buffer.append(listOfCells);
	}

	static private void addKPIList(StringBuilder buffer, String KPIs) {
		buffer.append(EQL_OPTS_KPI_LIST);
		buffer.append(KPIs);
	}
	
	static private void addStartEndDate(StringBuilder buffer, String startDate, String endDate) {
		buffer.append(EQL_OPTS_START_DATE);
		buffer.append(startDate.replace(" ", "%20"));

		if ((endDate != null) && (endDate.equals("") == false)) {
			buffer.append(EQL_OPTS_END_DATE);
			buffer.append(endDate.replace(" ", "%20"));
		}
		
	}
	
	static private void addCSVExport(StringBuilder buffer) {
		buffer.append(EQL_OPTS_CSV_EXPORT);
	}
	static private void addReport(StringBuilder buffer, String reportName) {
		buffer.append(EQL_OPTS_REPORT);
		buffer.append(reportName);
	}

	static private void addTechno(StringBuilder buffer, String techno) {
		if (techno.equals(TopologyCste.TECHNO_LTE)) {
			buffer.append(OTYPE_CELL_LTE);
		} else  {
			if (techno.equals(TopologyCste.TECHNO_WCDMA)) {
				buffer.append(OTYPE_CELL_WCDMA);
			} else {
				if (techno.equals(TopologyCste.TECHNO_GSM)) {
					buffer.append(OTYPE_CELL_GSM);
				} else {
					LOG.warn("addTechno", "Error: unknown techno for cell");
				}
			}
		}

	}
	
	private SimpleEQLRequests getManager(String techno) {
		if (techno.equals(TopologyCste.TECHNO_LTE)) {
			return _LTE_mgr;
		} else  {
			if (techno.equals(TopologyCste.TECHNO_WCDMA)) {
			return _WCDMA_mgr;
			} else {
				if (techno.equals(TopologyCste.TECHNO_GSM)) {
					return _GSM_mgr;
				} else {
					LOG.warn("getManager", " Error: unknown techno for cell");
					return null;
				}
			}
		}

	}
	
}
