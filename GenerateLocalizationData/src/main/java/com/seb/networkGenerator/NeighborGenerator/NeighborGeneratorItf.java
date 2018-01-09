package com.seb.networkGenerator.NeighborGenerator;

import java.util.List;

import com.seb.networkGenerator.generic.Cell;
import com.seb.utilities.Technology;

public interface NeighborGeneratorItf {
	public void appendCellsFor(Technology techno, List<Cell> cells);
	public List<Cell> getCells(Technology techno);
	public void insertNRsInTopologyDatabase();
	public void addNeighborRelations();
}
