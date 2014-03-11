package com.duckduckgo.mobile.android.events.saveEvents;

public class SaveSearchEvent extends SaveEvent {
	
	public String pageData;

	public SaveSearchEvent(String pageData){
		this.pageData = pageData;		
	}
}
