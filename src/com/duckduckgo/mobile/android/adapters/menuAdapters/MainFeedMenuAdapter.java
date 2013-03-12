package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.menuItems.SaveMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveMenuItem;

public class MainFeedMenuAdapter extends PageMenuContextAdapter {
	DuckDuckGo context;
	FeedObject feedObject;

	public MainFeedMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public MainFeedMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, String pageType, FeedObject feedObject) {
		this(context, resource, textViewResourceId);
		this.feedObject = feedObject;
		addItems();
	}
	
	public void addItems() {
		add(new ShareMenuItem(context, feedObject.getTitle(), feedObject.getUrl()));
		add(new SendToExternalBrowserMenuItem(context, feedObject.getUrl()));		
		if(feedObject.isSaved()){
			add(new UnSaveMenuItem(context, feedObject.getId()));
		}else{
			add(new SaveMenuItem(context, feedObject));
		}
	}
}
