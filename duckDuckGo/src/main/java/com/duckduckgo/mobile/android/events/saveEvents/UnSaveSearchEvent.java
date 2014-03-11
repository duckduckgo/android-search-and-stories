package com.duckduckgo.mobile.android.events.saveEvents;

public class UnSaveSearchEvent extends SaveEvent {
	public String query;

	public UnSaveSearchEvent(String query){
		this.query = query;
	}
	
}
