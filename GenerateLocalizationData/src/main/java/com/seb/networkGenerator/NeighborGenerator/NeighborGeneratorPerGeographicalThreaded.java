package com.seb.networkGenerator.NeighborGenerator;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.networkGenerator.generic.Cell;

public class NeighborGeneratorPerGeographicalThreaded extends NeighborGeneratorPerGeographical {
	private static final Logger LOG = LogManager.getLogger(NeighborGeneratorPerGeographicalThreaded.class);

	public NeighborGeneratorPerGeographicalThreaded(String outputDatabaseName, GeoContainer cellGeoContainer) {
		super(outputDatabaseName, cellGeoContainer);
	}

	@Override
	public void addNeighborRelations() {
		ExecutorService executor = Executors.newCachedThreadPool();


		Set<Long> indexes = _cellGeoContainer.getAllIndexesWithCells();
		for (Long currentIndex : indexes) {
			LOG.debug("addNeighborRelations::Add neighbor relations for geoIndex: " + currentIndex.longValue());
			List<Cell> cells = _cellGeoContainer.getCellsForIndex(currentIndex);
			if (cells == null || cells.isEmpty()) {
				LOG.warn("addNeighborRelations:: geo index should not be empty:" + currentIndex.longValue());
			}

			Runnable worker = new WorkerNRCreator(cells, _cellGeoContainer);
			executor.execute(worker);
		}

		LOG.debug("NeighborGeneratorPerGeographicalThreaded:: Wait end of Executor");
		executor.shutdown();
		try {
			if (executor.awaitTermination(24, TimeUnit.HOURS) == false) {
				LOG.fatal("NeighborGeneratorPerGeographicalThreaded:: stopped on timeout, all threads not completed!");
			}
			LOG.debug("NeighborGeneratorPerGeographicalThreaded:: Finished all thread");
		} catch (Exception ex) {
			LOG.fatal("Exception when executing threads", ex);
		}
	}

}
