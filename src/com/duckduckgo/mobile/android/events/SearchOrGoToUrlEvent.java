package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.util.SESSIONTYPE;

public class SearchOrGoToUrlEvent extends Event {

	public String url;
	public SESSIONTYPE sessionType;
	
	public SearchOrGoToUrlEvent(String url) {
		this(url, SESSIONTYPE.SESSION_BROWSE);
	}
	
	public SearchOrGoToUrlEvent(String url, SESSIONTYPE sessionType){
		this.url = url;
		this.sessionType = sessionType;
	}
	
}