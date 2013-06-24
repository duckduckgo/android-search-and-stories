package com.duckduckgo.mobile.android.events.shareEvents;

import android.content.Context;

import com.duckduckgo.mobile.android.util.Sharer;

public class ShareFeedEvent extends ShareEvent {
	private final String title;
	private final String url;
	private final Context context;
	
	public ShareFeedEvent(Context context, String title, String url){
		this.context = context;
		this.title = title;
		this.url = url;
	}

	public void process() {
		Sharer.shareStory(context, title, url);
	};
}
