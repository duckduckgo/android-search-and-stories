package com.duckduckgo.mobile.android.events;

public class SearchBarSetTextEvent extends Event {

	public String text;
	
	public SearchBarSetTextEvent(String text){
		this.text = text;
	}
	
}