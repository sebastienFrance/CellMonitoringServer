package com.seb.imonserver.eql;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.generic.KPISimpleRow;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LineEQLHandler implements SimpleEQLHandlerItf {
	private static final Logger LOG = LogManager.getLogger(LineEQLHandler.class);

	private OutputStream _out;
	JSONArray _newArray;

	LineEQLHandler(OutputStream out) {
		_out = out;
		_newArray = new JSONArray();
	}

	@Override
	public void processRow(String line) {
		int indexComa = line.indexOf(',');
		int indexParenthesis = line.indexOf('(');
		String KPIName = line.substring(0, indexParenthesis);
		String KPIStringValues = line.substring(indexComa+1, line.length());

		List<Float> KPIValues = RowIndicatorsToArrayFloat(KPIStringValues);
		
		KPISimpleRow currentRow = new KPISimpleRow(KPIName, KPIValues);
		JSONObject newObject = JSONObject.fromObject(currentRow);
		_newArray.add(newObject);	
	}

	@Override
	public void processError() {
		try {
			_out.close();
			LOG.debug("processError::");
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

	
	private static List<Float> RowIndicatorsToArrayFloat(String line) {
		String[] values = line.split(",");
		List<Float> KPIValues = new ArrayList<Float>();
		for (String currStringValues : values) {
			try {
				if (currStringValues.endsWith("%")) {
					currStringValues = currStringValues.substring(0, currStringValues.length()-1);
				}
				Float currentFloatValue = Float.parseFloat(currStringValues);
				KPIValues.add(currentFloatValue);
			} catch (Exception ex) {
				LOG.error("RowIndicatorsToArrayFloat::error invalid value: " + currStringValues, ex);
				KPIValues.add(null);
			}
		}
		return KPIValues;
	}

}
