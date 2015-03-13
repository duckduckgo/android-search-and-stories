package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.RecentFeedCursorAdapter;
import com.duckduckgo.mobile.android.adapters.RecyclerRecentFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.views.DDGRecyclerView;
import com.duckduckgo.mobile.android.views.RecentFeedListView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class RecentFeedTabFragment extends Fragment/*ListFragment*/ /*implements AdapterView.OnItemLongClickListener*/ {

    public static final String TAG = "recent_feed_tab_fragment";

    RecentFeedListView recentFeedListView;
    RecentFeedCursorAdapter recentFeedAdapter;

    private RecyclerRecentFeedAdapter recyclerRecentFeedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DDGRecyclerView recentFeedRecyclerView;

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
        fragmentView = inflater.inflate(R.layout.fragment_tab_recentsfeed, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recentFeedRecyclerView = (DDGRecyclerView) fragmentView.findViewById(R.id.list);

        //recentFeedRecyclerView = (DDGRecyclerView) fragmentView.findViewById(R.id.list);
        recentFeedRecyclerView.setEmptyView(fragmentView.findViewById(R.id.empty));
        //recyclerRecentFeedCursorAdapter = new RecyclerRecentFeedCursorAdapter(getActivity(), DDGApplication.getDB().getCursorRecentFeed());
        recyclerRecentFeedAdapter = new RecyclerRecentFeedAdapter(getActivity(), DDGApplication.getDB().getAllRecentFeed());
        ArrayList<FeedObject> feeds = DDGApplication.getDB().getAllRecentFeed();
        int i = 1;
        for(FeedObject feedObject : feeds) {
            Log.e("aaa", i+" - feed: "+feedObject.toString());
            i++;
        }

        layoutManager = new LinearLayoutManager(getActivity());
        recentFeedRecyclerView.setLayoutManager(layoutManager);
        recentFeedRecyclerView.setAdapter(recyclerRecentFeedAdapter);

        checkIfRecordHistory();

    }

    @Override
    public void onResume() {
        super.onResume();
        checkIfRecordHistory();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("aaa", "on hidden change, recording: "+PreferencesManager.getRecordHistory());
        if(!hidden) {
            checkIfRecordHistory();
        }
    }
/*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.e("aaa", "on click");
        recentFeedListView.onItemClick(l, v, position, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        Log.e("aaa", "on long click");
        recentFeedListView.onItemLongClick(parent, v, position, id);
        return true;
    }
*/

    public void checkIfRecordHistory() {
        if(!PreferencesManager.getRecordHistory()) {

            //getListView().setVisibility(View.GONE);
            //recentFeedRecyclerView.setVisibility(View.GONE);

            //fragmentView.findViewById(R.id.empty).setVisibility(View.VISIBLE);
            //recyclerRecentFeedCursorAdapter.clear();

            TextView title = (TextView) fragmentView.findViewById(R.id.empty_title);
            title.setText(getResources().getString(R.string.disabled_recents_title));

            TextView text = (TextView) fragmentView.findViewById(R.id.empty_text);
            text.setText(getResources().getString(R.string.disabled_recents_text));
        } else {
            recentFeedRecyclerView.setVisibility(View.VISIBLE);
            fragmentView.findViewById(R.id.empty).setVisibility(View.GONE);
        }

    }
    private void cancelCategoryFilter() {
        recyclerRecentFeedAdapter.resetFilterCategory();

    }

    @Subscribe
    public void onFeedCancelCategoryFilterEvent(FeedCancelCategoryFilterEvent event) {
        cancelCategoryFilter();
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        Log.e("aaa", "on sync adapters event, record history: "+PreferencesManager.getRecordHistory());

        //recentFeedAdapter.changeCursor(DDGApplication.getDB().getCursorRecentFeed());
        //recentFeedAdapter.notifyDataSetChanged();
        //if(PreferencesManager.getRecordHistory()) {
            recyclerRecentFeedAdapter.changeData(DDGApplication.getDB().getAllRecentFeed());
        //} else {
            //recyclerRecentFeedCursorAdapter.changeData(new ArrayList<FeedObject>());
        //}
        //checkIfRecordHistory();
    }
}
