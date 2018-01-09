package com.seb.networkGenerator.generic;

import java.util.List;

import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkTopology.generic.Adjacency;
import com.seb.networkTopology.generic.Utility;
import com.seb.topologyMgt.GeoLocation;

public class TopologyUtils {

	/**
	 * Check if distance from centerCell to targetCell is <= maxNRDistance 
	 * 
	 * @param centerCell source cell
	 * @param targetCell target cell 
	 * @param maxNRDistance max distance allowed between the two cells
	 * @return True if the distance as <= maxNRDistance otherwise it returns False
	 */
	public static boolean isTargetCellInRange(Cell centerCell, Cell targetCell, int maxNRDistance) {
		GeoLocation cellLocation = targetCell.getGeoLocation();
		return isTargetCellInRange(centerCell, cellLocation, maxNRDistance);
	}

	public static boolean isTargetCellInRange(Cell centerCell, GeoLocation targetCellLocation, int maxNRDistanceInMeter) {
		double distanceInKm = targetCellLocation.distanceTo(centerCell.getGeoLocation(), 6371.01);

		return distanceInKm < (maxNRDistanceInMeter / 1000.0) ? true : false;
	}
	
	public static int getRandomRadius() {
		return Utility.generateRandomInteger(5000, 10000);
	}
	
	
	/**
	 * Add generic attributes to a cell 
	 * 
	 * @param newCell target cell 
	 * @param cellName name of the cell to build value of the attribute
	 */
	public static void addCellAttributesFor(Cell newCell, String cellName) {
		String[] sections = NetworkGeneratorProperties.getAttributesSectionsName();
		for (String sectionName : sections) {
			for (int j = 0; j < NetworkGeneratorProperties.getMaxAttributesPerSection(); j++) {
				String paramName = sectionName + "_" + Integer.toString(j);
				String paramValue = cellName + "_" + Integer.toString(j);
				newCell.addParamValue(paramName, paramValue, sectionName);
			}
		}
	}
	
	public static int countNeighbors(List<Cell> listOfCells) {
		int counter = 0;

		for (Cell currentCell : listOfCells) {
			counter += currentCell.getNumberInterFreqNR();
			counter += currentCell.getNumberIntraFreqNR();
			counter += currentCell.getNumberInterRATNR();
		}

		return counter;
	}
	public static void addNeighborRelation(Cell sourceCell, String targetTelecomId, String targetTechno, String targetDLFrequency) {

		String isMeasuredByANR = "false";
		String isNoHO = "false";
		String isNoRemove = "false";
		if (sourceCell.getTechno().equals("LTE")) {
			isMeasuredByANR = Utility.getRandomBooleanString(NetworkGeneratorProperties.getLTEPercentageMeasuredByANR());
			isNoHO = Utility.getRandomBooleanString();
			isNoRemove = Utility.getRandomBooleanString();
		} 

		Adjacency neighborRelation = new Adjacency(targetTelecomId, isNoHO, isNoRemove, isMeasuredByANR, targetTechno, targetDLFrequency);
		sourceCell.addNeighbor(neighborRelation);
	}

}
