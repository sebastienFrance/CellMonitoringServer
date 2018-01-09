package com.seb.imonserver.generic;

import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatasourceParser {
	private static final Logger LOG = LogManager.getLogger(DatasourceParser.class);

	XMLInputFactory _factory;
	
	private static String DATASOURCE_ELEMENT = "Datasource";
	//private static String NPO_ELEMENT = "NPO";
	//private static String SAM_ELEMENT = "SAM";
	//private static String WMS_ELEMENT = "WMS";
	//private static String OMC_ELEMENT = "OMC";

	public DatasourceParser() {
		_factory = XMLInputFactory.newInstance();
	}
	
	public void parse(String datasourceFileName) {
		LOG.info("Start datasource parsing");
		try {
			XMLStreamReader eventReader =
					_factory.createXMLStreamReader(
							new FileReader(datasourceFileName));
			
			while (true) {
			    int event = eventReader.next();
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			    	eventReader.close();
			       break;
			    }
			    if (event == XMLStreamConstants.START_ELEMENT) {
			        String elementName = eventReader.getLocalName();
			        if (elementName.endsWith(DATASOURCE_ELEMENT)) {
			        	
			        	parseDatasource(eventReader);
			        }
			    }
			}

		}
		catch (Exception ex) {
			LOG.error(ex);
		}
		LOG.info("End of LTE parsing");
	}
	
	private void parseDatasource(XMLStreamReader eventReader) {
		boolean endOfDatasource = false;
		//String cellId = eventReader.getAttributeValue(0);
		LOG.debug("parseDatasource::Start parseDatasource");
		try {
			while (endOfDatasource == false) {
				int cellEvent = eventReader.next();
				if (cellEvent == XMLStreamConstants.END_ELEMENT) {
					String elementName = eventReader.getLocalName();
					LOG.debug("parseDatasource::ElementName: " + elementName);
					if (elementName.endsWith(DATASOURCE_ELEMENT)) {
						endOfDatasource = true;
					}
				}						    
			}
		}
		catch (Exception e) {
			LOG.error("Exception during parseDatasource",e);
		}
		LOG.debug("parseDatasource::End parseDatasource");
	}
	
}
