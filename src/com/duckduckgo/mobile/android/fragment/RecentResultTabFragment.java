package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.squareup.otto.Subscribe;

public class RecentResultTabFragment extends ListFragment {

    public static final String TAG = "recent_result_tab_fragment";

    private RecentSearchListView recentSearchListView;
    private RecentResultCursorAdapter recentResultAdapter;

    private int lastFirstVisibleItem;

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
        fragmentView = inflater.inflate(R.layout.fragment_tab_recentsresult, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(PreferencesManager.getRecordHistory()) {
            recentSearchListView = (RecentSearchListView) getListView();
            recentResultAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory(), true);
            recentSearchListView.setAdapter(recentResultAdapter);

            recentSearchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    final int currentFirstVisibleItem = recentSearchListView.getFirstVisiblePosition();;
                    if (currentFirstVisibleItem > lastFirstVisibleItem) {
                        DDGActionBarManager.getInstance().tryToHideTab();
                    } else if (currentFirstVisibleItem < lastFirstVisibleItem) {
                        DDGActionBarManager.getInstance().tryToShowTab();
                    }
                    lastFirstVisibleItem = currentFirstVisibleItem;
                }
            });

        } else {
            getListView().setVisibility(View.GONE);

            TextView title = (TextView) fragmentView.findViewById(R.id.empty_title);
            title.setText(getResources().getString(R.string.disabled_recents_title));

            TextView text = (TextView) fragmentView.findViewById(R.id.empty_text);
            text.setText(getResources().getString(R.string.disabled_recents_text));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        DDGActionBarManager.getInstance().tryToShowTab();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        recentSearchListView.onItemClick(l, v, position, id);
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        if(recentResultAdapter!=null) {
            recentResultAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
            recentResultAdapter.notifyDataSetChanged();
        }

    }
}
