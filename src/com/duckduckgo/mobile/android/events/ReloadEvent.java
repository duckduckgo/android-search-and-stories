package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;


public class ReloadEvent extends Event {
	private DuckDuckGo context;

	public ReloadEvent(DuckDuckGo context){
		this.context = context;
	}
	
	public void process() {
		context.reloadAction();
	};
}