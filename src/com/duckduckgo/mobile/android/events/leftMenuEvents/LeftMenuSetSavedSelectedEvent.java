package com.duckduckgo.mobile.android.events.leftMenuEvents;

import com.duckduckgo.mobile.android.events.Event;

public class LeftMenuSetSavedSelectedEvent extends Event {

	public boolean selected;

	public LeftMenuSetSavedSelectedEvent(boolean selected) {
		this.selected = selected;
	}
}
