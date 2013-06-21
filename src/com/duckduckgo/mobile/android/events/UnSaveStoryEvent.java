package com.duckduckgo.mobile.android.events;

import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class UnSaveStoryEvent extends SaveEvent {
	private DuckDuckGo context;
	private String feedObjectId;

	public UnSaveStoryEvent(DuckDuckGo context, String feedObjectId){
		this.context = context;
		this.feedObjectId = feedObjectId;		
	}
	
	public void process() {
		final long delResult = DDGApplication.getDB().makeItemHidden(feedObjectId);
		if(delResult != 0) {							
			context.syncAdapters();
		}
		Toast.makeText(context, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
	};
}
