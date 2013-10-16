package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.util.SCREEN;

public class UpdateVisibilityEvent extends Event {
	public SCREEN screen; 

	public UpdateVisibilityEvent(SCREEN screen){
		this.screen = screen;
	}
	
}