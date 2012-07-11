package com.duckduckgo.mobile.android.network;

import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.params.ConnManagerParams;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRouteBean;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

public class DDGNetworkConstants {
	public static DDGHttpClient mainClient = null;
	private static ClientConnectionManager mainConnManager = null;
    private static HttpParams httpParams = new BasicHttpParams();
	
	public static void initialize(){
		// Create and initialize HTTP parameters
        httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        // Increase default max connection per route to 20
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));
         
        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
         
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        mainConnManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        mainClient = new DDGHttpClient(mainConnManager, httpParams);
	}
	
	public static DDGHttpClient getNewClient(){
		return new DDGHttpClient(mainConnManager,httpParams);
	}
}
