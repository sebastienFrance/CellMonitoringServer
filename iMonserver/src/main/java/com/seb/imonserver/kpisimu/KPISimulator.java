package com.seb.imonserver.kpisimu;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * List of service to generate random values for KPIs
 * 
 * @author Sebastien Brugalieres
 *
 */
public class KPISimulator {
	private static final Logger LOG = LogManager.getLogger(KPISimulator.class);

	private final static int KPI_NAME_INDEX = 0;
	private final static int KPI_UNIT_INDEX = 1;
	private final static int KPI_MIN_VALUE = 2;
	private final static int KPI_MAX_VALUE = 3;
	
	private final static String KPI_TYPE_PERCENTAGE ="%";
	private final static String KPI_TYPE_INTEGER ="int";
	
	private final static String KPI_PERIODICITY_15MN = "15mn";
	private final static String KPI_PERIODICITY_HOURLY = "h";
	private final static String KPI_PERIODICITY_DAILY = "d";
	private final static String KPI_PERIODICITY_WEEKLY = "w";
	private final static String KPI_PERIODICITY_MONTHLY = "m";
	
	private String[] _KPIDefinition;
	private Random _rand;
	
	public KPISimulator(String[] rowContent) {
		 _rand = new Random();
		_KPIDefinition = rowContent;
	}
	
	public String getName() {
		return _KPIDefinition[KPI_NAME_INDEX];
	}
	
	public ArrayList<Float> generateSimpleRandomValue(String periodicity) {
		int numberOfValues;
		int factor;
		
		if (periodicity.equals(KPI_PERIODICITY_15MN)) {
			 numberOfValues = 24;
			 factor = 1;
		} else if (periodicity.equals(KPI_PERIODICITY_HOURLY)) {
			 numberOfValues = 24;
			 factor = 4;
		} else if (periodicity.equals(KPI_PERIODICITY_DAILY)) {
			 numberOfValues = 7;
			 factor = 96;
		} else if (periodicity.equals(KPI_PERIODICITY_WEEKLY)) {
			 numberOfValues = 4;
			 factor = 96*7;
		} else if (periodicity.equals(KPI_PERIODICITY_MONTHLY)) {
			 numberOfValues = 6;
			 factor = 96*7*30;
		} else {
			LOG.warn("generateSimpleRandomValue::Error: KPISimulator with unknown periodicity : " + periodicity);
			return null;
		}
	
		ArrayList<Float> values = new ArrayList<Float>();
		for (int i = 0 ; i < numberOfValues; i++) {
			values.add(generateSimpleRandomValue(factor));
		}
		
		return values;
	}
	
	public String generateRandomValue(String periodicity, boolean endWithComma) {
		int numberOfValues;
		int factor;
		
		if (periodicity.equals(KPI_PERIODICITY_15MN)) {
			 numberOfValues = 24;
			 factor = 1;
		} else if (periodicity.equals(KPI_PERIODICITY_HOURLY)) {
			 numberOfValues = 24;
			 factor = 4;
		} else if (periodicity.equals(KPI_PERIODICITY_DAILY)) {
			 numberOfValues = 7;
			 factor = 96;
		} else if (periodicity.equals(KPI_PERIODICITY_WEEKLY)) {
			 numberOfValues = 4;
			 factor = 96*7;
		} else if (periodicity.equals(KPI_PERIODICITY_MONTHLY)) {
			 numberOfValues = 6;
			 factor = 96*7*30;
		} else {
			LOG.warn("generateRandomValue::Error: KPISimulator with unknown periodicity : " + periodicity);
			return null;
		}
		
		StringBuilder values = new StringBuilder();
		for (int i = 0 ; i < numberOfValues; i++) {
			values.append(generateRandomValue(factor));
			if (i < (numberOfValues -1)) {
				values.append(',');
			} else {
				if (endWithComma == true) {
					values.append(',');					
				}
			}
		}
		
		return values.toString();
	}
	
	public String generateRandomValue() {
		return generateRandomValue(1);
	}
	public Float generateSimpleRandomValue() {
		return generateSimpleRandomValue(1);
	}
	
	public String generateRandomValue(int factor) {
		if (_KPIDefinition[KPI_UNIT_INDEX].equals(KPI_TYPE_PERCENTAGE)) {
			return generateRandomPercentage();
		} else if (_KPIDefinition[KPI_UNIT_INDEX].equals(KPI_TYPE_INTEGER)) {
			return generateRandomInteger(factor);
		} else {
			LOG.warn("generateRandomValue::Warning: unknown type for KPI Simulator: " + getName());
			return "0.0";
		}
	}

	public Float generateSimpleRandomValue(int factor) {
		if (_KPIDefinition[KPI_UNIT_INDEX].equals(KPI_TYPE_PERCENTAGE)) {
			return generateSimpleRandomPercentage();
		} else if (_KPIDefinition[KPI_UNIT_INDEX].equals(KPI_TYPE_INTEGER)) {
			return generateSimpleRandomInteger(factor);
		} else {
			LOG.warn("generateSimpleRandomValue::Warning: unknown type for KPI Simulator: " + getName());
			return new Float(0.0);
		}
	}
	private String generateRandomPercentage() {
		Float value = generateSimpleRandomPercentage();
		return String.format(Locale.US, "%.2f%%", value); // need US local to have dot as separator
	}
	
	private Float generateSimpleRandomPercentage() {
		// Example assumes these variables have been initialized
		// above, e.g. as method parameters, fields, or otherwise
		// such as: rand = new Random();
		
		
		float minFloat = (Float.parseFloat(_KPIDefinition[KPI_MIN_VALUE])) * 100; // Max 2 digits after .
		float maxFloat = (Float.parseFloat(_KPIDefinition[KPI_MAX_VALUE])) *100;
		
		int min = (int) minFloat;
		int max = (int) maxFloat;

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = _rand.nextInt(max - min + 1) + min;
		
		float randomNumFloat = (float) (randomNum) / 100;
		
		return new Float(randomNumFloat); 
	}

	private String generateRandomInteger(int factor) {
		Float value = generateSimpleRandomInteger(factor);		
		return value.toString();
		
	}
	
	private Float generateSimpleRandomInteger(int factor) {
		int min = Integer.parseInt(_KPIDefinition[KPI_MIN_VALUE]); // Max 2 digits after .
		int max = Integer.parseInt(_KPIDefinition[KPI_MAX_VALUE]);
		
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = (_rand.nextInt(max - min + 1) + min) * factor;
		
		return new Float(randomNum);
		
	}

}
