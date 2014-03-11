package com.duckduckgo.mobile.android.events.savedSearchEvents;

import com.duckduckgo.mobile.android.events.Event;

public class SavedSearchItemEvent extends Event {
	
	public String query;

	public SavedSearchItemEvent(String query){
		this.query = query;
	}
	
}
