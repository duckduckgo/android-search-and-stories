package com.duckduckgo.mobile.android.events.saveEvents;

public class SaveSearchEvent extends SaveEvent {

	public String pageTitle = null;
	public String pageData = null;

	public SaveSearchEvent(String pageData){
		this.pageData = pageData;		
	}

	public SaveSearchEvent(String pageTitle, String pageData) {
		this.pageTitle = pageTitle;
		this.pageData = pageData;
	}
}
