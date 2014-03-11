package com.duckduckgo.mobile.android.adapters.menuAdapters;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.menuItems.DeleteUrlInHistoryMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SaveSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.SendToExternalBrowserMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.ShareSearchMenuItem;
import com.duckduckgo.mobile.android.util.menuItems.UnSaveSearchMenuItem;

public class HistorySearchMenuAdapter extends PageMenuContextAdapter {
	private DuckDuckGo context;
	private HistoryObject historyObject;

	public HistorySearchMenuAdapter(DuckDuckGo context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}
	
	public HistorySearchMenuAdapter(DuckDuckGo context, int resource, 
			int textViewResourceId, HistoryObject historyObject) {
		this(context, resource, textViewResourceId);
		this.historyObject = historyObject;
		addItems();
	}
	
	public void addItems() {
		add(new ShareSearchMenuItem(context, historyObject.getData()));
		add(new SendToExternalBrowserMenuItem(context, historyObject.getUrl()));
		add(new DeleteUrlInHistoryMenuItem(context, historyObject.getData(), historyObject.getUrl()));
		if(DDGApplication.getDB().isSavedSearch(historyObject.getData())){
			add(new UnSaveSearchMenuItem(context, historyObject.getData()));
		}else{
			add(new SaveSearchMenuItem(context, historyObject.getData()));
		}
	}
}