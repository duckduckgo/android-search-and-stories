package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;

public class CacheFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "CacheFeedTask";
			
	private FileCache fileCache = null;
			
	private boolean requestFailed = false;
	
	DuckDuckGo activity;
			
	public CacheFeedTask(DuckDuckGo activity) {
		this.activity = activity;
		this.fileCache = DDGApplication.getFileCache();	
	}
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		String body = null;
		
		if (isCancelled()) return null;

		body = DDGControlVar.storiesJSON;

		// try getting JSON from file cache
		if(body == null){
			synchronized(fileCache) {
				body = fileCache.getStringFromInternal(DDGConstants.STORIES_JSON_PATH);
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
			Log.e(TAG, "cacheFeed body: null");
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
			BusProvider.getInstance().post(new FeedRetrieveSuccessEvent(feed, REQUEST_TYPE.FROM_CACHE));
		}
	}
	
}
