package com.seb.networkTopology.generic;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class XMLAttributesParser {
	private static final Logger LOG = LogManager.getLogger(XMLAttributesParser.class);

	XMLInputFactory _factory;
	
	private static String ELEMENT_ATTRIBUTE = "Attribute";
	
	private static String ELEMENT_ATTR_ATTRBUTE_NAME = "name";
	private static String ELEMENT_ATTR_ATTRBUTE_SECTION = "section";

	public XMLAttributesParser() {
		_factory = XMLInputFactory.newInstance();
	}
	
	/**
	 * Parse an XML file that contains a list of Attribute Name / Section and build a table
	 * that contains the name of each attribute with its section name
	 * 
	 * @param attributeFileName
	 * @return Map with Key is Attribute name and the Value is the Section name
	 */
	public Map<String, String> parse(String attributeFileName) {
		Map<String, String> attributes = new HashMap<String, String>();
		
		try {
			XMLStreamReader eventReader =
					_factory.createXMLStreamReader(
							new FileReader(attributeFileName));
			
			while (true) {
			    int event = eventReader.next();
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			    	eventReader.close();
			       break;
			    }
			    if (event == XMLStreamConstants.START_ELEMENT) {
			        String elementName = eventReader.getLocalName();
			        if (elementName.endsWith(ELEMENT_ATTRIBUTE)) {
			        	
			        	parseAttribute(eventReader, attributes);
			        }
			    }
			}
			return attributes;

		}
		catch (Exception e) {
			LOG.error(e);
			return null;
		} 
	}
	
	/**
	 * Parse an attribute and add it to the HashMap
	 * 
	 * @param eventReader cursor on the XML element ATTRIBUTE
	 * @param attributes hashMap to add the new attribute
	 * 
	 */
	private void parseAttribute(XMLStreamReader eventReader, Map<String, String> attributes) {
		String attrName = null;
		String sectionName = null;
		
		for (int i = 0 ; i < eventReader.getAttributeCount(); i++) {
			if (eventReader.getAttributeLocalName(i).equals(ELEMENT_ATTR_ATTRBUTE_NAME)) {
				attrName = eventReader.getAttributeValue(i);
			} else if (eventReader.getAttributeLocalName(i).equals(ELEMENT_ATTR_ATTRBUTE_SECTION)) {
				sectionName = eventReader.getAttributeValue(i);
			}
			
			if ((attrName != null) && (sectionName != null)) {
				attributes.put(attrName, sectionName);
				return;
			}
		}
	}
	
}
