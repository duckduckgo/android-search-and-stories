package com.duckduckgo.mobile.android.events.feedEvents;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class FeedItemSelectedEvent extends Event {
	
	public FeedObject feedObject;

	public FeedItemSelectedEvent(FeedObject feedObject){
		this.feedObject = feedObject;
	}
	
}
