package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.RecentFeedCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.squareup.otto.Subscribe;

public class RecentFeedTabFragment extends ListFragment implements AdapterView.OnItemLongClickListener{

    public static final String TAG = "recent_feed_tab_fragment";

    MainFeedListView recentFeedListView;
    RecentFeedCursorAdapter recentFeedAdapter;

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
        fragmentView = inflater.inflate(R.layout.fragment_tab_recentfeed, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recentFeedListView = (MainFeedListView) getListView();
        recentFeedListView.setOnItemLongClickListener(this);
        recentFeedAdapter = new RecentFeedCursorAdapter(getActivity(), DDGApplication.getDB().getCursorStoryHistory());
        recentFeedListView.setAdapter(recentFeedAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.e("aaa", "on click");
        //recentFeedListView.onItemClick(l, v, position, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        Log.e("aaa", "on long click");
        return true;
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        recentFeedAdapter.changeCursor(DDGApplication.getDB().getCursorStoryHistory());
        recentFeedAdapter.notifyDataSetChanged();
    }
}
