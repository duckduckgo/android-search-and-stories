package com.duckduckgo.mobile.android.events;

import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class SaveSearchEvent extends SaveEvent {
	
	private DuckDuckGo context;
	private String pageData;

	public SaveSearchEvent(DuckDuckGo context, String pageData){
		this.context = context;
		this.pageData = pageData;		
	}
	
	public void process() {
		context.itemSaveSearch(pageData);
		context.syncAdapters();
		Toast.makeText(context, R.string.ToastSaveSearch, Toast.LENGTH_SHORT).show();
	}
}
