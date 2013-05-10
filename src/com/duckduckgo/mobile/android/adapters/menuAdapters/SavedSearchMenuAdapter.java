package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.util.menuItems.SearchExternalMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveSearchMenuItem;

public class SavedSearchMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private String query;

	public SavedSearchMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public SavedSearchMenuAdapter(DuckDuckGo context, int resource,
                                  int textViewResourceId, String query) {
		this(context, resource, textViewResourceId);
		this.query = query;
		addItems();
	}
	
	public void addItems() {
		add(new ShareSearchMenuItem(context, query));
		add(new UnSaveSearchMenuItem(context, query));		
		add(new SearchExternalMenuItem(context, query));
	}
}
