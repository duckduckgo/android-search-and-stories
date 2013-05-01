package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.listener.FeedListener;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public class MainFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "MainFeedTask";
	
	private Context context = null;
	private FeedListener listener = null;
	
	private SharedPreferences sharedPreferences;
	
	private FileCache fileCache = null;
		
	private boolean fromCache = false;
	
	private boolean requestFailed = false;
	
	public MainFeedTask(Context context, FeedListener listener) {
		this(context, listener, false);
	}	
			
	public MainFeedTask(Context context, FeedListener listener, boolean fromCache) {
		this.context = context;
		this.listener = listener;
		this.fileCache = DDGApplication.getFileCache();
		
		this.fromCache = fromCache;
		
		sharedPreferences = DDGApplication.getSharedPreferences();
	}
	
	private String getFeedUrl() throws InterruptedException {
		String feedUrl = DDGConstants.MAIN_FEED_URL;
		
		if(DDGControlVar.targetSource != null){
			// temporary, icon tap filter
			feedUrl += "&s=" + DDGControlVar.targetSource;
		}
		else {
			// main, preference-based filter
			if(DDGControlVar.defaultSources == null || DDGControlVar.defaultSources.isEmpty()){
				synchronized (DDGControlVar.defaultSources) {
					DDGControlVar.defaultSources.wait();
				}
			}
			
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
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		String body = null;
		
		if (isCancelled()) return null;

		if(this.fromCache) {
			body = DDGControlVar.storiesJSON;

			// try getting JSON from file cache
			if(body == null){
				synchronized(fileCache) {
					body = fileCache.getStringFromInternal(DDGConstants.STORIES_JSON_PATH);
				}
			}
		}
		else {

			try {				
				// if an update is triggered, directly fetch from URL
				String feedUrl = getFeedUrl();
				if(feedUrl == null)
					return returnFeed;
				body = DDGNetworkConstants.mainClient.doGetString(getFeedUrl());
				synchronized(fileCache) {
					fileCache.saveStringToInternal(DDGConstants.STORIES_JSON_PATH, body);
					DDGControlVar.storiesJSON = new String(body);
				}
			}				
			catch (Exception e) {
				requestFailed = true;
				Log.e(TAG, e.getMessage(), e);
			}
		}

		if(body != null) {	
			try {
				json = new JSONArray(body);
			} catch (JSONException jex) {
				Log.e(TAG, jex.getMessage(), jex);
			}
		}
		else {
			Log.e(TAG, "body: null - fromCache:" + fromCache);
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
			this.listener.onFeedRetrievalFailed();
			return;
		}
		
		if (this.listener != null && feed != null) {
			this.listener.onFeedRetrieved(feed, fromCache);
		}
	}
	
}
