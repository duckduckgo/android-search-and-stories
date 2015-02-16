package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SearchAdapter;
import com.duckduckgo.mobile.android.adapters.SeparatedListAdapter;
import com.duckduckgo.mobile.android.adapters._SearchAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.AutoCompleteResultClickEvent;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.SearchListView;
import com.squareup.otto.Subscribe;

public class SearchFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = "search_fragment";
    public static final String TAG_HOME_PAGE = "search_fragment_home_page";

    private ListView autoCompleteResultListView;

    private SearchListView searchListView;
    private _SearchAdapter searchAdapter;
    private SearchAdapter adapter;

//    private RecentSearchListView recentSearchListView;
    //private RecentResultCursorAdapter recentSearchAdapter;

//    private SavedSearchListView savedSearchListView;
    private SavedResultCursorAdapter savedSearchAdapter;
    private RecentResultCursorAdapter recentAdapter;

    private LinearLayout search_container;
    //private ViewTreeObserver viewTreeObserver = null;
    private View fragmentView = null;

    //private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        Log.e("aaa", "Search on create: "+getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        Log.e("aaa", "Search on destroy: "+getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("aaa", "Search on activity created: "+getId());

        search_container = (LinearLayout) fragmentView.findViewById(R.id.search_container);

        searchListView = (SearchListView) fragmentView.findViewById(R.id.search_list);
        savedSearchAdapter = new SavedResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
        searchAdapter = new _SearchAdapter(getActivity());
        searchAdapter.addSection("recents", DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        searchAdapter.addSection("favorites", savedSearchAdapter);
        //searchListView.setAdapter(searchAdapter);
        recentAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory());

        adapter = new SearchAdapter(getActivity());
        adapter.addSection("recents", DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        //adapter.addSection("recents", recentAdapter);
        adapter.addSection("favorites", savedSearchAdapter);


        SeparatedListAdapter adapter2 = new SeparatedListAdapter(getActivity());
        adapter2.addSection("recents", DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        //adapter2.addSection("recents", recentAdapter);
        adapter2.addSection("favorites", savedSearchAdapter);

        //searchListView.setAdapter(adapter);
        searchListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        searchListView.setLimit(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount());
        //searchListView.setLimit(recentAdapter.getCount());


        autoCompleteResultListView = (ListView) fragmentView.findViewById(R.id.autocomplete_list);
        autoCompleteResultListView.setDivider(null);
        if(DDGControlVar.isAutocompleteActive) {
            autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);
        } else {
            autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        }
        autoCompleteResultListView.setOnItemClickListener(this);
        autoCompleteResultListView.setOnItemLongClickListener(this);
        autoCompleteResultListView.setVisibility(View.GONE);
        //fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.VISIBLE);

        fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("aaa", "Search on resume: "+getId());
        setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_SEARCH_HOME_PAGE);
        syncAdapters();
        //showRecentAndSaved();

        //search_container.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("aaa", "Search on pause: "+getId());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            //search_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            //search_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("aaa", "Search on stop: "+getId());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("aaa", "search fragment on hidden changed, hidden: " + hidden);
        if(!hidden) {
            //showRecentAndSaved();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(DDGControlVar.isAutocompleteActive) {
            //aaa make an event to the activity;
            BusProvider.getInstance().post(new AutoCompleteResultClickEvent(position));//aaa todo change event!
        } else {
            //
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return false;
    }

    @Subscribe
    public void onShowAutoCompleteResultsEvent(ShowAutoCompleteResultsEvent event) {
        //showSearch();
        Log.e("aaa,", "+++SEARCH FRAGMENT, show autocomplete: "+event.isVisible);
        showAutoCompleteResults(event.isVisible);
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        syncAdapters();
    }

    @Override
    public void onGlobalLayout() {
        int totalHeight = search_container.getRootView().getHeight();
        int visibleHeight = search_container.getHeight();

        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        int statusBar = getStatusBarHeight();
        int navigationBar = getNavigationBarHeight();
        int actionBarHeight = (int) getActivity().getResources().getDimension(R.dimen.actionbar_height);
        //Log.e("aaa", "status bar: "+statusBar);
        //Log.e("aaa", "navigation bar: "+navigationBar);
        totalHeight = totalHeight - statusBar - navigationBar - actionBarHeight;


        int itemHeight = (int) getActivity().getResources().getDimension(R.dimen.temp_item_height);

//        LinearLayout.LayoutParams recentParams = (LinearLayout.LayoutParams) recentSearchListView.getLayoutParams();
//        LinearLayout.LayoutParams savedParams = (LinearLayout.LayoutParams) savedSearchListView.getLayoutParams();
        LinearLayout.LayoutParams searchParams = (LinearLayout.LayoutParams) searchListView.getLayoutParams();
//        int newRecentHeight = 0;
//        int newSavedHeight = 0;

//        visibleHeight = visibleHeight - recentParams.topMargin - recentParams.bottomMargin - savedParams.topMargin;

        //visibleHeight = visibleHeight - recentParams.topMargin - recentParams.bottomMargin - savedParams.topMargin;

        int maxItems = visibleHeight / itemHeight;
        int recentItems = 0;

        //if((totalHeight-visibleHeight) > (totalHeight/3)) {
        if(!portrait) {
            Log.e("aaa", "is portrait: "+portrait);

            Log.e("aaa", "max items: "+maxItems);
            return;

            //int halfItems = maxItems / 2;
            //recentItems = halfItems <= recentAdapter.getCount() ? halfItems : recentAdapter.getCount();
            //int savedItems = maxItems - recentItems;

        } else if((totalHeight - visibleHeight) > (statusBar + navigationBar + actionBarHeight)) {
            Log.e("aaa", "open keyboard");
            Log.e("aaa", "max items: "+maxItems);
            //Log.e("aaa", "open");
            //Log.e("aaa", "total height: " + totalHeight);
            //Log.e("aaa", "visible height: " + visibleHeight);

            //recentItems = (maxItems - 1) <= DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount() ? (maxItems - 1) : DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount();
            //recentItems = (maxItems - 1) <= recentAdapter.getCount() ? (maxItems - 1) : recentAdapter.getCount();
            recentItems = 1;
            int savedItems = maxItems - recentItems;

//            newRecentHeight = recentItems * itemHeight;
//            newSavedHeight = LinearLayout.LayoutParams.MATCH_PARENT;

        } else {
            Log.e("aaa", "close keyboard");
            Log.e("aaa", "max items: "+maxItems);
            //Log.e("aaa", "close");
            //Log.e("aaa", "total height: " + totalHeight);
            //Log.e("aaa", "visible height: " + visibleHeight);
            int halfItems = maxItems / 2;
            //recentItems = halfItems <= recentAdapter.getCount() ? halfItems : recentAdapter.getCount();
            recentItems = maxItems;
            int savedItems = maxItems - recentItems;

//            newRecentHeight = recentItems * itemHeight;
//            newSavedHeight = LinearLayout.LayoutParams.MATCH_PARENT;

            //Log.e("aaa", "visible height: "+visibleHeight);
            //Log.e("aaa", "recent height: "+newRecentHeight);
            //Log.e("aaa", "saved height: "+newSavedHeight);
        }
//        if(recentParams.height!=newRecentHeight) {
//            recentParams.height = newRecentHeight;//
//            recentSearchListView.setLayoutParams(recentParams);
//        }
//        if(savedParams.height!=newSavedHeight) {
//            savedParams.height = newSavedHeight;
//            savedSearchListView.setLayoutParams(savedParams);
//        }

        Log.e("aaa", "recent items: "+recentItems);
        //boolean shouldUpdateAdapters = recentItems!=recentAdapter.getCount();
        boolean shouldUpdateAdapters = recentItems!=DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount();
        Log.e("aaa", "should update adapters: "+shouldUpdateAdapters);
        if(shouldUpdateAdapters) {
            syncRecents(recentItems);
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

    private void syncRecents(int limit) {
        searchListView.setLimit(limit);
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory(limit));
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        recentAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory(limit));
        recentAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
        //searchAdapter.
        searchAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();


    }

    private void syncAdapters() {
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();/*
        toggleDivider(savedSearchAdapter.getCount()!=0 && (
                recentSearchListView.getVisibility()==View.VISIBLE && DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount()!=0
                ));*/
        searchAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    public void showAutoCompleteResults(boolean visible) {
        autoCompleteResultListView.setVisibility(visible ? View.VISIBLE : View.GONE);
        searchListView.setVisibility(visible ? View.GONE : View.VISIBLE);
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

    private void showDivider() {
        toggleDivider(true);
    }

    private void hideDivider() {
        toggleDivider(false);
    }

    private void toggleDivider(boolean visible) {
        if(visible) {
            fragmentView.findViewById(R.id.search_divider).setVisibility(View.VISIBLE);
        } else {
            fragmentView.findViewById(R.id.search_divider).setVisibility(View.GONE);
        }
    }


}
