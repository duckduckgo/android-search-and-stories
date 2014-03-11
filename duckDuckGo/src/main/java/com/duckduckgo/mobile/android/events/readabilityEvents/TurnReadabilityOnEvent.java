package com.duckduckgo.mobile.android.events.readabilityEvents;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class TurnReadabilityOnEvent extends ReadabilityEvent {
	public FeedObject feedObject;

	public TurnReadabilityOnEvent(FeedObject feedObject){
		this.feedObject = feedObject;
	}
}
