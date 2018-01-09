package com.seb.imonserver.eql;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seb.imonserver.generic.Credentials;


/**
 * Used to send EQL Requests
 * 
 * @author Sebastien Brugalieres
 *
 */
public class SimpleEQLRequests  {
	private static final Logger LOG = LogManager.getLogger(SimpleEQLRequests.class);

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	
	public static final String EQL_CMD_INDICATOR = "indicator";
	public static final String EQL_CMD_PARAMETER = "parameter";
	public static final String EQL_CMD_REPORT = "report";
	public static final String EQL_CMD_VIEW = "view";
	public static final String EQL_CMD_DYNAMIC_QUERY = "dynamicquery";

	private final static String PROTOCOL_HTTPS = "https";
	
	private final static String BASE_URL_EXPORT = "/maat/report/?";
	
	private Credentials _userCredential;
	
	public SimpleEQLRequests(Credentials userCredential) {
		_userCredential = userCredential;
	}
	
	
	public  boolean init(String host, int port, boolean useSSL) {
		return false;
	}

	public  void shutdown() {
		
	}

	public  boolean makeQuery(String cmd, String uriOpt,
			String method,SimpleEQLHandlerItf handler) {
		sendRequest(uriOpt, handler);
		return true;
	}

    public void sendRequest(String options, SimpleEQLHandlerItf handler)  {
        HttpHost targetHost = new HttpHost(_userCredential.getIPAddress(), _userCredential.getPortNumber(), PROTOCOL_HTTPS);
        DefaultHttpClient httpclient = null;
    	try {
        	 httpclient = httpClientTrustingAllSSLCerts();
        	httpclient.getCredentialsProvider().setCredentials(
    				new AuthScope(targetHost.getHostName(), targetHost.getPort()),
    				new UsernamePasswordCredentials(_userCredential.getUserName(), _userCredential.getPassword()));
    		
    		// Create AuthCache instance
    		AuthCache authCache = new BasicAuthCache();
    		// Generate BASIC scheme object and add it to the local
    		// auth cache
    		BasicScheme basicAuth = new BasicScheme();
    		authCache.put(targetHost, basicAuth);

    		// Add AuthCache to the execution context
    		BasicHttpContext localcontext = new BasicHttpContext();
    		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

    		HttpGet httpget = new HttpGet(BASE_URL_EXPORT + options);

    		LOG.info("sendRequest::executing request: " + httpget.getRequestLine());
    		LOG.info("sendRequest::to target: " + targetHost);

    		HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
            	try {
            		HttpEntity entity = response.getEntity();
            		if (entity != null) {
            			String result = EntityUtils.toString(entity); 
            			LOG.info("sendRequest::Response content:");
            			LOG.info("sendRequest::" + result);
            		} else {
            			LOG.info("sendRequest::No data !");
            		}
            	} catch (Exception ex) {
    				LOG.error(ex);
            	}
            	
            	handler.processError();
            	return;
            }
            
    		HttpEntity entity = response.getEntity();

    		if (entity != null) {
    			LOG.info("sendRequest::Response content length: " + entity.getContentLength());
    			String result = EntityUtils.toString(entity); 
    			String[] rows = result.split("\n");
    			
    			if ((rows == null) || (rows.length < 3)) {
    				handler.processError();
    			} else if (rows[0].startsWith("VIEW,EVOLIUM") == false) {
    				handler.processError();   				
    			} else {		
    				for (int i = 0; i < rows.length; i++) {
    					LOG.info("sendRequest::rows: " + i + " : " + rows[i]);
    					if (i > 2) {
    						handler.processRow(rows[i]);
    					}
    				}
    				handler.endOfData();
    			}
    		}
    		EntityUtils.consume(entity);

    	}
    	catch (Exception ex) {
			LOG.error(ex);
    		handler.processError();
    	}
    	finally {
    		if (httpclient != null) {
    			httpclient.getConnectionManager().shutdown();
    		}
    	}
    }
   

    private DefaultHttpClient httpClientTrustingAllSSLCerts() throws NoSuchAlgorithmException, KeyManagementException {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, getTrustingManager(), new java.security.SecureRandom());

        SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
        Scheme sch = new Scheme(PROTOCOL_HTTPS, _userCredential.getPortNumber(), socketFactory);
        httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        return httpclient;
    }

    private TrustManager[] getTrustingManager() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

        } };
        return trustAllCerts;
    }
}
