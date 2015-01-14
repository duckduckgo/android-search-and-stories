package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

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

    private LinearLayout search_container;
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

        search_container = (LinearLayout) fragmentView.findViewById(R.id.search_container);

        recentSearchListView = (RecentSearchListView) fragmentView.findViewById(R.id.recentList);
        recentSearchListView.setDivider(null);
        recentSearchAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory());
        recentSearchListView.setAdapter(recentSearchAdapter);

        setMaxItemVisible(recentSearchListView);

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

    public void setMaxItemVisible(final ListView listView) {
        Log.e("aaa", "--------------------inside calculate visible item");
        final View container = search_container;
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                //Log.e("aaa", "total height: "+container.getRootView().getHeight());
                //Log.e("aaa", "visible height: "+container.getHeight());
                int totalHeight = container.getRootView().getHeight();
                int visibleHeight = container.getHeight();
                if(2*visibleHeight<totalHeight) {
                    //Log.e("aaa", "open");

                    View item = listView.getAdapter().getView(0, null, listView);
                    item.measure(0, 0);
                    int itemHeight = (item.getMeasuredHeight() + listView.getDividerHeight());
                    //Log.e("aaa", "item height: " + itemHeight);

                    int maxItems = (int) (visibleHeight / itemHeight);
                    maxItems = maxItems - 1;
                    //Log.e("aaa", "max items: " + maxItems);

                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    //params.height = itemHeight * maxItems;

                    int oldHeight = params.height;
                    int newHeight = (itemHeight * maxItems);

                    //Log.e("aaa", "total list height: " + params.height);
                    //Log.e("aaa", "new list height: " + (itemHeight * maxItems));

                    params.height = (itemHeight * maxItems);
                    listView.setLayoutParams(params);
                    listView.requestLayout();

                } else {
                    //Log.e("aaa", "close");

                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.height = -2;
                    listView.setLayoutParams(params);
                    listView.requestLayout();

                }

            }
        });

    }


}
