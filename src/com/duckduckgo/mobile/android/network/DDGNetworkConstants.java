package com.duckduckgo.mobile.android.network;

import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.params.ConnManagerParams;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRouteBean;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

public class DDGNetworkConstants {
	public static DDGHttpClient mainClient = null;
	private static ClientConnectionManager mainConnManager = null;
    private static HttpParams httpParams = new BasicHttpParams();
    
//    public static Map<String, String> extraHeaders = new HashMap<String, String>();
	
	public static void initialize(){
		// Create and initialize HTTP parameters
        httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        // Increase default max connection per route to 20
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));
         
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        mainConnManager = new ThreadSafeClientConnManager();
        mainClient = new DDGHttpClient(mainConnManager, httpParams);
        
        
        // initialize referrer headers to use with WebView
//        extraHeaders.put("Referer", "http://www.google.com/");
	}
}
