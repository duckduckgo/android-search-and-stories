package com.duckduckgo.mobile.android.adapters.menuAdapters;

import android.content.Context;

import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.util.menuItems.SearchExternalMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveSearchMenuItem;

public class SavedSearchMenuAdapter extends PageMenuContextAdapter {
	private Context context;
	private String query;

	public SavedSearchMenuAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public SavedSearchMenuAdapter(Context context, int resource,
                                  int textViewResourceId, String query) {
		this(context, resource, textViewResourceId);
		this.query = query;
		addItems();
	}
	
	public void addItems() {
        add(new UnSaveSearchMenuItem(context, query));
		add(new ShareSearchMenuItem(context, query));
		add(new SearchExternalMenuItem(context, query));
	}
}
