package com.seb.networkTopology.generic;

import java.io.FileReader;
import java.sql.Connection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.datasources.DatasourceTopology;
import com.seb.utilities.Technology;

public abstract class AbstractXMLTopologyParser implements ParseTechnoItf {
	private static final Logger LOG = LogManager.getLogger(AbstractXMLTopologyParser.class);

	private String _snapshotName;
	private XMLInputFactory _factory;
	
	protected Technology _techno;

	protected String _prefix;
	protected double _deltaLat;
	protected double _deltaLong;
	
	protected int _cellCount = 0;
	protected int _neighborCount = 0;
	
	
	protected DatasourceTopology _DSTopology;
	
	public int getCellCount() {
		return _cellCount;
	}
	
	public int getNeighborCount() {
		return _neighborCount;
	}
	
	protected void initializationBeforeParsing() {}
	
	/**
	 * Must be implemented per technology. Used to build the zone header content
	 * 
	 * @return the name of the technology
	 */
	protected abstract String getTechnologyName();

	/**
	 * Must be implemented per technology. Used to build the zone header content
	 * 
	 * @return the release of the technology
	 */
	protected abstract String getReleaseName();
	
	/**
	 * Must be implemented per technology to extract the right date from the input file.
	 * 
	 * Called every time the XML parser detects a START_ELEMENT
	 * 
	 */	
	protected abstract void manageStartElement(XMLStreamReader eventReader);

	/**
	 * Must be implemented per technology to extract the right date from the input file
	 * 
	 * Called every time the XML parser detects an END_ELEMENT
	 * 
	 */	
	protected abstract void manageEndElement(XMLStreamReader eventReader, Connection theConnection);
	
	protected void manageEndOfParsing(XMLStreamReader eventReader, Connection theConnection) {}
	
	
	public AbstractXMLTopologyParser(Technology techno, String snapshotName, XMLInputFactory factory, DatasourceTopology DSTopology) {
		_snapshotName = snapshotName;
		_factory = factory;
		_DSTopology = DSTopology;
		_techno = techno;
	}
	
	@Override
	public Technology getTechnology() {
		return _techno;
	}
	
	/**
	 * Relocate a cell using delta for latitude and longitude
	 * 
	 * @param latitude delta for latitude
	 * @param longitude delta for longitude
	 * 
	 */	
	@Override
	public void setDeltaCoord(double latitude, double longitude) {
		_deltaLat = latitude;
		_deltaLong = longitude;
	}
	
	/**
	 * This prefix + "_" will be added to the name of each cell 
	 * 
	 * @param prefix prefix to be added before the name of the cell
	 * 
	 */		
	@Override
	public void setPrefix(String prefix) {
		_prefix = prefix;
	}

	/**
	 * Generate the zone header section and the topology content
	 * 
	 * @param out Buffer to write the data
	 * 
	 */
	@Override
	public void parse(Connection theConnection) {
		LOG.info("Start parsing: " + _snapshotName);
		try {
			XMLStreamReader eventReader =
					_factory.createXMLStreamReader(
							new FileReader(_snapshotName));

			initializationBeforeParsing();
			
			int event = eventReader.next();
			while (true) {
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			    	manageEndOfParsing(eventReader, theConnection);
			    	eventReader.close();
			       break;
			    } else if (event == XMLStreamConstants.START_ELEMENT) {
			    	 manageStartElement(eventReader);
			    	 event = eventReader.getEventType();
			    } else if (event == XMLStreamConstants.END_ELEMENT) {
			    	manageEndElement(eventReader, theConnection);
				    event = eventReader.next();
			    } else {
				    event = eventReader.next();
			    }
			}

		}
		catch (Exception e) {
			LOG.error(e);
		}
		LOG.info("End parsing: " + _snapshotName);
	}
	
	
	/**
	 * 
	 * Returns the content of an Element text (if it's a CHARACTER) else it return null
	 * 
	 * @param eventReader
	 * @return the text content or null if not a Text Element
	 */
	protected String getElementText(XMLStreamReader eventReader) {
		try {
		   int eventType = eventReader.next();
		   if (eventType != XMLStreamConstants.CHARACTERS) {
			   return null;
		   } 
		   String value = eventReader.getText();
		   return value;

		}
		catch (Exception ex) {
			LOG.error(ex);
			return null;
		}
	}
	
	
}
