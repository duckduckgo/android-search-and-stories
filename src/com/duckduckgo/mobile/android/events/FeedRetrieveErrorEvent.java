package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.util.SCREEN;


public class FeedRetrieveErrorEvent extends Event {
	private DuckDuckGo activity;

	public FeedRetrieveErrorEvent(DuckDuckGo activity){
		this.activity = activity;
	}
	
	public void process() {
		if (activity.mDuckDuckGoContainer.currentScreen != SCREEN.SCR_SAVED_FEED) {
			new FeedRequestFailureDialogBuilder(activity).show();
		}
	};
}