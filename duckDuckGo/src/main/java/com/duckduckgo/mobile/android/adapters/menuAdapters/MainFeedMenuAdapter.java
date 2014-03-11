package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.menuItems.SaveStoryMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareFeedMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveStoryMenuItem;

public class MainFeedMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private FeedObject feedObject;

	public MainFeedMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public MainFeedMenuAdapter(DuckDuckGo context, int resource,
                               int textViewResourceId, FeedObject feedObject) {
		this(context, resource, textViewResourceId);
		this.feedObject = feedObject;
		addItems();
	}
	
	public void addItems() {
		add(new ShareFeedMenuItem(context, feedObject.getTitle(), feedObject.getUrl()));
		add(new SendToExternalBrowserMenuItem(context, feedObject.getUrl()));		
		if(feedObject.isSaved()){
			add(new UnSaveStoryMenuItem(context, feedObject.getId()));
		}else{
			add(new SaveStoryMenuItem(context, feedObject));
		}
	}
}
