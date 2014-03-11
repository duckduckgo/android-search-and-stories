package com.duckduckgo.mobile.android.events.readabilityEvents;

public class TurnReadabilityOffEvent extends ReadabilityEvent {
	public String url;

	public TurnReadabilityOffEvent(String url){
		this.url = url;
	}
}
