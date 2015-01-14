package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.SavedSearchListView;
import com.squareup.otto.Subscribe;

public class SearchFragment extends Fragment {

    public static final String TAG = "search_fragment";

    private RecentSearchListView recentSearchListView;
    private RecentResultCursorAdapter recentSearchAdapter;

    private SavedSearchListView savedSearchListView;
    private SavedResultCursorAdapter savedSearchAdapter;

    private View fragmentView = null;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recentSearchListView = (RecentSearchListView) fragmentView.findViewById(R.id.recentList);
        recentSearchListView.setDivider(null);
        recentSearchAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory());
        recentSearchListView.setAdapter(recentSearchAdapter);

        savedSearchListView = (SavedSearchListView) fragmentView.findViewById(R.id.savedList);
        savedSearchListView.setDivider(null);
        savedSearchAdapter = new SavedResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
        savedSearchListView.setAdapter(savedSearchAdapter);
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        recentSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        recentSearchAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
    }


}
