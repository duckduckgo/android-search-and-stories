package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.ReadabilityFeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.ReadabilityFeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class ReadableFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "ReadableFeedTask";
						
	private boolean requestFailed = false;
	
	private String articleUrl = null;
	
	public ReadableFeedTask(FeedObject currentFeedObject) {
		this.articleUrl = currentFeedObject.getArticleUrl();
	}	
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		String body = null;
		
		if (isCancelled()) { return null; }
		if(articleUrl == null || articleUrl.length() == 0) { return null; }
        try {
            // if an update is triggered, directly fetch from URL
            body = DDGNetworkConstants.mainClient.doGetString(articleUrl);
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
			Log.e(TAG, "body: null");
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
			BusProvider.getInstance().post(new ReadabilityFeedRetrieveErrorEvent());
			return;
		}
		
		if (feed != null) {
			BusProvider.getInstance().post(new ReadabilityFeedRetrieveSuccessEvent(feed));
		}
	}
	
}
