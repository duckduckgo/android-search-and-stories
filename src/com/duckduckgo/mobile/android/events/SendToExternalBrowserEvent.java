package com.duckduckgo.mobile.android.events;

import android.content.Intent;
import android.net.Uri;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class SendToExternalBrowserEvent extends ExternalEvent {
	private DuckDuckGo context;
	private String url;

	public SendToExternalBrowserEvent(DuckDuckGo context, String url){
		this.context = context;
		this.url = url;
	}
	
	public void process() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	context.startActivity(browserIntent);
	};
	
}
