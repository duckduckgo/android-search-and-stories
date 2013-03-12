package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveMenuItem;

public class SavedFeedMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private FeedObject feedObject;

	public SavedFeedMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public SavedFeedMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, String pageType, FeedObject feedObject) {
		this(context, resource, textViewResourceId);
		this.feedObject = feedObject;
		addItems();
	}
	
	public void addItems() {
		add(new ShareMenuItem(context, feedObject.getTitle(), feedObject.getUrl()));
		add(new SendToExternalBrowserMenuItem(context, feedObject.getUrl()));		
		add(new UnSaveMenuItem(context, feedObject.getId()));
	}
}
