package com.duckduckgo.mobile.android.events.externalEvents;

public class SendToExternalBrowserEvent extends ExternalEvent {
	public String url;

	public SendToExternalBrowserEvent(String url){
		this.url = url;
	}
	
}
