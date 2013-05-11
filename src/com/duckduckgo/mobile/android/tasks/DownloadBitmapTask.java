package com.duckduckgo.mobile.android.tasks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.DownloadableImage;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.util.DDGUtils;

//TODO: Eventually, don't take an ImageView, take a Downloadable object
public class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
	private static final String TAG = "DownloadBitmapTask";
	
	public String url;
	private WeakReference<DownloadableImage> imageViewReference = null;
	private final ImageCache imageCache;
	public boolean isCompleted = false;
	
	// flag for background download - not updating/using imageViewReference
	public boolean isBackground = true;
	
	public DownloadBitmapTask(DownloadableImage image, ImageCache imageCache) {
		if(image != null) {
			imageViewReference = new WeakReference<DownloadableImage>(image);
			isBackground = false;
		}
		this.imageCache = imageCache;
	}
	
	public void setBackground() {
		isBackground = true;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap;
		url = params[0];
		
		if(url.startsWith("CUSTOM__")) {
			bitmap = DDGApplication.getFileCache().getBitmapFromImageFile(url);
		}
		else {
			bitmap = DDGUtils.downloadBitmap(this, url);
		}
		
		if (isCancelled()) {
			bitmap = null;
			isCompleted = true;
			
			return null;
		}

		if (bitmap != null) {
			if (bitmap.getHeight()<=1 && bitmap.getWidth()<=1) {
				Log.d(TAG, "URL: " + url);
				Log.d(TAG, "Got Image that was too small!");
				bitmap = null;
			}
		}
		
		imageCache.addBitmapToCache(url, bitmap);
		
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {	
		
		Log.v(TAG,"IS BACK: " + isBackground);
		if(!isBackground) {
			// downloading visible item
			
			if (imageViewReference != null) {
				DownloadableImage image = imageViewReference.get();
				if (image != null) {
					image.setBitmap(bitmap);
				}
			}
		}
		
		isCompleted = true; 
		
//		// signal task completion
//		while(true) {
//			synchronized (DDGControlVar.taskCompleteSignal) {
//				DDGControlVar.taskCompleteSignal.task = this;
//				DDGControlVar.taskCompleteSignal.notifyAll();
//				break;
//			}
//		}
	}
	
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}
		
		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break;
					} else {
						bytesSkipped = 1;
					}
				}
			}
			return totalBytesSkipped;
		}
	}
}
