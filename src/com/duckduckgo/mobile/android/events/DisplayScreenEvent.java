package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.util.SCREEN;

public class DisplayScreenEvent extends Event {
	
	public SCREEN screenToDisplay;

	public DisplayScreenEvent(SCREEN screenToDisplay){
		this.screenToDisplay = screenToDisplay;
	}
	
}