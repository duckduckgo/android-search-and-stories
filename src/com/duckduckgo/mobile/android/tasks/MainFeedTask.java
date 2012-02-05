package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.duckduckgo.mobile.android.DDGConstants;
import com.duckduckgo.mobile.android.objects.FeedObject;

import android.os.AsyncTask;
import android.util.Log;

public class MainFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "MainFeedTask";
	
	private FeedListener listener = null;
	
	public MainFeedTask(FeedListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		try {
			HttpClient client = new HttpClient();
			client.getParams().setParameter(HttpMethodParams.USER_AGENT, DDGConstants.USER_AGENT);
			HttpMethod get = new GetMethod(DDGConstants.MAIN_FEED_URL);

			if (isCancelled()) return returnFeed;
			
			int result = client.executeMethod(get);

			if (isCancelled()) return returnFeed;
			
			if (result != HttpStatus.SC_OK) {
				throw new Exception("Unable to execute query");
			}
			String body = get.getResponseBodyAsString();
			json = new JSONArray(body);
		} catch (JSONException jex) {
			Log.e(TAG, jex.getMessage(), jex);
		} catch (HttpException httpException) {
			Log.e(TAG, httpException.getMessage(), httpException);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
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
		if (this.listener != null) {
			this.listener.onFeedRetrieved(feed);
		}
	}
	
	public static interface FeedListener {
		public void onFeedRetrieved(List<FeedObject> feed);
	}
}
