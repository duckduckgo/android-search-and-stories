package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SourceInfoPair;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;

public class MainFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "MainFeedTask";
			
	ImageCache cache;
	private FileCache fileCache = null;
	
	DuckDuckGo activity;
	
	private boolean requestFailed = false;
	
	public MainFeedTask(DuckDuckGo activity) {
		this.activity = activity;
		this.cache = DDGApplication.getImageCache();
		this.fileCache = DDGApplication.getFileCache();
	}
	
	private String getFeedUrl() throws InterruptedException {
		String feedUrl = DDGConstants.MAIN_FEED_URL;
		
		if(DDGControlVar.targetSource != null){
			// temporary, icon tap filter
			feedUrl += "&s=" + DDGControlVar.targetSource;
		}
		else {
			// main, preference-based filter			
			String paramString = "";
			for(String s : DDGControlVar.getRequestSources()){
				paramString += s + ",";
			}
			if(paramString.length() > 0){
				paramString = paramString.substring(0,paramString.length()-1);
			}

			feedUrl += "&s=" + paramString;
		}
		
		return feedUrl;
	}
	
	
	/**
	 * Retrieves source response (type_info=1) from Watrcoolr
	 * and initializes:
	 * 1. Source icons (high-quality icons from Watrcoolr)
	 * 2. Default sources
	 */
	private Set<SourceInfoPair> initializeSources() {
		JSONArray json = null;
		Set<String> defaultSet = new HashSet<String>(); 
		Set<SourceInfoPair> sourceInfoPairs = new HashSet<SourceInfoPair>();
		
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
							
							sourceInfoPairs.add(new SourceInfoPair(id, imageUrl));		
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Failed to create object with info at index " + i);
				}
			}
		}
		
		DDGControlVar.defaultSources = defaultSet;
		
		PreferencesManager.saveDefaultSources(defaultSet);
		
		return sourceInfoPairs;
	}
	
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		String body = null;
		
		if (isCancelled()) return null;
		
		
		if(!DDGControlVar.isDefaultsChecked) {
			Set<SourceInfoPair> sourceInfoPairs = initializeSources();
			new SourceIconsTask(activity.mPullRefreshFeedView.getRefreshableView(), sourceInfoPairs).execute();
			DDGControlVar.isDefaultsChecked = true;
		}
		

		try {
			// if an update is triggered, directly fetch from URL
			String feedUrl = getFeedUrl();
			if(feedUrl == null)
				return returnFeed;
			body = DDGNetworkConstants.mainClient.doGetString(feedUrl);
			synchronized(fileCache) {
				fileCache.saveStringToInternal(DDGConstants.STORIES_JSON_PATH, body);
				DDGControlVar.storiesJSON = new String(body);
			}
		}				
		catch (Exception e) {
			requestFailed = true;
			Log.e(TAG, e.getMessage(), e);
		}

		if(body != null) {	
			try {
				json = new JSONArray(body);
			} catch (JSONException jex) {
				Log.e(TAG, jex.getMessage(), jex);
			}
		}
		else {
			Log.e(TAG, "mainFeed body: null");
		}

		if (json != null) {
			if (isCancelled()) return returnFeed;
			for (int i = 0; i < json.length(); i++) {
				try {
					JSONObject nextObj = json.getJSONObject(i);
					if (nextObj != null) {
						FeedObject feed = new FeedObject(nextObj);
						if (feed != null) {
							returnFeed.add(feed);
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Failed to create object with info at index " + i);
				}
			}
		}
		
		return returnFeed;
	}
	
	@Override
	protected void onPostExecute(List<FeedObject> feed) {		
		
		if(requestFailed) {
			BusProvider.getInstance().post(new FeedRetrieveErrorEvent());
			return;
		}
		
		if (feed != null) {
			BusProvider.getInstance().post(new FeedRetrieveSuccessEvent(feed, REQUEST_TYPE.FROM_NETWORK));
		}			
	}
	
}
