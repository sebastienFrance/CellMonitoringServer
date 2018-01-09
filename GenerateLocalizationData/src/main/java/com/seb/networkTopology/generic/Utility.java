package com.seb.networkTopology.generic;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Utility {
	private static final Logger LOG = LogManager.getLogger(Utility.class);

	private static Random _rand = new Random();

	
	public static String getDirectoryForCurrentDay(String outputBaseDirectory) {
		
		File folder = new File(outputBaseDirectory);
		if (folder.isDirectory() == false) {
			LOG.fatal("Utility::Error: Cannot create database if directory doesn't exist: " + outputBaseDirectory);
			return null;
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		dateFormat.format(date);
		String databaseOutputDirectory = new String (outputBaseDirectory + "/" + dateFormat.format(date));
		
		File databaseFolder = new File(databaseOutputDirectory);
		if (databaseFolder.isDirectory() == true) {
			// Directory already exist ! content must be cleanup
		} else {
			if (databaseFolder.mkdir() == false) {
				LOG.error("Utility::Error Cannot create directory: " + databaseOutputDirectory);
				return null;				
			} 
		} 
		
	    return databaseOutputDirectory;
	}

	public static void resetLatestDirectory(String outputDirectory) {
		LOG.info("Utility::Delete old latest link " + outputDirectory + "/latest");
		try {
			File latestToBeDeleted = new File(outputDirectory + "/latest");
			latestToBeDeleted.delete();
		} catch (java.lang.SecurityException e) {
			LOG.error(e);
		}

		
		LOG.info("Utility::Create new latest link in " + outputDirectory + "/latest");
		Runtime currentRuntime = Runtime.getRuntime();
		String outputXMLDir = Utility.getDirectoryForCurrentDay(outputDirectory);
		try {
			currentRuntime.exec("/bin/ln -s " + outputXMLDir + " " + outputDirectory + "/latest");
		} catch (IOException e) {
			LOG.error(e);
		}	
	}
	

	
	
	/**
	 * Extract from a simple file a list of value. The input file must 
	 * contains one value per row
	 * 
	 * @param fileName Full path name of the file
	 * @return List of values contained in the file
	 */
	public static List<String> parseListOfValues(String fileName) {

		try {
			List<String> listOfIdentifier = new ArrayList<String>();
			FileReader kpiFile = new FileReader(fileName);
			BufferedReader reader = new BufferedReader (kpiFile);
			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				String value = currentLine.trim();
				listOfIdentifier.add(value);
			}
			reader.close();
			
			return listOfIdentifier;
		} catch (Exception ex){
			LOG.error("parseListOfValues: Exception when readingh file " + fileName, ex);
			return null;
		}
		
	}

	
	
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
