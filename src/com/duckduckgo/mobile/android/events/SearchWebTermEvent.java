package com.duckduckgo.mobile.android.events;

public class SearchWebTermEvent extends Event {

	public String term;
	
	public SearchWebTermEvent(String term){
		this.term = term;
	}
	
}