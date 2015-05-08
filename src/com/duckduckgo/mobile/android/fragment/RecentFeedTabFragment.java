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
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.RecentFeedCursorAdapter;
import com.duckduckgo.mobile.android.adapters.RecyclerRecentFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.views.DDGRecyclerView;
import com.duckduckgo.mobile.android.views.RecentFeedListView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class RecentFeedTabFragment extends Fragment/*ListFragment*/ /*implements AdapterView.OnItemLongClickListener*/ {

    public static final String TAG = "recent_feed_tab_fragment";

    private RecyclerRecentFeedAdapter recyclerRecentFeedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DDGRecyclerView recentFeedRecyclerView;

    private boolean recyclerScrollPerformed = false;

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

        recentFeedRecyclerView.setEmptyView(fragmentView.findViewById(R.id.empty));
        recyclerRecentFeedAdapter = new RecyclerRecentFeedAdapter(getActivity(), DDGApplication.getDB().getAllRecentFeed());

        layoutManager = new LinearLayoutManager(getActivity());
        recentFeedRecyclerView.setLayoutManager(layoutManager);
        recentFeedRecyclerView.setAdapter(recyclerRecentFeedAdapter);

        recentFeedRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 10) {
                    DDGActionBarManager.getInstance().tryToHideTab();
                } else if(dy < -10) {
                    DDGActionBarManager.getInstance().tryToShowTab();
                }
                recyclerScrollPerformed = true;
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE) {
                    if(recyclerScrollPerformed) {
                        recyclerScrollPerformed = false;
                    } else {
                        DDGActionBarManager.getInstance().tryToShowTab();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkIfRecordHistory();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        DDGActionBarManager.getInstance().tryToShowTab();
    }

    public void checkIfRecordHistory() {
        if(!PreferencesManager.getRecordHistory()) {

            TextView title = (TextView) fragmentView.findViewById(R.id.empty_title);
            title.setText(getResources().getString(R.string.disabled_recents_title));

            TextView text = (TextView) fragmentView.findViewById(R.id.empty_text);
            text.setText(getResources().getString(R.string.disabled_recents_text));
        }

    }

    private void cancelCategoryFilter() {
        DDGControlVar.targetCategory = null;
        recyclerRecentFeedAdapter.changeData(DDGApplication.getDB().getAllRecentFeed());

    }

    private void cancelSourceFilter() {
        DDGControlVar.targetSource = null;
        recyclerRecentFeedAdapter.changeData(DDGApplication.getDB().getAllRecentFeed());
    }

    @Subscribe
    public void onFeedCancelCategoryFilterEvent(FeedCancelCategoryFilterEvent event) {
        cancelCategoryFilter();
    }

    @Subscribe
    public void onFeedCancelSourceFilterEvent(FeedCancelSourceFilterEvent event) {
        cancelSourceFilter();
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        if(recyclerRecentFeedAdapter!=null) {
            recyclerRecentFeedAdapter.changeData(DDGApplication.getDB().getAllRecentFeed());
        }
    }
}
