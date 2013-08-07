package com.duckduckgo.mobile.android.events.externalEvents;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class SendToExternalBrowserEvent extends ExternalEvent {
	public String url;

	public SendToExternalBrowserEvent(DuckDuckGo context, String url){
		this.url = url;
	}
	
}
