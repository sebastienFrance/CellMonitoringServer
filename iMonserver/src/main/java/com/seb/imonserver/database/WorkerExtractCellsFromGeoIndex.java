package com.seb.imonserver.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.datamodel.Cell;
import com.seb.topologyMgt.GeoIndexAllocator;
import com.seb.topologyMgt.GeoLocation;
import com.seb.topologyMgt.TopologyMgtSQLRequestBuilder;
import com.seb.userManagement.UserDescription;

public class WorkerExtractCellsFromGeoIndex implements Runnable{
		private static final Logger LOG = LogManager.getLogger(WorkerExtractCellsFromGeoIndex.class);

		private Long _geoIndex;
		private GeoIndexAllocator.GeoIndexSize _indexSize;
		private String _networkDatabaseName;
		private GeoLocation _centerLocation;
		private double _distance;
		private UserDescription _user;
		
		private List<Cell> _cellsForGeoIndex = new ArrayList<Cell>(); 

		public WorkerExtractCellsFromGeoIndex(Long geoIndex, GeoIndexAllocator.GeoIndexSize indexSize, String networkDatabaseName, GeoLocation centerLocation, double distance, UserDescription user) {
			_geoIndex = geoIndex;
			_indexSize = indexSize;
			_networkDatabaseName = networkDatabaseName;
			_centerLocation = centerLocation;
			_distance = distance;
			_user = user;
		}
		
		@Override
		public void run() {
			String sql = TopologyMgtSQLRequestBuilder.getCellsInGeoIndex(_geoIndex, _indexSize);
			
			boolean isFullyContained = GeoIndexAllocator.getInstance().isFullyContainedIn(_geoIndex, _indexSize, _centerLocation, _distance);
			if (isFullyContained) {
				LOG.info("run:: is FullyContained, no need to check content for GeoIndex: " + _geoIndex);
			} else {
				LOG.info("run:: is NOT FullyContained, content must be checked for GeoIndex: " + _geoIndex);
				/*
				LOG.info("run:: GeoIndex: " + _geoIndex);
				LOG.info("run:: IndexSize: " + _indexSize);
				LOG.info("run:: center Latitude: " + _centerLocation.getLatitudeInDegrees() + " / Long: " + _centerLocation.getLongitudeInDegrees());
				LOG.info("run:: distance: " + _distance);
				*/
			}
			
			try (Connection theConnection = DatabaseUtility.openDatabaseConnection(_networkDatabaseName);
				Statement stmt = theConnection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)){

				while (rs.next()) {
					Cell newCell = DatabaseUtility.createCellFromResultSet(rs);
					if (!isFullyContained) {
						if (DatabaseQuery.isCellMatchingDistance(newCell, _centerLocation, _distance, _user)) {
							_cellsForGeoIndex.add(newCell);
						}
					} else {
						_cellsForGeoIndex.add(newCell);
					}
				}
			} 
			catch (Exception ex) {
				LOG.error(ex);
			}
		}
		
		public List<Cell> getCellsForGeoIndexResult() {
			return _cellsForGeoIndex;
		}
	}
