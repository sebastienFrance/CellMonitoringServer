package com.seb.imonserver.kpisimu;



import java.util.Date;
import java.util.Random;

import com.seb.imonserver.datamodel.Alarm;


public class AlarmSimulator {

	private AlarmSimulator() {}
	
	public static Alarm getRandomAlarm(int timeOffsetInSec) {
		
		int alarmSeverity = getRandomAlarmSeverity();
		String pbCause = getRandomPbCause();
		
		Date currentTime = new Date();
		long currentTimeFrom1970 = currentTime.getTime() + timeOffsetInSec * 1000;
		
		
		int alarmType = getRandomAlarmType();
		//  Alarm currentAlarm = new Alarm(i,"PbCause" + i, i);
		
		String additionalText = "This is an additional text related to the alarm to give much more details";
		
		Boolean isAcknowledged = getRandomIsAcknowledged();
		  
		return new Alarm(alarmSeverity, pbCause, currentTimeFrom1970, alarmType, additionalText, isAcknowledged);  
	}
	

	
	private static int ALARM_SEVERITY_CLEARED = 0;
	private static int ALARM_SEVERITY_WARNING = 1;
	private static int ALARM_SEVERITY_MINOR = 2;
	private static int ALARM_SEVERITY_MAJOR = 3;
	private static int ALARM_SEVERITY_CRITICAL = 4;
	
	
	private static int getRandomAlarmSeverity() {
		int randomValue = generateRandomInteger(0,100);
		
		if (randomValue <= 15) {
			return ALARM_SEVERITY_WARNING;
		} else if (randomValue <= 55) {
			return ALARM_SEVERITY_MINOR;
		} else if (randomValue <= 85) {
			return ALARM_SEVERITY_MAJOR;
		} else {
			return ALARM_SEVERITY_CRITICAL;
		}
	}
	
	private static String[] pbCauses = { "fire detected", "water flooding", "ressource exhaustion", "processor heat", "lossOfFrame",
		"lossOfPointer", "nEIdentifierDuplication", "transmitterFailure", "rectifierHighVoltage"};

	public static String getRandomPbCause() {
		int randomValue = generateRandomInteger(0, pbCauses.length-1);
		return pbCauses[randomValue];
	}
	
	
	//private static int ALARM_TYPE_OTHER = 1;
	//private static int ALARM_TYPE_COMMUNICATIONS_ALARM = 2;
	//private static int ALARM_TYPE_QUALITY_OF_SERVICE_ALARM = 3;
	//private static int ALARM_TYPE_PROCESSING_ERROR_ALARM = 4;
	//private static int ALARM_TYPE_ENVIRONMENTAL_ALARM = 5;
	
	
//    other (1),
//    communicationsAlarm (2),
//    qualityOfServiceAlarm (3),
//    processingErrorAlarm (4),
//    equipmentAlarm (5),
//    environmentalAlarm (6),
//    integrityViolation (7),
//    operationalViolation (8),
//    physicalViolation (9),
//    securityServiceOrMechanismViolation (10),
//    timeDomainViolation (11)
	
	private static int getRandomAlarmType() {
		return generateRandomInteger(1, 5);
	}
	
	public static Boolean getRandomIsAcknowledged() {
		int index = generateRandomInteger(0, 1);
		
		if(index == 0) {
			return false;
		} else {
			return true;
		}
	}

	
	
	private static Random _rand = new Random();


	/**
	 * Generate a random number between [min..max]
	 * 
	 * @param min min value for the random number
	 * @param max max value for the random number
	 * @return random value in [min...max]
	 */
	public static int generateRandomInteger(int min, int max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = (_rand.nextInt(max - min + 1) + min);
		
		return randomNum;
		
	}
	
	/**
	 * Extract randomly a string from an Array of string
	 * 
	 * @param tableOfValue 
	 * @return A random value from the input Array string
	 */
	public static String getRandomString(String[] tableOfValue) {
		int index = generateRandomInteger(0, tableOfValue.length -1);
		
		return tableOfValue[index];
	}

	/**
	 * Generates a random boolean
	 * 
	 * @return a random boolean
	 */
	public static String getRandomBooleanString() {
		int index = generateRandomInteger(0, 1);
		
		if(index == 0) {
			return "false";
		} else {
			return "true";
		}
	}

	/**
	 * Generates a random boolean in string form "true" or "false"
	 * 
	 * @param truePercentage
	 * @return a random string "true" or "false"
	 */
	public static String getRandomBooleanString(int truePercentage) {
		int index = generateRandomInteger(0, 100);
		
		if(index <= truePercentage) {
			return "true";
		} else {
			return "false";
		}
	}
	
}
