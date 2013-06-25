package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class FeedItemLongClickEvent extends Event {
	
	public FeedObject feedObject;

	public FeedItemLongClickEvent(FeedObject feedObject){
		this.feedObject = feedObject;
	}
	
}
