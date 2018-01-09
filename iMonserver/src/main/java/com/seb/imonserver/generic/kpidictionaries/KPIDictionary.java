package com.seb.imonserver.generic.kpidictionaries;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * It stores the list of KPIs dictionaries that has been loaded.
 * 
 * @author Sebastien Brugalieres
 *
 */
public class KPIDictionary {
	private static final Logger LOG = LogManager.getLogger(KPIDictionary.class);

	private String _name;
	private String _description;
	
	private List<KPIs> _KPIs;
	
	public KPIDictionary() {
		_KPIs = new ArrayList<KPIs>();
	}

	public String getName() {
		return _name;
	}


	public void setName(String _name) {
		this._name = _name;
	}


	public String getDescription() {
		return _description;
	}


	public void setDescription(String _description) {
		this._description = _description;
	}
	
	public void addKPIs(KPIs theKPIs) {
		_KPIs.add(theKPIs);
	}
	
	public List<KPIs> getKPIs() {
		return _KPIs;
	}
	
	public void dump() {
		LOG.info("KPI Dictionary name: " + _name);
		LOG.info("KPI Dictionary description: " + _description);
		
		for (KPIs theKPIs : _KPIs) {
			theKPIs.dump();
		}
	}
	
	private final static String ELEMENT_KPI_LIST = "KPIList";
	private final static String ELEMENT_KPI_DICTIONARY = "KPIDictionary";
	private final static String ELEMENT_KPIS = "KPIs";
	private final static String ELEMENT_KPI = "KPI";
	
	private final static String ELEMENT_KPI_DICTIONARY_ATTR_NAME = "name";
	private final static String ELEMENT_KPI_DICTIONARY_ATTR_DESCRIPTION = "description";

	private final static String ELEMENT_KPIS_TECHNO = "techno";

	private final static String ELEMENT_KPI_ATTR_NAME = "name";
	private final static String ELEMENT_KPI_ATTR_INTERNAL_NAME = "internalName";
	private final static String ELEMENT_KPI_ATTR_SHORT_DESCRIPTION = "shortDescription";
	private final static String ELEMENT_KPI_ATTR_DOMAIN = "domain";
	private final static String ELEMENT_KPI_ATTR_FORMULA = "formula";
	private final static String ELEMENT_KPI_ATTR_UNIT = "unit";
	private final static String ELEMENT_KPI_ATTR_DIRECTION = "direction";
	private final static String ELEMENT_KPI_ATTR_LOW = "low";
	private final static String ELEMENT_KPI_ATTR_MEDIUM = "medium";
	private final static String ELEMENT_KPI_ATTR_HIGH = "high";
	private final static String ELEMENT_KPI_ATTR_RELATED_KPI = "relatedKPI";
	
	
//	<KPIList>
//	<KPIDictionary name="KPI Dashboard" description="General KPI giving a global view of the network">
//	<KPIs techno = "LTE">
//	<KPI name="Connection Success Rate" internalName="LTE_CNX_SUCC_RATE" shortDescription="Connection success rate" domain="Accessibility" formula="To be completed" unit="%" direction="decrease" low="99.5" medium="98.5" high="97" relatedKPI="LTE_CNX_NB"/>

	private static JSONArray _KPIDictionaries ;

	public static JSONArray getKPIDictionaries() {
		return _KPIDictionaries;
	}
	
	static public void initKPIDictionary(String KPIDictionaryFileName) {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		try {
			XMLStreamReader eventReader =
					factory.createXMLStreamReader(
							new FileReader(KPIDictionaryFileName));

			parseKPIDictionaries(eventReader, KPIDictionaryFileName);

		}
		catch (Exception ex) {
			LOG.error(ex);
		}	  
	}

	static public void parseKPIDictionaries(XMLStreamReader eventReader, String KPIDictionaryFileName) {
		try {
			boolean end = false;

			List<KPIDictionary> KPIDictionaries = new ArrayList<KPIDictionary>();

			KPIDictionary currentKPIDictionary = null;
			KPIs currentKPIs = null;

			JSONObject newObject = null;
			_KPIDictionaries = new JSONArray();

			while (end == false) {
				int event = eventReader.next();
				if (event == XMLStreamConstants.END_ELEMENT) {
					String endElementName = eventReader.getLocalName();
					if (endElementName.equals(ELEMENT_KPI_LIST)) {
						end = true;
					} else if (endElementName.equals(ELEMENT_KPI_DICTIONARY)) {
						newObject = JSONObject.fromObject(currentKPIDictionary);
						_KPIDictionaries.add(newObject);	
					}
				}

				if (event == XMLStreamConstants.START_ELEMENT) {
					String elementName = eventReader.getLocalName();
					if (elementName.equals(ELEMENT_KPI_LIST)) {
						//String cellName = eventReader.getAttributeValue(null,ATTR_CELL_ID);
					} else if (elementName.equals(ELEMENT_KPI_DICTIONARY)) {
						currentKPIDictionary = new KPIDictionary();
						currentKPIDictionary.setName(eventReader.getAttributeValue(null, ELEMENT_KPI_DICTIONARY_ATTR_NAME));
						currentKPIDictionary.setDescription(eventReader.getAttributeValue(null, ELEMENT_KPI_DICTIONARY_ATTR_DESCRIPTION));

						KPIDictionaries.add(currentKPIDictionary);
					} else if (elementName.equals(ELEMENT_KPIS)) {
						currentKPIs = new KPIs();
						currentKPIs.setTechno(eventReader.getAttributeValue(null, ELEMENT_KPIS_TECHNO));

						currentKPIDictionary.addKPIs(currentKPIs);
					} else if (elementName.equals(ELEMENT_KPI)) {

						KPI theKPI = new KPI();

						theKPI.setName(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_NAME));
						theKPI.setInternalName(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_INTERNAL_NAME));
						theKPI.setShortDescription(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_SHORT_DESCRIPTION));
						theKPI.setDomain(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_DOMAIN));
						theKPI.setFormula(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_FORMULA));
						theKPI.setUnit(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_UNIT));
						theKPI.setDirection(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_DIRECTION));
						theKPI.setLow(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_LOW));
						theKPI.setMedium(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_MEDIUM));
						theKPI.setHigh(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_HIGH));
						theKPI.setRelatedKPI(eventReader.getAttributeValue(null, ELEMENT_KPI_ATTR_RELATED_KPI));

						currentKPIs.addKPI(theKPI);
					}
				}
			}


		}
		catch (Exception ex) {
			LOG.error(ex);
		}  
	}
}
