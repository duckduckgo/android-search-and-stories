package com.duckduckgo.mobile.android.events.searchBarEvents;

import com.duckduckgo.mobile.android.events.Event;

public class SearchBarSetProgressEvent extends Event {

	public int newProgress;

	public SearchBarSetProgressEvent(int newProgress) {
		this.newProgress = newProgress;
	}
}
