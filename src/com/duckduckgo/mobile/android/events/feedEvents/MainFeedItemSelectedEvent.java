package com.duckduckgo.mobile.android.events.feedEvents;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class MainFeedItemSelectedEvent extends FeedItemSelectedEvent {
	
	public MainFeedItemSelectedEvent(FeedObject feedObject){
		super(feedObject);
	}
	
}
