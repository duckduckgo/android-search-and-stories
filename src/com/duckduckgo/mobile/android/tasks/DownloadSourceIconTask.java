package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
	private Context context;
			
	public DownloadSourceIconTask(Context context, ImageCache cache) {
		this.cache = cache;
		this.fileCache = DDGApplication.getFileCache();
		this.context = context;
	}
	
	@Override
	protected List<SourcesObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<SourcesObject> returnFeed = new ArrayList<SourcesObject>();
		Set<String> defaultSet = new HashSet<String>(); 
		
		try {
			if (isCancelled()) return null;
			
			String body = null;
						
			// get source response (type_info=1)
			body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
						
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
						String id = nextObj.getString("id");
						int def = nextObj.getInt("default");
						
						if(id != null && !id.equals("null")){
							// record new default list
							if(def == 1){
								defaultSet.add(id);
							}
																					
							if(imageUrl != null && imageUrl.length() != 0){
								// if mode = reading simple source map, then do not exit upon icon cache detection
								if(cache.getBitmapFromCache("DUCKDUCKICO--"+id, false) != null){
									// pass
								}
								else {
									Bitmap bitmap = DDGUtils.downloadBitmap(this, imageUrl);
									if(bitmap != null){
										cache.addBitmapToCache("DUCKDUCKICO--"+id, bitmap);
									}
								}
							}
						
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Failed to create object with info at index " + i);
				}
			}
		}
		
		DDGControlVar.defaultSources = defaultSet;
				
		return returnFeed;
	}
	
	@Override
	protected void onPostExecute(List<SourcesObject> result) {
		super.onPostExecute(result);
		DDGControlVar.isDefaultsChecked = true;
	}

}
