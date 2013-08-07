package com.duckduckgo.mobile.android.events.deleteEvents;


public class DeleteStoryInHistoryEvent extends DeleteEvent {
	public String feedObjectId;

	public DeleteStoryInHistoryEvent(String feedObjectId){
		this.feedObjectId = feedObjectId;
	}
}
