package com.duckduckgo.mobile.android.adapters.menuAdapters;

import android.content.Intent;
import android.net.Uri;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Item.ItemType;
import com.duckduckgo.mobile.android.util.menuItems.ShareMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;

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
			addItemToUnsave();
		}else{
			addItemToSave();
		}
	}

	
	private void addItemToSave() {
		Item saveItem = getItem(ItemType.SAVE);
		saveItem.ActionToExecute = new Action() {
			@Override
			public void Execute() {
				final long delResult = DDGApplication.getDB().makeItemHidden(feedObject.getId());
				if(delResult != 0) {							
					context.syncAdapters();
				}					
			}
		};
		add(saveItem);
	}

	private void addItemToUnsave() {
		Item saveItem = getItem(ItemType.UNSAVE);
		saveItem.ActionToExecute = new Action() {
			@Override
			public void Execute() {
				context.itemSaveFeed(feedObject, null);
				context.syncAdapters();
			}
		};
		add(saveItem);
	}
}
