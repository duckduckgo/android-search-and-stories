package com.duckduckgo.mobile.android.adapters.menuAdapters;

import android.content.Intent;
import android.net.Uri;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.menuItems.DeleteFeedInHistoryMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SaveFeedMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SearchExternalMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareFeedMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveFeedMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveSearchMenuItem;

public class HistoryFeedMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private HistoryObject historyObject;

	public HistoryFeedMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public HistoryFeedMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, HistoryObject historyObject) {
		this(context, resource, textViewResourceId);
		this.historyObject = historyObject;
		addItems();
	}
	
	public void addItems() {
		FeedObject feedObject = new FeedObject(historyObject.getFeedId());
		add(new ShareFeedMenuItem(context, historyObject.getData(), historyObject.getUrl()));
		add(new SaveFeedMenuItem(context, feedObject));
		add(new UnSaveFeedMenuItem(context, historyObject.getFeedId()));
		add(new DeleteFeedInHistoryMenuItem(context, historyObject.getFeedId()));
		add(new SendToExternalBrowserMenuItem(context, historyObject.getUrl()));
	}
}