package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.HistoryListView;

public class RecentSearchFragment extends Fragment {

	public static final String TAG = "recent_search_fragment";

	private HistoryListView recentSearchView = null;
/*
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
*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent_search, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	public void init() {
		recentSearchView = (HistoryListView) getView().findViewById(R.id.recentSearchItems);
		recentSearchView.setDivider(null);
		recentSearchView.setAdapter(DDGControlVar.mDuckDuckGoContainer.historyAdapter.getRecentSearchAdapter());
	}
}
