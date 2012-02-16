package com.duckduckgo.mobile.android.tasks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGConstants;
import com.duckduckgo.mobile.android.download.DownloadableImage;
import com.duckduckgo.mobile.android.download.ImageCache;

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
			HttpClient client = new HttpClient();
			client.getParams().setParameter(HttpMethodParams.USER_AGENT, DDGConstants.USER_AGENT);
			HttpMethod get = new GetMethod(url);

			if (isCancelled()) return null;
		
			int result = client.executeMethod(get);

			if (isCancelled()) return null;
		
			if (result != HttpStatus.SC_OK) {
				throw new Exception("Unable to execute query");
			}
			InputStream inputStream = null;
			try {
				inputStream = get.getResponseBodyAsStream();
				if (inputStream != null) {
					return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
				}
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (HttpException httpException) {
			Log.e(TAG, httpException.getMessage(), httpException);
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
