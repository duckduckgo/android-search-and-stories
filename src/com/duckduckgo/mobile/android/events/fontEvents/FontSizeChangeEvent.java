package com.duckduckgo.mobile.android.events.fontEvents;

import com.duckduckgo.mobile.android.events.Event;

public class FontSizeChangeEvent extends Event {
	
	public int diff;
	public float diffPixel;

	public FontSizeChangeEvent(int diff, float diffPixel){
		this.diff = diff;
		this.diffPixel = diffPixel;
	}
	
}