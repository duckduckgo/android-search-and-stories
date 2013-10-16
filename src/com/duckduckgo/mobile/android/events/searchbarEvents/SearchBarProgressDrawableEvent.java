package com.duckduckgo.mobile.android.events.searchbarEvents;

import com.duckduckgo.mobile.android.events.Event;

public class SearchBarProgressDrawableEvent extends Event {
	
	public int level;
	
	public SearchBarProgressDrawableEvent(int level){
		this.level = level;
	}
	
}