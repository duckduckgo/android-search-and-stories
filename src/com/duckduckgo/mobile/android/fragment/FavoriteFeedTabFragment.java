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


public class FavoriteFeedTabFragment extends /*ListFragment*/Fragment {
	public static final String TAG = "saved_feed_tab_fragment";
	FavoriteFeedListView favoriteFeedListView;
	FavoriteFeedCursorAdapter favoriteFeedAdapter;

    private RecyclerFavoriteFeedAdapter recyclerFavoriteFeedAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DDGRecyclerView favoriteFeedRecyclerView;

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


    /** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 *//*
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.fragment_tab_favoritefeed, container, false);
		//setRetainInstance(true);
		BusProvider.getInstance().register(this);
		return fragmentLayout;
	}*/
/*
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getInstance().unregister(this);
	}
*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*
		favoriteFeedListView = (FavoriteFeedListView) getListView();
		favoriteFeedAdapter = new FavoriteFeedCursorAdapter(getActivity(), getActivity(), DDGApplication.getDB().getCursorStoryFeed());
		favoriteFeedListView.setAdapter(favoriteFeedAdapter);*/

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
            }
        });

	}
/*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("aaa", "new config");
    }
*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("aaa", "is visible to user");
        DDGActionBarManager.getInstance().tryToShowTab();
    }

    private void cancelCategoryFilter() {
        //recyclerFavoriteFeedAdapter.resetFilterCategory();
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
/*
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        favoriteFeedListView.onItemClick(l, v, position, id);
	}
*/
	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		//favoriteFeedAdapter.changeCursor(DDGApplication.getDB().getCursorStoryFeed());
		//favoriteFeedAdapter.notifyDataSetChanged();
        recyclerFavoriteFeedAdapter.changeData(DDGApplication.getDB().getAllFavoriteFeed());
	}
}