package com.duckduckgo.mobile.android.events.readabilityEvents;

import java.util.List;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class ReadabilityFeedRetrieveSuccessEvent extends Event {
	public List<FeedObject> feed;

	public ReadabilityFeedRetrieveSuccessEvent(List<FeedObject> feed){
		this.feed = feed;
	}
	
}