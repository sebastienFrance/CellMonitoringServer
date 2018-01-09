package com.seb.imonserver.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.generic.PropertyUtility;



public  class WebServerProperties {
	
	private static final Logger LOG = LogManager.getLogger(WebServerProperties.class);

	private  final String PROP_PROTOCOL = "Protocol";
	private  final String PROP_TLS_VERSION = "TLSVersion";
	private  final String PROP_HTTP_VERSION = "HTTPVersion";
	private  final String PROP_PORT = "Port";
	
	private  final String PROP_SERVLET_CONTEXT = "ServletContext";
	private  final String PROP_SERVLET_PATH = "ServletPath";
	private  final String PROP_SERVLET_TMP_DIR = "ServletTmpDir";
	private  final String PROP_SERVLET_NAME = "ServletName";
	
	private  final String PROP_WEB_SERVER = "Webserver";
	
	private  final String PROP_KEYSTORE = "Keystore";
	private  final String PROP_KEYSTORE_PASSWORD = "KeystorePassword";

	// read from the property file
	private String _protocol;
	private String _tlsVersion;
	private int _port;
	private String _httpVersion;
	
	private String _webserver;
	
	private String _servletContext;
	private String _servletPath;
	private String _servletTmpDir;
	private String _servletName;
	
	private String _keystore;
	private String _keystorePassword;
	
	public boolean isHTTPS() {
		return "https".equals(_protocol) ? true : false; 
	}
	
	public String getTLSVersion() {
		return _tlsVersion;
	}
	
	public int getPort() {
		return _port;
	}

	public boolean isHTTP2() {
		return "2".equals(_httpVersion) ? true : false; 
	}

	public boolean isWebServerTomcat() {
		return "tomcat".equals(_webserver) ? true : false;
	}
	
	public boolean isWebServerJetty() {
		return "jetty".equals(_webserver) ? true : false;
	}
	
	public String getServletContext() {
		return _servletContext;
	}
	
	public String getServletPath() {
		return _servletPath;
	}
	
	public String getServletTmpDir() {
		return _servletTmpDir;
	}

	public String getServletName() {
		return _servletName;
	}

	public String getKeystore() {
		return _keystore;
	}
	
	public String getKeystorePassword() {
		return _keystorePassword;
	}
	
	private WebServerProperties() {	
	}

	private static WebServerProperties INSTANCE = new WebServerProperties();

	public static WebServerProperties getInstance() {
		return INSTANCE;
	}

	public void initialize(String propertyFileName) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertyFileName));
		} catch (IOException e) {
			LOG.fatal("Cannot open the property file: " + propertyFileName, e);
			System.exit(1);
		}

		_protocol = PropertyUtility.getStringProperties(properties, PROP_PROTOCOL);
		_tlsVersion = PropertyUtility.getStringProperties(properties, PROP_TLS_VERSION);
		_port = PropertyUtility.getIntProperties(properties, PROP_PORT);
		_httpVersion = PropertyUtility.getStringProperties(properties, PROP_HTTP_VERSION);
		
		_webserver = PropertyUtility.getStringProperties(properties, PROP_WEB_SERVER);
		
		_servletContext = PropertyUtility.getStringProperties(properties, PROP_SERVLET_CONTEXT);
		_servletPath = PropertyUtility.getStringProperties(properties, PROP_SERVLET_PATH);
		_servletTmpDir = PropertyUtility.getStringProperties(properties, PROP_SERVLET_TMP_DIR);
		_servletName = PropertyUtility.getStringProperties(properties, PROP_SERVLET_NAME);
		
		_keystore = PropertyUtility.getStringProperties(properties, PROP_KEYSTORE);
		_keystorePassword = PropertyUtility.getStringProperties(properties, PROP_KEYSTORE_PASSWORD);
	}
}
