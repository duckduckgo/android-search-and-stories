package com.duckduckgo.mobile.android.events.shareEvents;

import android.content.Context;

import com.duckduckgo.mobile.android.util.Sharer;

public class ShareSearchEvent extends ShareEvent {
	private final String query;
	private final Context context;
	
	public ShareSearchEvent(Context context, String query){
		this.context = context;
		this.query = query;
	}

	public void process() {
		Sharer.shareSearch(context, query);
	};
}
