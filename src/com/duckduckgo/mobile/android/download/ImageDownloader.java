package com.duckduckgo.mobile.android.download;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.duckduckgo.mobile.android.tasks.DownloadBitmapTask;

import android.graphics.Bitmap;
import android.util.Log;

//TODO: Any way we can limit the number of simultaneous downloads? Do we need to?
//TODO: Any way we can find out that multiple objects are attempting to get the same item?
//TODO: Think we need a download service for those that these tasks can register and wait on, or some such...

public class ImageDownloader {
	private static final String TAG = "ImageDownloader";
	
	private final ImageCache cache;
	
    private Map<DownloadableImage, String> imageViews=Collections.synchronizedMap(new WeakHashMap<DownloadableImage, String>());
	
	public ImageDownloader(ImageCache cache) {
		this.cache = cache;
	}
	
    boolean imageViewReused(DownloadableImage image, String url){
        String tag=imageViews.get(image);
        if(tag==null || !tag.equals(url))
            return true;
        return false;
    }
	
	//TODO: Should take a Downloadable object
	public void download(String url, DownloadableImage image, boolean onlyUseMemCache) {
		if (url == null || url.isEmpty()) {
			//Cancel anything downloading, set the image to default, and return
			cancelPreviousDownload(url, image);
			image.setDefault();
			return;

		}
		imageViews.put(image, url);

		Bitmap bitmap = cache.getBitmapFromCache(url, onlyUseMemCache);
		
		if(cache.checkFail(url)){
			url = null;
		}
		

		if (bitmap == null) {
			Log.d(TAG, "Attempting download of URL: " + url);
			
			if(onlyUseMemCache){
				image.setBitmap(null);
				image.setMemCacheDrawn(false);
				return;
			}
			
			attemptDownload(url, image);
		} else {
			Log.d(TAG, "Using Cached Image for URL: " + url);
			cancelPreviousDownload(url, image);
			image.setBitmap(bitmap);
			
			if(onlyUseMemCache){
				image.setMemCacheDrawn(true);
			}
		}
	}
	
	private void attemptDownload(String url, DownloadableImage image) {
		if (url == null) {
			//No image to download!
			image.setDownloadBitmapTask(null);
			image.setBitmap(null);
			return;
		}
		
		if (!cancelPreviousDownload(url, image)) {
			Log.d(TAG, "Already downloading URL: " + url);
			//We are already downloading that exact image
			return;
		}
		
		if(imageViewReused(image,url))
            return;
		
		DownloadBitmapTask task = new DownloadBitmapTask(image, cache);
		image.setDownloadBitmapTask(task);
		image.setDefault();
		task.execute(url);
	}
	
	private static boolean cancelPreviousDownload(String url, DownloadableImage image) {
		DownloadBitmapTask task = image.getDownloadBitmapTask();
		
		if (task != null) {
			String bitmapUrl = task.url;
			if ((bitmapUrl == null) || !bitmapUrl.equals(url)) {
				task.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}
}
