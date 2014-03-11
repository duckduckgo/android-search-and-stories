package com.duckduckgo.mobile.android.events.deleteEvents;


public class DeleteUrlInHistoryEvent extends DeleteEvent {
	public String pageUrl;
	public String pageData;

	public DeleteUrlInHistoryEvent(String pageData, String pageUrl){
		this.pageData = pageData;
		this.pageUrl = pageUrl;
	}
}
