package com.duckduckgo.mobile.android.events;

import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class DeleteUrlInHistoryEvent extends DeleteEvent {
	private DuckDuckGo context;
	private String pageUrl;
	private String pageData;

	public DeleteUrlInHistoryEvent(DuckDuckGo context, String pageData, String pageUrl){
		this.context = context;
		this.pageData = pageData;
		this.pageUrl = pageUrl;
	}
	
	public void process() {
		final long delHistory = DDGApplication.getDB().deleteHistoryByDataUrl(pageData, pageUrl);				
		if(delHistory != 0) {							
			context.syncAdapters();
		}	
		Toast.makeText(context, R.string.ToastDeleteUrlInHistory, Toast.LENGTH_SHORT).show();
	};
}
