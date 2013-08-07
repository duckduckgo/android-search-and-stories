package com.duckduckgo.mobile.android.events.saveEvents;

public class UnSaveStoryEvent extends SaveEvent {
	public String feedObjectId;

	public UnSaveStoryEvent(String feedObjectId){
		this.feedObjectId = feedObjectId;		
	}
}
