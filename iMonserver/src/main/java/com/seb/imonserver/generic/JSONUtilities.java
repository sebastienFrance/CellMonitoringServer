package com.seb.imonserver.generic;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import net.sf.json.JSONObject;

public class JSONUtilities {
	private static final Logger LOG = LogManager.getLogger(JSONUtilities.class);
	
	private JSONUtilities() {}

	public static void sendJSONObject(Object newObject, OutputStream out) {
		try {
			out.write(newObject.toString().getBytes());
			out.flush();
			out.close();
		} catch (IOException ex) {
			LOG.error(ex);
		}		
	}
	
	public static void sendImageWithJSON(byte[] theImage, OutputStream out) {
		  try {
			  JSONObject json = new JSONObject();
			  json.put("image", theImage);
			  JSONUtilities.sendJSONObject(json, out);
		  } catch (Exception ex) {
				LOG.error(ex);
		  }
	}
}
