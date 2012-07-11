package com.duckduckgo.mobile.android.tasks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.download.DownloadableImage;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

//TODO: Eventually, don't take an ImageView, take a Downloadable object
public class DownloadBitmapTask extends AsyncTask<String, Void, Bitmap> {
	private static final String TAG = "DownloadBitmapTask";
	
	public String url;
	private final WeakReference<DownloadableImage> imageViewReference;
	private final ImageCache imageCache;
	
	public DownloadBitmapTask(DownloadableImage image, ImageCache imageCache) {
		imageViewReference = new WeakReference<DownloadableImage>(image);
		this.imageCache = imageCache;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		url = params[0];
		return downloadBitmap(url);
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
			return;
		}

		if (bitmap != null) {
			if (bitmap.getHeight()<=1 && bitmap.getWidth()<=1) {
				Log.d(TAG, "URL: " + url);
				Log.d(TAG, "Got Image that was too small!");
				bitmap = null;
			}
		}

		imageCache.addBitmapToCache(url, bitmap);
		
		if (imageViewReference != null) {
			DownloadableImage image = imageViewReference.get();
			if (image != null) {
				image.setBitmap(bitmap);
			}
		}
	}
	
	private Bitmap downloadBitmap(String url) {
		try {

			if (isCancelled()) return null;
				
			InputStream inputStream = null;
			try {
				inputStream = DDGNetworkConstants.mainClient.doGetStream(url);
				if (inputStream != null) {
					// FIXME large bitmaps cause OutOfMemoryErrors
					// see: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
					return BitmapFactory.decodeStream(inputStream);
				}
			} 
			catch(DDGHttpException conex) {
				Log.e(TAG, "Http Call Returned Bad Status. " + conex.getHttpStatus());
				imageCache.addFailedUrl(url);
				throw conex;
			}
			finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (DDGHttpException conException) {
			Log.e(TAG, conException.getMessage(), conException);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return null;
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
