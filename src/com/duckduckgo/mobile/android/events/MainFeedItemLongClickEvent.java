package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class MainFeedItemLongClickEvent extends FeedItemLongClickEvent {
	
	public FeedObject feedObject;

	public MainFeedItemLongClickEvent(FeedObject feedObject){
		super(feedObject);
	}
	
}
