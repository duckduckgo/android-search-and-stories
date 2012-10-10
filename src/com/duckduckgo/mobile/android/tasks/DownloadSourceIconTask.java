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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.SourceProcessor;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;

public class DownloadSourceIconTask extends AsyncTask<Void, Void, List<SourcesObject>> {

	private static String TAG = "DownloadSourceIconTask";
	
	ImageCache cache;
	
	FileCache fileCache;
	private Context context;
		
	private boolean testedCache = false;
	private boolean isCached = false;
	private boolean readSimpleSourceMap = false;
	private boolean readDefaultSourceSet = false;
			
	public DownloadSourceIconTask(Context context, ImageCache cache) {
		this.cache = cache;
		this.fileCache = DDGApplication.getFileCache();
		this.context = context;
	}
	
	@Override
	protected List<SourcesObject> doInBackground(Void... arg0) {
		JSONArray json = null;
		List<SourcesObject> returnFeed = new ArrayList<SourcesObject>();
		Map<String, String> simpleFeed = new HashMap<String, String>();
		try {
			if (isCancelled()) return null;
			
			String body = null;
			
			
			if(!DDGControlVar.hasUpdatedSources) {
				// if an update is triggered, directly fetch from URL
				body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
				fileCache.saveStringToInternal(DDGConstants.SOURCE_JSON_PATH, body);
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
			
			if(!DDGControlVar.hasUpdatedSources || DDGControlVar.simpleSourceMap == null){
				// read dump object line by line and create simple source map
				fileCache.processFromInternal(DDGConstants.SOURCE_SIMPLE_PATH, new SourceProcessor());
				
				if(DDGControlVar.simpleSourceMap == null || DDGControlVar.simpleSourceMap.isEmpty()){
					readSimpleSourceMap = true;
				}
			}
			
			if(!DDGControlVar.hasUpdatedSources || DDGControlVar.defaultSourceSet == null 
					|| DDGControlVar.defaultSourceSet.isEmpty()){
				readDefaultSourceSet = true;
			}
			
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
						String title = nextObj.getString("title");
						int def = nextObj.getInt("default");
						
						if(id != null && !id.equals("null")){
							
							simpleFeed.put(id, title);
							
							if(def == 1){
								DDGControlVar.defaultSourceSet.add(id);
							}
																					
							if(imageUrl != null && imageUrl.length() != 0){
								// if mode = reading simple source map, then do not exit upon icon cache detection
										if(!testedCache){
											if(cache.getBitmapFromCache("DUCKDUCKICO--"+id, false) != null){
												isCached = true;
												if(!readSimpleSourceMap && !readDefaultSourceSet) {
													return returnFeed;
												}
											}
											testedCache = true;
										}
								
									if(!isCached) {
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
		
		dumpSimpleSourceMap(simpleFeed);
		
		// default source related
		synchronized (DDGControlVar.defaultSourceSet) {
			DDGControlVar.defaultSourceSet.notify();
		}
		
		if(readDefaultSourceSet){
			DDGUtils.saveSet(DDGApplication.getSharedPreferences(), DDGControlVar.defaultSourceSet, "defaultset");
		}
		
		return returnFeed;
	}
	
	private void dumpSimpleSourceMap(Map<String,String> sourceMap) {
		
		// dump simple source list serialization before method exit
		if(!DDGControlVar.hasUpdatedFeed || DDGControlVar.simpleSourceMap == null || DDGControlVar.simpleSourceMap.isEmpty()) {
			
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
