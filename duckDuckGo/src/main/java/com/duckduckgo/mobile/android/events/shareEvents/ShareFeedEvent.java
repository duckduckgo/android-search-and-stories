package com.duckduckgo.mobile.android.events.shareEvents;

public class ShareFeedEvent extends ShareEvent {
	public final String title;
	public final String url;
	
	public ShareFeedEvent(String title, String url){
		this.title = title;
		this.url = url;
	}
}
