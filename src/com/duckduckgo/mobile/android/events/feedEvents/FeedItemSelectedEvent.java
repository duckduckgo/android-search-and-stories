package com.duckduckgo.mobile.android.events.feedEvents;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class FeedItemSelectedEvent extends Event {
	
	public FeedObject feedObject = null;

	public String feedId = null;

	public FeedItemSelectedEvent(String feedId) {
		this.feedId = feedId;
	}

	public FeedItemSelectedEvent(FeedObject feedObject){
		this.feedObject = feedObject;
	}
	
}
