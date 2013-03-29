package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.util.menuItems.ReloadMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SaveSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SearchExternalMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveSearchMenuItem;

public class WebViewQueryMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private String query;
	private Boolean isQuerySaved;

	public WebViewQueryMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public WebViewQueryMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, String query, Boolean isQuerySaved) {
		this(context, resource, textViewResourceId);
		this.query = query;
		this.isQuerySaved = isQuerySaved;
		addMenuItems();
	}
	
	public void addMenuItems() {
		add(new ShareSearchMenuItem(context, query));
		add(new SearchExternalMenuItem(context, query));
		add(new ReloadMenuItem(context));
		
		if(isQuerySaved){
			add(new UnSaveSearchMenuItem(context, query));
		}
		else{
			add(new SaveSearchMenuItem(context, query));
		}
	}
}