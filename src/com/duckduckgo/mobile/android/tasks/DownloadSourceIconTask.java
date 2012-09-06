package com.duckduckgo.mobile.android.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;

public class DownloadSourceIconTask extends AsyncTask<Void, Void, List<SourcesObject>> {

	private static String TAG = "DownloadSourceIconTask";
	
	ImageCache cache;
	
	FileCache fileCache;
			
	public DownloadSourceIconTask(ImageCache cache) {
		this.cache = cache;
		this.fileCache = DDGApplication.getFileCache();
	}
	
	@Override
	protected List<SourcesObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<SourcesObject> returnFeed = new ArrayList<SourcesObject>();
		try {
			if (isCancelled()) return null;
			
			String body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
			fileCache.saveStringToInternal(DDGConstants.SOURCE_JSON_PATH, body);
			DDGControlVar.sourceJSON = body;
			
			Log.e(TAG, body);
			json = new JSONArray(body);
		} catch (JSONException jex) {
			Log.e(TAG, jex.getMessage(), jex);
		} catch (DDGHttpException conException) {
			Log.e(TAG, "Unable to execute Query: " + conException.getMessage(), conException);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		if (json != null) {
			if (isCancelled()) return returnFeed;
			for (int i = 0; i < json.length(); i++) {
				try {
					JSONObject nextObj = json.getJSONObject(i);
					if (nextObj != null) {
						
						String imageUrl = nextObj.optString("image");			
						String link = nextObj.getString("link");
						
						if(link != null && !link.equals("null")){
							
							URL linkUrl = new URL(link);
							
							if(imageUrl != null && imageUrl.length() != 0){
									Bitmap bitmap = DDGUtils.downloadBitmap(this, imageUrl);
									if(bitmap != null){
										String host = linkUrl.getHost();
										if (host.indexOf(".") != host.lastIndexOf(".")) {
											//Cut off the beginning, because we don't want/need it
											host = host.substring(host.indexOf(".")+1);
										}
										cache.addBitmapToCache("DUCKDUCKICO--"+host, bitmap);
									}
							}
						
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Failed to create object with info at index " + i);
				} catch (MalformedURLException e) {
					Log.e(TAG, "URL problem. Failed to create object with info at index " + i);
					e.printStackTrace();
				}
			}
		}
		
		return returnFeed;
	}
	
	@Override
	protected void onPostExecute(List<SourcesObject> feed) {	
			if (feed != null) {
				// this.listener.onSourceIconRetrieved(feed);
				
				// TODO cache the feed response
				
				DDGControlVar.sourceIconsCached = true;
				Editor editor = DDGApplication.getSharedPreferences().edit();
				editor.putBoolean("sourceiconscached", true);
				editor.commit();
			} else {
				// this.listener.onSourceIconRetrievalFailed();
			}
	}

}
