package com.duckduckgo.mobile.android.network;

import java.io.InputStream;
import java.security.KeyStore;

import android.content.Context;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.params.ConnManagerParams;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRouteBean;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

import com.duckduckgo.mobile.android.R;

public class DDGNetworkConstants {
	public static DDGHttpClient mainClient;
	private static ClientConnectionManager mainConnManager;
    private static HttpParams httpParams = new BasicHttpParams();    
    
//    public static Map<String, String> extraHeaders = new HashMap<String, String>();
	
	public static void initialize(Context context){
		// Create and initialize HTTP parameters
        httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        // Increase default max connection per route to 20
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));        

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        
        try {
        	char[] clientPassword = new String("daxtheduck").toCharArray();
	        KeyStore mTrustStore = KeyStore.getInstance("BKS");
	        InputStream in = context.getResources().openRawResource(R.raw.trust_store);
	        mTrustStore.load(in, clientPassword);	    
	        SSLSocketFactory sslSocketFactory = new SSLSocketFactory(mTrustStore);
	        schemeRegistry.register(new Scheme("https", 443, sslSocketFactory));
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
         
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        mainConnManager = new ThreadSafeClientConnManager(schemeRegistry);
        mainClient = new DDGHttpClient(mainConnManager, httpParams);        
        
        
        // initialize referrer headers to use with WebView
//        extraHeaders.put("Referer", "http://www.google.com/");
	}
	
	public static DDGHttpClient getNewClient(){
		return new DDGHttpClient(mainConnManager,httpParams);
	}
}
