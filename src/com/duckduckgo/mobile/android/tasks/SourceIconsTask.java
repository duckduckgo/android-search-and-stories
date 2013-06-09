package com.duckduckgo.mobile.android.tasks;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public class SourceIconsTask extends AsyncTask<Void, Void, Void> {

	private static String TAG = "SourceIconsTask";
			
	ListView mainFeedView;
	ImageCache cache;	
		
	public SourceIconsTask(ListView mainFeedView) {
		this.mainFeedView = mainFeedView;
		this.cache = DDGApplication.getImageCache();
	}	
	
	/**
	 * Retrieves source response (type_info=1) from Watrcoolr
	 * and initializes:
	 * 1. Source icons (high-quality icons from Watrcoolr)
	 * 2. Default sources
	 */
	private void initializeSources() {
		JSONArray json = null;
		Set<String> defaultSet = new HashSet<String>(); 
		
		try {			
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
														
							// ***** save source icon to ImageCache if needed ****  
							if(imageUrl != null && imageUrl.length() != 0){
								if(cache.getBitmapFromCache("DUCKDUCKICO--"+id, false) != null){
									// pass
								}
								else {
									DDGUtils.downloadAndSaveBitmapToCache(this, imageUrl, "DUCKDUCKICO--"+id);
								}
							}
							// ***************************************************
						
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Failed to create object with info at index " + i);
				}
			}
		}
		
		DDGControlVar.defaultSources = defaultSet;
		
		PreferencesManager.saveDefaultSources(defaultSet);
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {		
		if (isCancelled()) return null;				
		initializeSources();				
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if(mainFeedView != null) {
			mainFeedView.invalidateViews();
		}
		super.onPostExecute(result);
	}
	
}
