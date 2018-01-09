package com.seb.networkTopology.generic;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyUtility {
	private static final Logger LOG = LogManager.getLogger(PropertyUtility.class);

	private PropertyUtility() {}
	
	public static String getStringProperties(Properties properties, String propertyName) {
		String propertyValue = properties.getProperty(propertyName);
		if (propertyValue != null) {
			propertyValue = propertyValue.trim();
			LOG.info(propertyName + " = " + propertyValue);						
		} else {
			LOG.error("getStringProperties::Cannot find property " + propertyName);			
		}
		return propertyValue;
	}

	public static String[] getStringArrayProperties(Properties properties, String propertyName) {
		String propertyValue = properties.getProperty(propertyName);
		String[] values = null;
		if (propertyValue != null) {
			propertyValue = propertyValue.trim();
			values = propertyValue.split(",");
			LOG.info(propertyName + " = " + propertyValue);						
		} else {
			LOG.error("getStringArrayProperties::Cannot find property " + propertyName);			
		}
		return values;
	}

	public static double[] getDoubleArrayProperties(Properties properties, String propertyName) {
		String[] values = getStringArrayProperties(properties, propertyName);
		
		double[] result = new double[values.length];
		for (int i=0; i < values.length; i++) {
			result[i] = Double.parseDouble(values[i]);
		}
		
		return result;
	}

	public static double getDoubleProperties(Properties properties, String propertyName) {
		String propertyValue = properties.getProperty(propertyName);
		double propertyDoubleValue = 0.0;
		if (propertyValue != null) {
			propertyValue = propertyValue.trim();
			propertyDoubleValue = Double.parseDouble(propertyValue);
			LOG.info(propertyName + " = " + propertyValue);						
		} else {
			LOG.error("getDoubleProperties::Cannot find property " + propertyName);			
		}
		return propertyDoubleValue;
	}
	
	public static int getIntProperties(Properties properties, String propertyName) {
		String propertyValue = properties.getProperty(propertyName);
		int propertyDoubleValue = 0;
		if (propertyValue != null) {
			propertyValue = propertyValue.trim();
			propertyDoubleValue = Integer.parseInt(propertyValue);
			LOG.info(propertyName + " = " + propertyValue);						
		} else {
			LOG.error("getIntProperties::Cannot find property " + propertyName);			
		}
		return propertyDoubleValue;
	}

	public static boolean getBooleanProperties(Properties properties, String propertyName) {
		String propertyValue = properties.getProperty(propertyName);
		boolean propertyBooleanValue = false;
		if (propertyValue != null) {
			propertyValue = propertyValue.trim();
			if ("true".equalsIgnoreCase(propertyValue)) {
				propertyBooleanValue = true;
			} else {
				propertyBooleanValue = false;
			}
			LOG.info(propertyName + " = " + propertyValue);						
		} else {
			LOG.error("getBooleanProperties::Cannot find property " + propertyName);			
		}
		return propertyBooleanValue;
	}

}
