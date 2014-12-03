package com.duckduckgo.mobile.android.events.leftMenuEvents;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.util.SCREEN;

public class LeftMenuMarkSelectedEvent extends Event {

	public SCREEN current;

	public LeftMenuMarkSelectedEvent(SCREEN current) {
		this.current = current;
	}
}
