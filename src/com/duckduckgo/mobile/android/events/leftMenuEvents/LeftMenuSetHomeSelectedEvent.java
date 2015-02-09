package com.duckduckgo.mobile.android.events.leftMenuEvents;

import com.duckduckgo.mobile.android.events.Event;

public class LeftMenuSetHomeSelectedEvent extends Event {

	public boolean selected;

	public LeftMenuSetHomeSelectedEvent(boolean selected) {
		this.selected = selected;
	}
}
