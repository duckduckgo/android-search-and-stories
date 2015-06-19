package com.duckduckgo.mobile.android.tasks;

import java.util.Set;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.objects.SourceInfoPair;
import com.duckduckgo.mobile.android.util.DDGUtils;

public class SourceIconsTask extends AsyncTask<Void, Void, Void> {

	private static String TAG = "SourceIconsTask";
			
	//ListView mainFeedView;
    RecyclerView mainFeedRecyclerView;
	ImageCache cache;	
	Set<SourceInfoPair> sourceInfoPairs;
		
	public SourceIconsTask(RecyclerView mainFeedRecyclerView /*ListView mainFeedView*/, Set<SourceInfoPair> sourceInfoPairs) {
		//this.mainFeedView = mainFeedView;
        this.mainFeedRecyclerView = mainFeedRecyclerView;
		this.cache = DDGApplication.getImageCache();
		this.sourceInfoPairs = sourceInfoPairs;
	}	
	
	
	@Override
	protected Void doInBackground(Void... params) {		
		if (isCancelled()) return null;				
		
		for(SourceInfoPair sourceInfo : sourceInfoPairs) {		
			// ***** save source icon to ImageCache if needed ****  
			if(sourceInfo.imageUrl != null && sourceInfo.imageUrl.length() != 0){
				if(cache.getBitmapFromCache("DUCKDUCKICO--"+sourceInfo.id, false) != null){
					// pass
				}
				else {
					DDGUtils.downloadAndSaveBitmapToCache(this, sourceInfo.imageUrl, "DUCKDUCKICO--"+sourceInfo.id);
				}
			}
			
			// ***************************************************
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		//if(mainFeedView != null) {
			//mainFeedView.invalidateViews();
		//}
        if(mainFeedRecyclerView!=null) {
            mainFeedRecyclerView.invalidate();
        }
		super.onPostExecute(result);
	}
	
}
