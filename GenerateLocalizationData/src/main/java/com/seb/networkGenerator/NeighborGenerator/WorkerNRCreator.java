package com.seb.networkGenerator.NeighborGenerator;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.NetworkGeneratorProperties;
import com.seb.networkGenerator.generic.Cell;
import com.seb.networkGenerator.generic.TopologyUtils;
import com.seb.topologyMgt.GeoLocation;


public class WorkerNRCreator implements Runnable {
	private static final Logger LOG = LogManager.getLogger(WorkerNRCreator.class);

	private List<Cell> _cells;
	private GeoContainer _cellGeoContainer;

	public WorkerNRCreator(List<Cell> cells, GeoContainer cellGeoContainer) {
		_cells = cells;
		_cellGeoContainer = cellGeoContainer;
	}
	
	@Override
	public void run() {
		for (Cell currentCell : _cells) {
			newAddNRForCellInRange(currentCell, NetworkGeneratorProperties.getMaxDistanceLTENeighborsInMeters());
		}
	}

	protected void newAddNRForCellInRange(Cell centerCell, int maxNRInMeterDistance) {
		LOG.debug("Look for NR for: " + centerCell.getCellName() + " / " + centerCell.getTelecomId());
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
