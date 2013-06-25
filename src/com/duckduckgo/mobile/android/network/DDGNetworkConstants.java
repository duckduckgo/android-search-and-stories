package com.duckduckgo.mobile.android.network;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;

import android.app.Application;
import android.content.Context;
import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.PreferencesManager;

import info.guardianproject.onionkit.trust.StrongHttpsClient;
import info.guardianproject.onionkit.web.WebkitProxy;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.params.ConnManagerParams;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRouteBean;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

import com.duckduckgo.mobile.android.R;

public class DDGNetworkConstants {
	public static DDGHttpClient mainClient;
	private static ClientConnectionManager mainConnManager;
    private static HttpParams httpParams = new BasicHttpParams();    
    private final static String PROXY_HOST = "127.0.0.1";
    private final static int PROXY_HTTP_PORT = 8118; // default for Orbot/Tor
    
//    public static Map<String, String> extraHeaders = new HashMap<String, String>();
	
	public static void initialize(DDGApplication application){
        initializeMainClient(application, PreferencesManager.getEnableTor());
	}

    public static void initializeMainClient(Application application, boolean enableTor){
        // Create and initialize HTTP parameters
        httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        // Increase default max connection per route to 20
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(10));

        SchemeRegistry schemeRegistry = prepareSchemeRegistry(application);

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        mainConnManager = new ThreadSafeClientConnManager(schemeRegistry);
        mainClient = new DDGHttpClient(application.getApplicationContext(), mainConnManager, httpParams);
        if(enableTor){
            mainClient.getStrongTrustManager().setNotifyVerificationFail(true);
            mainClient.getStrongTrustManager().setNotifyVerificationSuccess(true);
            mainClient.useProxy(true, ConnRoutePNames.DEFAULT_PROXY, PROXY_HOST, PROXY_HTTP_PORT);
        }
    }

    private static SchemeRegistry prepareSchemeRegistry(Application application) {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        try {
            char[] clientPassword = "daxtheduck".toCharArray();
            KeyStore mTrustStore = KeyStore.getInstance("BKS");
            InputStream in = application.getResources().openRawResource(R.raw.trust_store);
            mTrustStore.load(in, clientPassword);
            SSLSocketFactory sslSocketFactory = new SSLSocketFactory(mTrustStore);
            schemeRegistry.register(new Scheme("https", 443, sslSocketFactory));

            javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory
                    .getInstance(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(mTrustStore);

            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return schemeRegistry;
    }
}
