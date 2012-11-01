package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.db.DdgDB;
import com.duckduckgo.mobile.android.listener.FeedListener;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;

public class SavedFeedTask extends AsyncTask<Void, Void, List<FeedObject>> {

	private static String TAG = "SavedFeedTask";
	
	private FeedListener listener = null;
	private DdgDB db = null;
	
	private SharedPreferences sharedPreferences;
			
	public SavedFeedTask(FeedListener listener) {
		this.listener = listener;
		this.db = DDGApplication.getDB();
		
		sharedPreferences = DDGApplication.getSharedPreferences();
	}
	
	@Override
	protected List<FeedObject> doInBackground(Void... arg0) {
		
		// retrieve from DB
		List<FeedObject> returnFeed = new ArrayList<FeedObject>();
		
		try {
				
			if (isCancelled()) return null;
			
			if(DDGControlVar.targetSource != null){
				// temporary, icon tap filter				
				returnFeed = this.db.selectByType(DDGControlVar.targetSource);
			}
			else {
				// main, preference-based filter
				Set<String> sourceSet = DDGUtils.loadSet(sharedPreferences, "sourceset");

				if(sharedPreferences.contains("sourceset_size") && !sourceSet.isEmpty()){					
					returnFeed = this.db.selectByType(sourceSet);
				}
				else {
					// this case is when default sources are not loaded
					if(DDGControlVar.defaultSourceSet == null || DDGControlVar.defaultSourceSet.isEmpty()){
						synchronized (DDGControlVar.defaultSourceSet) {
							DDGControlVar.defaultSourceSet.wait();
						}
					}
					
//					sourceSet = DDGControlVar.defaultSourceSet;
//					returnFeed = this.db.selectByType(sourceSet);

					returnFeed = this.db.selectAll();
				}
			}
			
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		return returnFeed;
	}
	
	@Override
	protected void onPostExecute(List<FeedObject> feed) {	
		if (this.listener != null) {
			if (feed != null) {
				this.listener.onFeedRetrieved(feed);
			} else {
				this.listener.onFeedRetrievalFailed();
			}
		}
	}
}
