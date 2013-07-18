package com.duckduckgo.mobile.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.HistoryCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.FontSizeChangeEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.squareup.otto.Subscribe;

public class RecentSearchFragment extends Fragment {
	
	private View contentView;
	
	private HistoryListView recentSearchView;
	private HistoryCursorAdapter recentSearchAdapter;
	
	private void initialise() {
		recentSearchAdapter = new HistoryCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory()); 
		recentSearchView = (HistoryListView) contentView.findViewById(R.id.recentSearchItems);
        recentSearchView.setDivider(null);
        recentSearchView.setAdapter(recentSearchAdapter);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        contentView = inflater.inflate(R.layout.recentsearch, container, false);
		initialise();
        return contentView;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		BusProvider.getInstance().register(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		BusProvider.getInstance().unregister(this);
	}
	
	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		recentSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
	}
	
	@Subscribe
	public void onFontSizeChange(FontSizeChangeEvent event) {
		DDGControlVar.recentTextSize = DDGControlVar.prevRecentTextSize + event.diffPixel;
		recentSearchAdapter.notifyDataSetInvalidated();
	}
	
	@Subscribe
	public void onFontSizeCancel(FontSizeCancelEvent event) {
		DDGControlVar.recentTextSize = DDGControlVar.prevRecentTextSize;
		recentSearchAdapter.notifyDataSetInvalidated();				
		DDGControlVar.prevRecentTextSize = 0;
	}
}
