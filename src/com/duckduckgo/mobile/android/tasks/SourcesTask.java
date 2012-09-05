package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;

public class SourcesTask extends AsyncTask<Void, Void, List<SourcesObject>> {

	private static String TAG = "SourcesTask";
	
	private SourcesListener listener = null;
	
	private FileCache fileCache = null;
		
	public SourcesTask(SourcesListener listener) {
		this.listener = listener;
		this.fileCache = DDGApplication.getFileCache();
	}
	
	@Override
	protected List<SourcesObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<SourcesObject> returnFeed = new ArrayList<SourcesObject>();
		try {
			if (isCancelled()) return null;
			
			String body = DDGControlVar.sourceJSON;
			
			if(body == null){
				body = fileCache.getStringFromInternal(DDGConstants.SOURCE_JSON_PATH);
				
				if(body == null){	// still null
					DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
					fileCache.saveStringToInternal(DDGConstants.SOURCE_JSON_PATH, body);
				}
			}
			
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
						SourcesObject feed = new SourcesObject(nextObj);
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
	protected void onPostExecute(List<SourcesObject> feed) {	
		if (this.listener != null) {
			if (feed != null) {
				this.listener.onSourcesRetrieved(feed);
			} else {
				this.listener.onSourcesRetrievalFailed();
			}
		}
	}
	
	public static interface SourcesListener {
		public void onSourcesRetrieved(List<SourcesObject> feed);
		public void onSourcesRetrievalFailed();
	}
}
