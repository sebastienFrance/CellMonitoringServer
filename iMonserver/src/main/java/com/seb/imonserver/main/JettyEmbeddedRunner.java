package com.seb.imonserver.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.seb.imonserver.IMonServer;

import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
//import org.eclipse.jetty.webapp.WebAppContext;


public class JettyEmbeddedRunner {
	private static final Logger LOG = LogManager.getLogger(JettyEmbeddedRunner.class);
	
	public void startServer() {
		//startOldVersion();
		startHTTP2Server();
	}
	private void startOldVersion() {
		
		WebServerProperties webProperties = WebServerProperties.getInstance();

		try {
			Server server = new Server();
			
			ServerConnector theConnector = null;
		    if (webProperties.isHTTPS()) {
		    	theConnector = initializeConnectorForHTTPS(server);
		    } else {
		    	theConnector = new ServerConnector(server);
		    }
			
	    	theConnector.setPort(webProperties.getPort());

			server.setConnectors(new Connector[] { theConnector });

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath(webProperties.getServletContext());
			context.setResourceBase(System.getProperty(webProperties.getServletTmpDir()));
			server.setHandler(context);

			// Add dump servlet
			context.addServlet(IMonServer.class, webProperties.getServletPath());

			server.start();
			server.join();
		} catch (Exception ex) {
			  LOG.error(ex);
		}
		
	}
	
	private ServerConnector initializeConnectorForHTTPS(Server server) {
		WebServerProperties webProperties = WebServerProperties.getInstance();

		HttpConfiguration httpConfig = new HttpConfiguration();
    	httpConfig.setSecureScheme("https");
    	httpConfig.setSecurePort(webProperties.getPort());
    	httpConfig.addCustomizer(new SecureRequestCustomizer());
		
		SslContextFactory sslContextFactory = new SslContextFactory();
		
		sslContextFactory.setKeyStorePassword(webProperties.getKeystorePassword()); 
		sslContextFactory.setKeyStorePath(webProperties.getKeystore());
		sslContextFactory.setProtocol(webProperties.getTLSVersion());
							
		String HTTPversion = webProperties.isHTTP2() ? HttpVersion.HTTP_2.toString() : HttpVersion.HTTP_1_1.toString();

		LOG.info("startServer::keystore: " + sslContextFactory.getKeyStorePath());
		LOG.info("startServer::keystore provider: " + sslContextFactory.getKeyStoreProvider());
		LOG.info("startServer::keystore type: " + sslContextFactory.getKeyStoreType());
		LOG.info("startServer::Protocol : " + sslContextFactory.getProtocol());
		LOG.info("startServer::HTTP version : " + HTTPversion);
	
		SslConnectionFactory sslFactory = new SslConnectionFactory(
				sslContextFactory, 
				HTTPversion);
		
		return new ServerConnector(server, sslFactory, new HttpConnectionFactory(httpConfig));				
	}

	private void startHTTP2Server() {
		WebServerProperties webProperties = WebServerProperties.getInstance();

		HttpConfiguration config = getHttpConfiguration();

		HttpConnectionFactory http1 = new HttpConnectionFactory(config);
		HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(config);

		NegotiatingServerConnectionFactory.checkProtocolNegotiationAvailable();
		ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
		alpn.setDefaultProtocol(http1.getProtocol()); // sets default protocol to HTTP 1.1

		// SSL Connection Factory
	    SslContextFactory sslContextFactory = getInitializedSslContextFactory();	
		SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server, ssl, alpn, http2, http1);
		connector.setPort(webProperties.getPort());
		server.addConnector(connector);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(webProperties.getServletContext());
		context.setResourceBase(System.getProperty(webProperties.getServletTmpDir()));
		server.setHandler(context);

		// Add dump servlet
		context.addServlet(IMonServer.class, webProperties.getServletPath());

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static SslContextFactory getInitializedSslContextFactory() {
		WebServerProperties webProperties = WebServerProperties.getInstance();

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(webProperties.getKeystore());
		sslContextFactory.setKeyStorePassword(webProperties.getKeystorePassword());
		sslContextFactory.setProtocol(webProperties.getTLSVersion()); 
	    sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
	    sslContextFactory.setUseCipherSuitesOrder(true);

	    return sslContextFactory;
	}
	
	private static HttpConfiguration getHttpConfiguration() {
		WebServerProperties webProperties = WebServerProperties.getInstance();
		HttpConfiguration config = new HttpConfiguration();
		config.setSecureScheme("https");
		config.setSecurePort(webProperties.getPort());
		config.setSendXPoweredBy(true);
		config.setSendServerVersion(true);
		config.addCustomizer(new SecureRequestCustomizer());
		return config;
	}



}