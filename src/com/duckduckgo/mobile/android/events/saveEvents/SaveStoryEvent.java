package com.duckduckgo.mobile.android.events.saveEvents;

import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class SaveStoryEvent extends SaveEvent {
	
	private DuckDuckGo context;
	private FeedObject feedObject;

	public SaveStoryEvent(DuckDuckGo context, FeedObject feedObject){
		this.context = context;
		this.feedObject = feedObject;
	}
	
	public void process() {
		context.itemSaveFeed(feedObject, null);
		context.syncAdapters();
		Toast.makeText(context, R.string.ToastSaveStory, Toast.LENGTH_SHORT).show();
	}
}
