package com.seb.networkGenerator.generic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.topologyMgt.GeoLocation;
import com.seb.networkTopology.generic.Utility;

public class ZoneFileGenerator {
	private static final Logger LOG = LogManager.getLogger(ZoneFileGenerator.class);

	private BufferedWriter _out;
	
	
	public ZoneFileGenerator(String zoneFileName) {
		FileWriter fstream = null;
		String outputXMLDir = Utility.getDirectoryForCurrentDay(NetworkGeneratorProperties.getInstance().getOutputDirectory());
		if (outputXMLDir == null) {
			LOG.fatal("ZoneFileGenerator::Error cannot create output directory for XML file");
			return;
		}

		try {
			fstream = new FileWriter(outputXMLDir + "/" + zoneFileName);
		}
		catch (Exception e) {
			LOG.fatal("ZoneFileGenerator::Exception when creating zone file" + outputXMLDir + "/" + zoneFileName, e);
			return;
		}
		 _out = new BufferedWriter(fstream);

	}
	
	public void closeZoneFile() {
		try {
			_out.close();
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	/**
	 * Create X zones based on the list of cells
	 * 
	 * @param out Buffer to write the zone
	 * @param listOfCells List of cells to build the zone
	 */
	public  void generateZoneFor(List<Cell> listOfCells) {

		if (listOfCells == null || listOfCells.size() == 0) {
			return;
		}

		int numberOfZones = NetworkGeneratorProperties.getNumberOfZones();

		String[] zoneTypes = { "WZ", "OZ" }; 
		try {
			for (int i = 0; i < numberOfZones; i++) {
				int randomCellIndex = Utility.generateRandomInteger(0, (listOfCells.size() -1));
				Cell initialCell = listOfCells.get(randomCellIndex);
				_out.write("Zone_" + i + "_" + initialCell.getCellName());
				String wzOrOz = Utility.getRandomString(zoneTypes);
				_out.write(";" + wzOrOz + ";");
				_out.write(initialCell.getTechno() + ";No comment;" + initialCell.getCellName());
				addCellsInRangeForZone(initialCell, listOfCells);
				_out.newLine();
			}
		} 
		catch (Exception ex) {
			LOG.error(ex);
		}

	}

	
	/**
	 * Add in Zone file all cells from the same techno that are in the range
	 * 
	 * @param out Buffer to write the cell name
	 * @param initialCell source cell of the Zone
	 * @param listOfCells List of cells in which to look to build the zone
	 */
	private  void addCellsInRangeForZone(Cell initialCell, List<Cell> listOfCells) {
		int cellCounter = 0;
		int maxCellDistance = NetworkGeneratorProperties.getZoneMaxCellDistance();
		for (Cell currentCell : listOfCells) {
			if (currentCell != initialCell) {
				if (TopologyUtils.isTargetCellInRange(initialCell, currentCell, maxCellDistance)) {
					try {
						_out.write(";" + currentCell.getCellName());
						cellCounter++;
						if (cellCounter == NetworkGeneratorProperties.getZoneMaxCellsPerZone()) {
							return;
						}
					}	
					catch (Exception ex) {
						LOG.error(ex);
					}
				}
			}
		}
		
	}




}
