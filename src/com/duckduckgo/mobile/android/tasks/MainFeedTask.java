package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
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

public class MainFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "MainFeedTask";
	
	private Context context = null;
	private FeedListener listener = null;
	
	private SharedPreferences sharedPreferences;
	
	private FileCache fileCache = null;
	
	private boolean cacheRead;
			
	public MainFeedTask(Context context, FeedListener listener) {
		this.context = context;
		this.listener = listener;
		this.fileCache = DDGApplication.getFileCache();
		
		sharedPreferences = DDGApplication.getSharedPreferences();
	}
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		String feedUrl = DDGConstants.MAIN_FEED_URL;
		String body = null;
		cacheRead = false;
		try {
			if (isCancelled()) return null;
			
			if(DDGControlVar.targetSource != null){
				// temporary, icon tap filter
				feedUrl += "&s=" + DDGControlVar.targetSource;
			}
			else {
				// main, preference-based filter
				Set<String> sourceSet = DDGUtils.loadSet(sharedPreferences, "sourceset");

				if(sharedPreferences.contains("sourceset_size") && !sourceSet.isEmpty()){
					String paramString = "";
					for(String s : sourceSet){
						paramString += s + ",";
					}
					if(paramString.length() > 0){
						paramString = paramString.substring(0,paramString.length()-1);
					}

					feedUrl += "&s=" + paramString;

				}
				else {
					// this case is when default sources are not loaded
					if(DDGControlVar.defaultSourceSet == null || DDGControlVar.defaultSourceSet.isEmpty()){
						synchronized (DDGControlVar.defaultSourceSet) {
							DDGControlVar.defaultSourceSet.wait();
						}
					}
					
					String paramString = "";
					for(String s : DDGControlVar.defaultSourceSet){
						paramString += s + ",";
					}
					if(paramString.length() > 0){
						paramString = paramString.substring(0,paramString.length()-1);
					}

					feedUrl += "&s=" + paramString;
					
				}
			}
						
			// if an update is triggered, directly fetch from URL
			body = DDGNetworkConstants.mainClient.doGetString(feedUrl);
			fileCache.saveStringToInternal(DDGConstants.STORIES_JSON_PATH, body);
			DDGControlVar.storiesJSON = new String(body);
			
			
			Log.e(TAG, body);
		} catch (DDGHttpException conException) {
			Log.e(TAG, "Unable to execute Query: " + conException.getMessage(), conException);
			
			body = DDGControlVar.storiesJSON;
			
			// try getting JSON from file cache
			if(body == null){
				body = fileCache.getStringFromInternal(DDGConstants.STORIES_JSON_PATH);
			}
			
			cacheRead = true;
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		if(body != null) {	
			try {
				json = new JSONArray(body);
			} catch (JSONException jex) {
				Log.e(TAG, jex.getMessage(), jex);
			}
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
		if(cacheRead) {
			Toast.makeText(context, R.string.InfoReadStoriesFromCache, Toast.LENGTH_LONG).show();
		}
		
		if (this.listener != null) {
			if (feed != null) {
				this.listener.onFeedRetrieved(feed);
			} else {
				this.listener.onFeedRetrievalFailed();
			}
		}
	}
	
}
