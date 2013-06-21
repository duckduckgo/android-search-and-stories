package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class SearchExternalEvent extends ExternalEvent {
	private final String query;
	private final DuckDuckGo context;
	
	public SearchExternalEvent(DuckDuckGo context, String query){
		this.context = context;
		this.query = query;
	}

	public void process() {
		context.searchExternal(query);
	};
}
