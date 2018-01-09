package com.seb.networkGenerator.CellGenerator;

import java.util.List;

import com.seb.networkGenerator.generic.Cell;
import com.seb.utilities.Technology;

public interface CellGeneratorItf {
	public int getLatestCellId();
	public int getLatestTelecomId();
	public List<Cell> getCells(Technology techno);
	public boolean parse();
	public  void insertCellsInTopologyDatabase(String outputDatabaseName, boolean appendExistingDatabase);
	public void setMaxCellsPerSite(int maxCellsPerSite);
}
