package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.menuItems.ReloadMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SaveStoryMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareFeedMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.TurnReadabilityOffMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.TurnReadabilityOnMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveStoryMenuItem;

public class WebViewStoryMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private String url;
	private Boolean isInReadabilityMode;
	private Boolean isItemSaved;
	private FeedObject feedObject;
	private Boolean hasReadability;

	public WebViewStoryMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public WebViewStoryMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, FeedObject feedObject, Boolean isInReadabilityMode) {
		this(context, resource, textViewResourceId);
		this.feedObject = feedObject;
		this.hasReadability = feedObject.hasPossibleReadability();
		this.url = feedObject.getUrl();
		this.isItemSaved = feedObject.isSaved();
		this.isInReadabilityMode = isInReadabilityMode;
		addMenuItems();
	}
	
	public void addMenuItems() {
		add(new ShareFeedMenuItem(context, feedObject.getTitle(), feedObject.getUrl()));
		add(new SendToExternalBrowserMenuItem(context, url));
		add(new ReloadMenuItem(context));
		if(isItemSaved){
			add(new UnSaveStoryMenuItem(context, feedObject.getId()));
		}
		else{
			add(new SaveStoryMenuItem(context, feedObject));
		}
		if(hasReadability) {
			if(!isInReadabilityMode)
				add(new TurnReadabilityOnMenuItem(context, feedObject));
			else{
				add(new TurnReadabilityOffMenuItem(context, feedObject.getUrl()));
			}
		}
	}
}