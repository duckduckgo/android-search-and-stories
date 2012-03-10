package com.duckduckgo.mobile.android;

import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.download.ImageDownloader;

import android.app.Application;

public class DDGApplication extends Application {

	private static final ImageDownloader imageDownloader = new ImageDownloader(new ImageCache());
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public static ImageDownloader getImageDownloader() {
		return imageDownloader;
	}
}
