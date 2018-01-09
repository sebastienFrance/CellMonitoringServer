package com.seb.networkGenerator.NeighborGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.generic.Cell;
import com.seb.topologyMgt.GeoIndex;
import com.seb.topologyMgt.GeoLocation;

public class GeoContainer {
	private static final Logger LOG = LogManager.getLogger(GeoContainer.class);

	private Map<Long, List<Cell>> _indexedCells = new HashMap<Long, List<Cell>>();
	
	private GeoIndex _geoDataIndex;
	
	public GeoContainer(GeoIndex geoDataIndex) {
		_geoDataIndex = geoDataIndex;
	}
	
	public void addCells(List<Cell> cells) {
		for (Cell currentCell : cells) {
			addCell(currentCell);
		}
	}
	
	public void addCell(Cell theCell) {
		Long index = _geoDataIndex.getIndex(theCell.getLatitude(), theCell.getLongitude());
		
		List<Cell> cells = _indexedCells.get(index);
		if (cells != null) {
			cells.add(theCell);
		} else {
			cells = new ArrayList<Cell>();
			cells.add(theCell);
			_indexedCells.put(index, cells);
		}
	}

	public List<Cell> getCellsForIndex(Long index) {
		return _indexedCells.get(index);
	}
	
	public Set<Long> getAllIndexesWithCells() {
		return _indexedCells.keySet();
	}
	
	public List<Cell> getCellsForBoundingBox(GeoLocation[] boundingBox) {
		if (boundingBox == null || boundingBox.length != 2) {
			return new ArrayList<Cell>();
		} else {
			return getCellsForBoundingBox(boundingBox[0].getLatitudeInDegrees(), boundingBox[0].getLongitudeInDegrees(), 
					boundingBox[1].getLatitudeInDegrees(), boundingBox[1].getLongitudeInDegrees());
		}
	}
	
	public List<Cell> getCellsForBoundingBox(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
		List<Cell> listOfCells = new ArrayList<Cell>();
		
		List<Long> indexes = _geoDataIndex.indexesForBoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
		
		if (indexes != null) {
			for (Long currentIndex : indexes) {
				List<Cell> partOfCells = getCellsForIndex(currentIndex);
				if (partOfCells != null) {
					listOfCells.addAll(partOfCells);
				}
			}
		} else {
			LOG.debug("getCellsForBoundingBox:: found no indexes...");
		}
		
		return listOfCells;
	}
}
