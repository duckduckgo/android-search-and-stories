package com.duckduckgo.mobile.android.events.readabilityEvents;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class TurnReadabilityOnEvent extends ReadabilityEvent {
	private DuckDuckGo context;
	private FeedObject feedObject;

	public TurnReadabilityOnEvent(DuckDuckGo context, FeedObject feedObject){
		this.context = context;
		this.feedObject = feedObject;
	}
	
	public void process() {
		context.launchReadableFeedTask(feedObject);
	};
}
