package com.seb.networkGenerator.NeighborGenerator;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkGenerator.generic.TopologyUtils;
import com.seb.topologyMgt.GeoLocation;

public class NeighborGeneratorPerGeographical extends NeighborGeneratorBasics{
	private static final Logger LOG = LogManager.getLogger(NeighborGeneratorPerGeographical.class);

	protected GeoContainer _cellGeoContainer;
	
	public NeighborGeneratorPerGeographical(String outputDatabaseName, GeoContainer cellGeoContainer) {
		super(outputDatabaseName);
		_cellGeoContainer = cellGeoContainer;
	}
	
	/**
	 * Add neighbors relations to all cells based on max distance (configured in property file)
	 */
	@Override
	public void addNeighborRelations() {
		LOG.info("addNeighborRelations::Add neighbor relations for LTE");
		for (Cell currentCell : _allLTECells) {
			newAddNRForCellInRange(currentCell, NetworkGeneratorProperties.getMaxDistanceLTENeighborsInMeters());
		}
		LOG.info("addNeighborRelations::Add neighbor relations for WCDMA");
		for (Cell currentCell : _allWCDMACells) {
			newAddNRForCellInRange(currentCell, NetworkGeneratorProperties.getMaxDistanceWCDMANeighborsInMeters());
		}
		LOG.info("addNeighborRelations::Add neighbor relations for GSM");
		for (Cell currentCell : _allGSMCells) {
			newAddNRForCellInRange(currentCell, NetworkGeneratorProperties.getMaxDistanceGSMNeighborsInMeters());
		}
		LOG.info("addNeighborRelations::End neighbor relations for GSM");
	}
	
	
	protected void newAddNRForCellInRange(Cell centerCell, int maxNRInMeterDistance) {
		LOG.info("Look for NR for: " + centerCell.getCellName() + " / " + centerCell.getTelecomId());
		GeoLocation centerLocation = GeoLocation.fromDegrees(centerCell.getLatitude(), centerCell.getLongitude());
		GeoLocation[] boundingBox = centerLocation.boundingCoordinates((maxNRInMeterDistance / 1000.0), 6371.01);

		List<Cell> cells = _cellGeoContainer.getCellsForBoundingBox(boundingBox);

		int numberIntraRATNR = 0;
		int numberInterRATNR = 0;
		boolean maxIntraRATNR = false;
		boolean maxInterRATNR = false;

		for (Cell currentCell : cells) {
			GeoLocation targetCellLocation = currentCell.getGeoLocation();
			if (TopologyUtils.isTargetCellInRange(centerCell, targetCellLocation, maxNRInMeterDistance)) {

				if (centerCell.getTechno().equals(currentCell.getTechno())) {
					numberIntraRATNR++;
					if (numberIntraRATNR <= NetworkGeneratorProperties.getMaxNumberIntraRATNR()) {
						TopologyUtils.addNeighborRelation(centerCell, currentCell.getTelecomId(), currentCell.getTechno(), currentCell.getDLFrequency());							
					} else {
						maxIntraRATNR = true;								
					}
				} else {
					numberInterRATNR++;
					if (numberInterRATNR <= NetworkGeneratorProperties.getMaxNumberInterRATNR()) {
						TopologyUtils.addNeighborRelation(centerCell, currentCell.getTelecomId(), currentCell.getTechno(), currentCell.getDLFrequency());							
					} else {
						maxInterRATNR = true;
					}
				}

				if ((maxInterRATNR == true) && (maxIntraRATNR == true)) {
					return;
				}					
			}
		}
	}
	
}
