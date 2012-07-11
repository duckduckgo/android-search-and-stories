package com.duckduckgo.mobile.android;

import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

import android.app.Application;

public class DDGApplication extends Application {

	private static final ImageCache imageCache = new ImageCache(null);
	private static final ImageDownloader imageDownloader = new ImageDownloader(imageCache);
	
	@Override
	public void onCreate() {
		super.onCreate();
		imageCache.setFileCache(new FileCache(this.getApplicationContext()));
		
		DDGNetworkConstants.initialize();
	}
	
	public static ImageDownloader getImageDownloader() {
		return imageDownloader;
	}
}
