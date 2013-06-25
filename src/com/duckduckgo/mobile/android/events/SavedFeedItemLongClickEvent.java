package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class SavedFeedItemLongClickEvent extends FeedItemLongClickEvent {
	
	public SavedFeedItemLongClickEvent(FeedObject feedObject){
		super(feedObject);
	}
	
}
