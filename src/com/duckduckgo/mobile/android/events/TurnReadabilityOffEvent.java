package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class TurnReadabilityOffEvent extends ReadabilityEvent {
	private DuckDuckGo context;
	private String url;

	public TurnReadabilityOffEvent(DuckDuckGo context, String url){
		this.context = context;
		this.url = url;
	}
	
	public void process() {
		context.mainWebView.forceOriginal();
		context.showWebUrl(url);
	};
}
