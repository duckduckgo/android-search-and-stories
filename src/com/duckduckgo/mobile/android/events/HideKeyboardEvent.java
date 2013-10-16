package com.duckduckgo.mobile.android.events;

public class HideKeyboardEvent extends Event {
	private int delay;
	
	public HideKeyboardEvent(){
	}
	
	public HideKeyboardEvent(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return delay;
	}
}