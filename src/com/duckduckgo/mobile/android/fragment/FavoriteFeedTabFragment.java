package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.FavoriteFeedCursorAdapter;
import com.duckduckgo.mobile.android.adapters.RecyclerFavoriteFeedAdapter;
import com.duckduckgo.mobile.android.adapters.RecyclerRecentFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.DDGRecyclerView;
import com.duckduckgo.mobile.android.views.FavoriteFeedListView;
import com.squareup.otto.Subscribe;


public class FavoriteFeedTabFragment extends Fragment {
	public static final String TAG = "saved_feed_tab_fragment";
	FavoriteFeedListView favoriteFeedListView;
	FavoriteFeedCursorAdapter favoriteFeedAdapter;

    private RecyclerFavoriteFeedAdapter recyclerFavoriteFeedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DDGRecyclerView favoriteFeedRecyclerView;

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
        fragmentView = inflater.inflate(R.layout.fragment_tab_favoritefeed, container, false);
        return fragmentView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        favoriteFeedRecyclerView = (DDGRecyclerView) fragmentView.findViewById(R.id.list);
        favoriteFeedRecyclerView.setEmptyView(fragmentView.findViewById(R.id.empty));

        layoutManager = new LinearLayoutManager(getActivity());
        favoriteFeedRecyclerView.setLayoutManager(layoutManager);

        recyclerFavoriteFeedAdapter = new RecyclerFavoriteFeedAdapter(getActivity(), DDGApplication.getDB().getAllFavoriteFeed());
        favoriteFeedRecyclerView.setAdapter(recyclerFavoriteFeedAdapter);

        favoriteFeedRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("aaa", "is visible to user");
        DDGActionBarManager.getInstance().tryToShowTab();
    }

    private void cancelCategoryFilter() {
        DDGControlVar.targetCategory = null;
        recyclerFavoriteFeedAdapter.changeData(DDGApplication.getDB().getAllFavoriteFeed());

    }

    private void cancelSourceFilter() {
        DDGControlVar.targetSource = null;
        recyclerFavoriteFeedAdapter.changeData(DDGApplication.getDB().getAllFavoriteFeed());
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
	public void onSyncAdapters(SyncAdaptersEvent event) {
        recyclerFavoriteFeedAdapter.changeData(DDGApplication.getDB().getAllFavoriteFeed());
	}
}