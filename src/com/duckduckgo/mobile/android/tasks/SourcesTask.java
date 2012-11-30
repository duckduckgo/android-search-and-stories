package com.duckduckgo.mobile.android.tasks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
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
	private Context context;
	
	private boolean cacheRead;
		
	public SourcesTask(Context context, SourcesListener listener) {
		this.listener = listener;
		this.fileCache = DDGApplication.getFileCache();
		this.context = context;
	}
	
	@Override
	protected List<SourcesObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<SourcesObject> returnFeed = new ArrayList<SourcesObject>();
		Map<String, String> simpleFeed = new HashMap<String, String>();
		String body = null;
		cacheRead = false;
		try {
			if (isCancelled()) return null;
			
			if(!DDGControlVar.hasUpdatedFeed) {
				// if an update is triggered, directly fetch from URL
				body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
				fileCache.saveStringToInternal(DDGConstants.SOURCE_JSON_PATH, body);
				DDGControlVar.sourceJSON = new String(body);
			}
			
			else {
				body = DDGControlVar.sourceJSON;
			
				if(body == null){
					body = fileCache.getStringFromInternal(DDGConstants.SOURCE_JSON_PATH);
					
					if(body == null){	// still null
						body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
						fileCache.saveStringToInternal(DDGConstants.SOURCE_JSON_PATH, body);
					}
				}
			
			}
			
			Log.e(TAG, body);
		} catch (DDGHttpException conException) {
			Log.e(TAG, "Unable to execute Query: " + conException.getMessage(), conException);
			
			body = DDGControlVar.sourceJSON;
			
			// try getting JSON from file cache
			if(body == null){
				body = fileCache.getStringFromInternal(DDGConstants.SOURCE_JSON_PATH);
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
			if (isCancelled()) {
				// dump source map to cache file before method exit
				dumpSimpleSourceMap(simpleFeed);
				return returnFeed;
			}
			for (int i = 0; i < json.length(); i++) {
				try {
					JSONObject nextObj = json.getJSONObject(i);
					if (nextObj != null) {
						SourcesObject feed = new SourcesObject(nextObj);
						if (feed != null) {
							simpleFeed.put(feed.getId(),feed.getTitle());
							returnFeed.add(feed);
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Failed to create object with info at index " + i);
				}
			}
		}
		
		// dump source map to cache file before method exit
		dumpSimpleSourceMap(simpleFeed);
		return returnFeed;
	}
	
	@Override
	protected void onPostExecute(List<SourcesObject> feed) {	
		if(cacheRead) {
			Toast.makeText(context, R.string.InfoReadStoriesFromCache, Toast.LENGTH_LONG).show();
		}
		
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
	
	private void dumpSimpleSourceMap(Map<String,String> sourceMap) {
		
		// dump simple source list serialization before method exit
		if(!DDGControlVar.hasUpdatedFeed || DDGControlVar.simpleSourceMap == null) {
			
			try {

				FileOutputStream fos = context.openFileOutput(DDGConstants.SOURCE_SIMPLE_PATH, Context.MODE_PRIVATE);
				String line;

				for(Map.Entry<String,String> e : sourceMap.entrySet()) {
					line = e.getKey() + "__" + e.getValue() + "\n";
					fos.write(line.getBytes());
				}

				fos.close();

			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		
		DDGControlVar.simpleSourceMap = sourceMap;
	}
}
