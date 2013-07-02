package com.duckduckgo.mobile.android.events.feedEvents;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class SavedFeedItemSelectedEvent extends FeedItemSelectedEvent {
	
	public SavedFeedItemSelectedEvent(FeedObject feedObject){
		super(feedObject);
	}
	
}
