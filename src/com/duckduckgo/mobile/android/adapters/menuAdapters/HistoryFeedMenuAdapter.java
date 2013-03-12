package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.util.menuItems.SearchExternalMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveSearchMenuItem;

public class HistoryFeedMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;

	public HistoryFeedMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public HistoryFeedMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, String pageType) {
		this(context, resource, textViewResourceId);
		addItems();
	}
	
	public void addItems() {
		
	}
}