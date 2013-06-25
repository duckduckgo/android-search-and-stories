package com.duckduckgo.mobile.android.events.saveEvents;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class SaveStoryEvent extends SaveEvent {
	
	public FeedObject feedObject;

	public SaveStoryEvent(FeedObject feedObject){
		this.feedObject = feedObject;
	}
	
}
