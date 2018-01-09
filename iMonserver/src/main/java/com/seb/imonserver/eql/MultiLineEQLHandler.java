package com.seb.imonserver.eql;

import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.generic.CellKPIRow;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class MultiLineEQLHandler implements SimpleEQLHandlerItf {
	private static final Logger LOG = LogManager.getLogger(MultiLineEQLHandler.class);

	private OutputStream _out;
	JSONArray _newArray;
	
	private boolean _firstRow;
	private String[] _ids;
	private String[] _idsToBeUsed;

	private String _cellPrefix = null;
	
	MultiLineEQLHandler(OutputStream out, String ids) {
		_out = out;
		_newArray = new JSONArray();
		_firstRow = false;
		_ids = ids.split(",");
		if (_ids.length > 0) {
			String firstCellName = _ids[0];
			if (firstCellName.startsWith("Vr")) {
				_cellPrefix="Vr";
			} else if (firstCellName.startsWith("Vx")) {
				_cellPrefix="Vx";
			} else if (firstCellName.startsWith("Po")) {
				_cellPrefix="Po";
			} else {
				_cellPrefix = null;
			}
		}
	}

	@Override
	public void processRow(String line) {
		//System.String[]err.println("handle(): " + line);
		// first row contains only 
		// ,DCU2085X,DCU2085Y,DCU2085X,DCU2085Y,DCU2085X,DCU2085Y,DCU2085X...
		if (_firstRow == false) {
			_firstRow = true;
			String[] idsFromEQL = line.split(",");
			_idsToBeUsed = new String[_ids.length];
			for (int i = 0 ; i < _idsToBeUsed.length; i++) {
				_idsToBeUsed[i] = idsFromEQL[i+1]; // ignore first entry from the split because it's empty
			}
		} else {
			int indexComa = line.indexOf(',');
			int indexParenthesis = line.indexOf('(');
			String KPIName = line.substring(0, indexParenthesis);
			String KPIValues = line.substring(indexComa+1, line.length());
			
			
			StringBuilder[] KPISValuesPerCell = new StringBuilder[_ids.length];
			
			String[] splitKPIValues = KPIValues.split(",");
			for (int i = 0; i < splitKPIValues.length;) {
				for (int j = 0; j < _idsToBeUsed.length; j++) {
					StringBuilder currentStringForCell = KPISValuesPerCell[j];
					if (currentStringForCell == null) {
						currentStringForCell = new StringBuilder();
						KPISValuesPerCell[j] = currentStringForCell;
					}
					currentStringForCell.append(splitKPIValues[i++]);
					currentStringForCell.append(",");
				}
			}
			
			// now we can rebuild for each cells the list of KPIs
			for (int j = 0; j < _idsToBeUsed.length; j++) {
				String KPIs = KPISValuesPerCell[j].toString();
				//System.err.println("CellName: " + cellIds[j] + " KPIs: " +  KPIs.substring(0, KPIs.length()-1));
				String cellName;
				String oexid = _idsToBeUsed[j];
				if (oexid.startsWith("CELLLTE") || oexid.startsWith("CELL3G")) {
					oexid = oexid.replaceAll("CELLTE","");
					oexid = oexid.replaceAll("CELL3G","");		  
				}

				if (_cellPrefix != null) {
					
					StringBuilder buffCellName = new StringBuilder(_cellPrefix);
					buffCellName.append(oexid);
					cellName = buffCellName.toString();
				} else {
					cellName = oexid;
				}
				CellKPIRow currentRow = new CellKPIRow(cellName, KPIName, KPIs.substring(0, KPIs.length()-1));
				JSONObject newObject = JSONObject.fromObject(currentRow);
				_newArray.add(newObject);
			}
		}
	}
	
	@Override
	public void processError() {
		try {
		_out.close();
		LOG.debug("processError(): ");
		}
		catch (Exception ex) {
			LOG.error(ex);
		}
	}
	
	@Override
	public void endOfData() {
		try {
			_out.write(_newArray.toString().getBytes());	
			_out.flush();
			_out.close();
		}
		catch (Exception ex) {
			LOG.error(ex);
		}
	}

}

