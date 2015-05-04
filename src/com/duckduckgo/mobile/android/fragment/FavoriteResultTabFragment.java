package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.FavoriteResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.views.FavoriteSearchListView;
import com.squareup.otto.Subscribe;

public class FavoriteResultTabFragment extends ListFragment {

	public static final String TAG = "saved_result_tab_fragment";
	private FavoriteSearchListView savedSearchView;
	private FavoriteResultCursorAdapter savedSearchAdapter;

    private int lastFirstVisibleItem;

    private View fragmentView = null;
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_tab_favoriteresult, container, false);
		//setRetainInstance(true);
		BusProvider.getInstance().register(this);
        return fragmentView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		savedSearchView = (FavoriteSearchListView) getListView();
		savedSearchAdapter = new FavoriteResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
		savedSearchView.setAdapter(savedSearchAdapter);

        savedSearchView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int currentFirstVisibleItem = savedSearchView.getFirstVisiblePosition();;
                if (currentFirstVisibleItem > lastFirstVisibleItem) {
                    DDGActionBarManager.getInstance().tryToHideTab();
                } else if (currentFirstVisibleItem < lastFirstVisibleItem) {
                    DDGActionBarManager.getInstance().tryToShowTab();
                }
                lastFirstVisibleItem = currentFirstVisibleItem;
            }
        });
	}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        DDGActionBarManager.getInstance().tryToShowTab();
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        savedSearchView.onItemClick(l, v, position, id);
	}

	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
		savedSearchAdapter.notifyDataSetChanged();
	}
}