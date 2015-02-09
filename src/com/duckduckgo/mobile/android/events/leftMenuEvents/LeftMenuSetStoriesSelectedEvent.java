package com.duckduckgo.mobile.android.events.leftMenuEvents;

import com.duckduckgo.mobile.android.events.Event;

public class LeftMenuSetStoriesSelectedEvent extends Event {

	public boolean selected;

	public LeftMenuSetStoriesSelectedEvent(boolean selected) {
		this.selected = selected;
	}
}
