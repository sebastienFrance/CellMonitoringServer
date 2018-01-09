package com.seb.networkGenerator.OpenCellIds;

import com.seb.networkTopology.generic.Utility;
import com.seb.utilities.Technology;

public class OpenCellIdsUtility {

	public static Technology getCellTechno() {
		int randomValue = Utility.generateRandomInteger(0, 100);
		if (randomValue <= 70) {
			return Technology.WCDMA;
		} else {
			return Technology.LTE;
		}
	}
	
	private OpenCellIdsUtility() {}
}
