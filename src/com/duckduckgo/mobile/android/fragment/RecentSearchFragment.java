package com.duckduckgo.mobile.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.squareup.otto.Subscribe;

public class RecentSearchFragment extends Fragment {
	
	private View contentView;
	
	private HistoryListView recentSearchView;
	private MultiHistoryAdapter historyAdapter;
	
	private void initialise() {
		historyAdapter = new MultiHistoryAdapter(getActivity());		
		recentSearchView = (HistoryListView) contentView.findViewById(R.id.recentSearchItems);
        recentSearchView.setDivider(null);
        recentSearchView.setAdapter(historyAdapter.getRecentSearchAdapter());
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
		historyAdapter.sync();
	}
}
