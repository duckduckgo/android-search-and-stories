package com.duckduckgo.mobile.android.events;

public class SearchBarProgressDrawableEvent extends Event {
	
	public int level;
	
	public SearchBarProgressDrawableEvent(int level){
		this.level = level;
	}
	
}