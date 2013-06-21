package com.duckduckgo.mobile.android.events;

import android.content.Context;

import com.duckduckgo.mobile.android.util.Sharer;

public class ShareWebPageEvent extends ShareEvent {
	private final Context context;
	private String url;
	private String title;
	
	public ShareWebPageEvent(Context context, String title, String url){
		this.context = context;
		this.title = title;
		this.url = url;
	}

	public void process() {
		Sharer.shareWebPage(context, title, url);
	};
}
