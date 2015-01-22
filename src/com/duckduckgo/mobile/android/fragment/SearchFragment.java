package com.duckduckgo.mobile.android.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.TestEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.SavedSearchListView;
import com.squareup.otto.Subscribe;

public class SearchFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = "search_fragment";

    private ListView autoCompleteResultListView;

    private RecentSearchListView recentSearchListView;
    //private RecentResultCursorAdapter recentSearchAdapter;

    private SavedSearchListView savedSearchListView;
    private SavedResultCursorAdapter savedSearchAdapter;

    private LinearLayout search_container;
    private ViewTreeObserver viewTreeObserver = null;
    private int actionBarHeight = 0;
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
        Log.e("aaa", "on activity created");

        actionBarHeight = (int) getActivity().getResources().getDimension(R.dimen.actionbar_height);

        search_container = (LinearLayout) fragmentView.findViewById(R.id.search_container);

        recentSearchListView = (RecentSearchListView) fragmentView.findViewById(R.id.recent_list);
        recentSearchListView.setDivider(null);
        //recentSearchAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory());
        //recentSearchListView.setAdapter(recentSearchAdapter);
        recentSearchListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);

        //setMaxItemVisible(recentSearchListView);

        savedSearchListView = (SavedSearchListView) fragmentView.findViewById(R.id.saved_list);
        savedSearchListView.setDivider(null);
        savedSearchAdapter = new SavedResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
        savedSearchListView.setAdapter(savedSearchAdapter);

        autoCompleteResultListView = (ListView) fragmentView.findViewById(R.id.search_list);
        autoCompleteResultListView.setDivider(null);
        if(DDGControlVar.isAutocompleteActive) {
            autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);
        } else {
            autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        }
        autoCompleteResultListView.setOnItemClickListener(this);
        autoCompleteResultListView.setOnItemLongClickListener(this);
        autoCompleteResultListView.setVisibility(View.GONE);
        fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("aaa", "on resume");
        syncAdapters();
        showRecentAndSaved();

        if(recentSearchListView.getCount()!=0) {
            setMaxItemVisible(recentSearchListView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(viewTreeObserver!=null) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
                viewTreeObserver.removeOnGlobalLayoutListener(this);
            } else {
                viewTreeObserver.removeGlobalOnLayoutListener(this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(DDGControlVar.isAutocompleteActive) {
            //aaa make an event to the activity;
        } else {
            //
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("aaa", "Search fragment hidden: " + hidden);
    }

    @Subscribe
    public void onTestEvent(TestEvent event) {//aaa make an event
        showSearch();
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        Log.e("aaa", "on sync adapters");
        syncAdapters();
        //recentSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        //recentSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGlobalLayout() {
        int totalHeight = search_container.getRootView().getHeight();
        int visibleHeight = search_container.getHeight();

        int statusBar = getStatusBarHeight();
        int navigationBar = getNavigationBarHeight();
        Log.e("aaa", "status bar: "+statusBar);
        Log.e("aaa", "navigation bar: "+navigationBar);
        totalHeight = totalHeight - statusBar - navigationBar - actionBarHeight;


        int itemHeight = (int) getActivity().getResources().getDimension(R.dimen.temp_item_height);

        LinearLayout.LayoutParams recentParams = (LinearLayout.LayoutParams) recentSearchListView.getLayoutParams();
        LinearLayout.LayoutParams savedParams = (LinearLayout.LayoutParams) savedSearchListView.getLayoutParams();
        int newRecentHeight = 0;
        int newSavedHeight = 0;

        visibleHeight = visibleHeight - recentParams.topMargin - recentParams.bottomMargin - savedParams.topMargin;

        int maxItems = visibleHeight / itemHeight;

        //if((totalHeight-visibleHeight) > (totalHeight/3)) {
        if((totalHeight - visibleHeight) > (statusBar + navigationBar + actionBarHeight)) {
            Log.e("aaa", "open");
            Log.e("aaa", "total height: " + totalHeight);
            Log.e("aaa", "visible height: " + visibleHeight);

            int recentItems = (maxItems - 1) <= recentSearchListView.getCount() ? (maxItems - 1) : recentSearchListView.getCount();
            int savedItems = maxItems - recentItems;

            Log.e("aaa", "MAX items: "+maxItems);
            Log.e("aaa", "recent items: "+recentItems);
            Log.e("aaa", "saved items: "+savedItems);

            Log.e("aaa", "visible height: "+visibleHeight);
            Log.e("aaa", "recent height: "+recentItems*itemHeight);
            Log.e("aaa", "saved height: "+savedItems*itemHeight);

            newRecentHeight = recentItems * itemHeight;
            newSavedHeight = LinearLayout.LayoutParams.MATCH_PARENT;

        } else {
            Log.e("aaa", "close");
            Log.e("aaa", "total height: " + totalHeight);
            Log.e("aaa", "visible height: " + visibleHeight);
            int halfItems = maxItems / 2;
            int recentItems = halfItems <= recentSearchListView.getCount() ? halfItems : recentSearchListView.getCount();
            int savedItems = maxItems - recentItems;
            Log.e("aaa", "MAX items: "+maxItems);
            Log.e("aaa", "half items: "+halfItems);
            Log.e("aaa", "recent items: "+recentItems);
            Log.e("aaa", "saved items: "+savedItems);

            newRecentHeight = recentItems * itemHeight;
            newSavedHeight = LinearLayout.LayoutParams.MATCH_PARENT;

            Log.e("aaa", "visible height: "+visibleHeight);
            Log.e("aaa", "recent height: "+newRecentHeight);
            Log.e("aaa", "saved height: "+newSavedHeight);
        }
        if(recentParams.height!=newRecentHeight) {
            recentParams.height = newRecentHeight;
            recentSearchListView.setLayoutParams(recentParams);
        }
        if(savedParams.height!=newSavedHeight) {
            savedParams.height = newSavedHeight;
            savedSearchListView.setLayoutParams(savedParams);
        }

    }

    private int getStatusBarHeight() {
        Rect rect = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    private int getNavigationBarHeight() {
        int id = getActivity().getResources().getIdentifier(
                getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return getActivity().getResources().getDimensionPixelSize(id);
        }
        return 0;
    }

    public void setMaxItemVisible(final ListView listView) {

        final View container = search_container;
        viewTreeObserver = container.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(this);

    }

    private void syncAdapters() {
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
    }

    private void showSearch() {
        toggleSearchView(true);
    }

    private void showRecentAndSaved() {
        toggleSearchView(false);
    }

    private void toggleSearchView(boolean openSearch) {
        if(openSearch) {
            fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.GONE);
            autoCompleteResultListView.setVisibility(View.VISIBLE);
        } else {
            fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.VISIBLE);
            autoCompleteResultListView.setVisibility(View.GONE);
        }
    }


}
