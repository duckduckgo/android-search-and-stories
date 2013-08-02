package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.util.SCREEN;

public class SwitchScreenEvent extends Event {

	public SCREEN screen;
	
	public SwitchScreenEvent(SCREEN screen){
		this.screen = screen;
	}
	
}