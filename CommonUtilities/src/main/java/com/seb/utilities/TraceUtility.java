package com.seb.utilities;


import java.util.Date;

public class TraceUtility {
	public static String duration(Date startDate) {
		Date date = new Date();
		long delta = date.getTime() - startDate.getTime();

		return "(Duration = " + delta + " ms)";
	}
	
	private TraceUtility() {}
}
