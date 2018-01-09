package com.seb.imonserver.main;


import java.io.IOException;
import java.util.Map;

import org.apache.catalina.LifecycleException;
 
//import com.hascode.tutorial.container.JettyEmbeddedRunner;
 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.iMonServerProperties;

public class Main {
	private static final Logger LOG = LogManager.getLogger(Main.class);

	public static void main(final String[] args) throws IOException, LifecycleException {

		//Logger logger = LogManager.getRootLogger();
		LOG.trace("Configuration File Defined To Be :: "+System.getProperty("log4j.configurationFile"));

		initializePropertyFiles();

		WebServerProperties webProperties = WebServerProperties.getInstance();
		LOG.info("### STARTING EMBEDDED WEB CONTAINER");
		if (webProperties.isWebServerTomcat()) {
			LOG.info("Starting Tomcat ..");
			new TomcatEmbeddedRunner().startServer();
		} else {
			LOG.info("Starting Jetty...");
			new JettyEmbeddedRunner().startServer();
		}
	}
	
	private static void initializePropertyFiles() {
		// Initialize the initial configuration file thanks to environment variable
		Map<String, String> env = System.getenv();
		String iMonPath = env.get("IMONITORING_PROP_FILE");  
		if (iMonPath == null) {
			LOG.fatal("init::Cannot find IMONITORING_PROP_FILE environment variable, must exit!");
			System.exit(1);
		}

		// Initialize the properties for the webServer configuration
		iMonServerProperties instanceProperties = iMonServerProperties.getInstance();
		instanceProperties.initialize(iMonPath);

		WebServerProperties webProperties = WebServerProperties.getInstance();
		webProperties.initialize(instanceProperties.getWebServerPropertyFile());
	}
}