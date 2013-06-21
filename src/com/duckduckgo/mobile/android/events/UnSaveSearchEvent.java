package com.duckduckgo.mobile.android.events;

import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class UnSaveSearchEvent extends SaveEvent {
	private DuckDuckGo context;
	private String query;

	public UnSaveSearchEvent(DuckDuckGo context, String query){
		this.context = context;
		this.query = query;
	}
	
	public void process() {
		final long delHistory = DDGApplication.getDB().deleteSavedSearch(query);
		if(delHistory != 0) {							
			context.syncAdapters();
		}	
		Toast.makeText(context, R.string.ToastUnSaveSearch, Toast.LENGTH_SHORT).show();
	};
}
