package com.seb.imonserver.main;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import com.seb.imonserver.IMonServer; 

public class TomcatEmbeddedRunner {
	
	public void startServer() throws LifecycleException {
		WebServerProperties webProperties = WebServerProperties.getInstance();
		
		Tomcat tomcat = new Tomcat();
		
		Connector defaultConnector = tomcat.getConnector();
		defaultConnector.setPort(webProperties.getPort());
	    
	    if (webProperties.isHTTPS()) {
		    defaultConnector.setSecure(true);
		    defaultConnector.setAttribute("sslProtocol",  webProperties.getTLSVersion());
		    defaultConnector.setAttribute("SSLEnabled",  true);
		    defaultConnector.setAttribute("keystorePass", webProperties.getKeystorePassword());
		    defaultConnector.setScheme("https");
	    } else {
		    defaultConnector.setSecure(false);
		    defaultConnector.setScheme("http");
	    }

	    defaultConnector.setAttribute("clientAuth",  "false");

	    /* TBD: use annotation @WebServlet instead of adding the servlet */
	    
		File base = new File(System.getProperty(webProperties.getServletTmpDir()));
		Context rootCtx = tomcat.addContext(webProperties.getServletContext(), base.getAbsolutePath());
		Tomcat.addServlet(rootCtx, webProperties.getServletName(), new IMonServer());
		rootCtx.addServletMapping(webProperties.getServletPath(), webProperties.getServletName());
		tomcat.start();
		tomcat.getServer().await();
	}
}