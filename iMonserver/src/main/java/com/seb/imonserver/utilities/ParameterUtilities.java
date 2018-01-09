package com.seb.imonserver.utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.userManagement.LocalUserManagement;

public class ParameterUtilities {
	
	private static final Logger LOG = LogManager.getLogger(LocalUserManagement.class);

	public final static String PARAMETER_NAME_SEPARATOR = "_PNAME_";
	public final static String PARAMETER_VALUES_SEPARATOR = "_PVALUES_";

	private final static String VALUES_SEPARATOR = ","; 

	private ParameterUtilities() {}
	
	public static String[] splitListOfValues(String allValues) {
		return allValues.split(VALUES_SEPARATOR);
	}

	public static List<ParameterWithValues> parseListOfParameterValues(String listParameterValues) {
		String[] parameterWithValues = listParameterValues.split(ParameterUtilities.PARAMETER_NAME_SEPARATOR);

		List<ParameterWithValues> parametersValuesMap = new  ArrayList<ParameterWithValues>();
		for (String currentParameterWithValues : parameterWithValues) {
			String[] paramValuesContent = currentParameterWithValues.split(ParameterUtilities.PARAMETER_VALUES_SEPARATOR);
			if (paramValuesContent.length != 2) {
				LOG.warn("getCountParameterList::parameter badly formed, it's ignored: "+ currentParameterWithValues);
			} else {
				ParameterWithValues currentParamValues = new ParameterWithValues(paramValuesContent[0], ParameterUtilities.splitListOfValues(paramValuesContent[1]));
				parametersValuesMap.add(currentParamValues);
			}
		}
		return parametersValuesMap;
	}
}
