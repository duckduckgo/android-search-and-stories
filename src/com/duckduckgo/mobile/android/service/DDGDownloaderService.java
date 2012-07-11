package com.duckduckgo.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.duckduckgo.mobile.android.network.DDGHttpClient;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

public class DDGDownloaderService extends Service {
	
	private DDGHttpClient client;
	private final IBinder binder = new LocalBinder(); 
	
		
	@Override
	public void onCreate() {
	    this.client = DDGNetworkConstants.getNewClient();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
		
	public class LocalBinder extends Binder 
	{ 
		public DDGDownloaderService getService() 
		{ 
			return DDGDownloaderService.this; 
		} 
	}
	
}
