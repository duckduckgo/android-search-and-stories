package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.menuItems.DeleteStoryInHistoryMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SaveStoryMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareFeedMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveStoryMenuItem;

public class HistoryStoryMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private HistoryObject historyObject;

	public HistoryStoryMenuAdapter(DuckDuckGo context, int resource,
                                   int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public HistoryStoryMenuAdapter(DuckDuckGo context, int resource,
                                   int textViewResourceId, HistoryObject historyObject) {
		this(context, resource, textViewResourceId);
		this.historyObject = historyObject;
		addItems();
	}
	
	public void addItems() {
		FeedObject feedObject = new FeedObject(historyObject.getFeedId());
		add(new ShareFeedMenuItem(context, historyObject.getData(), historyObject.getUrl()));
		if (feedObject.isSaved()) {
			add(new UnSaveStoryMenuItem(context, historyObject.getFeedId()));
		}
		else {
			add(new SaveStoryMenuItem(context, feedObject));
		}
		add(new DeleteStoryInHistoryMenuItem(context, historyObject.getFeedId()));
		add(new SendToExternalBrowserMenuItem(context, historyObject.getUrl()));
	}
}