package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.util.SCREEN;

public class AfterSwitchPostEvent extends Event {

	public SCREEN screenToDisplay;
	public Event postEvent;
	
	public AfterSwitchPostEvent(SCREEN screenToDisplay, Event postEvent){
		this.screenToDisplay = screenToDisplay;
		this.postEvent = postEvent;
	}
	
}