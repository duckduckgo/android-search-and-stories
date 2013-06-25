package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class FeedItemSelectedEvent extends Event {
	
	public FeedObject feedObject;

	public FeedItemSelectedEvent(FeedObject feedObject){
		this.feedObject = feedObject;
	}
	
}
