package com.duckduckgo.mobile.android.adapters;

import android.content.Context;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;

public class MultiHistoryAdapter extends SeparatedListAdapter {
	
	HistoryCursorAdapter recentSearchAdapter;
	HistoryCursorAdapter recentStoryAdapter;

	public MultiHistoryAdapter(Context context) {
		super(context, R.layout.history_section_header);	
		recentSearchAdapter = new HistoryCursorAdapter(context, DDGApplication.getDB().getCursorSearchHistory());    		
		recentStoryAdapter = new HistoryCursorAdapter(context, DDGApplication.getDB().getCursorStoryHistory());

		addSection(context.getString(R.string.LeftRecentSearches), recentSearchAdapter);
		addSection(context.getString(R.string.LeftRecentStories), recentStoryAdapter);
	}
	
	public void sync() {
		recentStoryAdapter.changeCursor(DDGApplication.getDB().getCursorStoryHistory());
    	recentSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
		notifyDataSetChanged();
	}
	
	public HistoryCursorAdapter getRecentSearchAdapter() {
		return recentSearchAdapter;
	}
	
	public HistoryCursorAdapter getRecentStoryAdapter() {
		return recentSearchAdapter;
	}
}
