package com.duckduckgo.mobile.android.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.FavoriteResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SearchAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.AutoCompleteResultClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.duckduckgo.mobile.android.views.SearchListView;
import com.squareup.otto.Subscribe;

public class SearchFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = "search_fragment";
    public static final String TAG_HOME_PAGE = "search_fragment_home_page";

    private ListView autoCompleteResultListView;

    private SearchListView searchListView;
    private SearchAdapter adapter;
    //private SeparatedListAdapter adapter;

    private FavoriteResultCursorAdapter savedSearchAdapter;
    private RecentResultCursorAdapter recentAdapter;

    private int maxRecents = 1;

    View headerPadding;
    private LinearLayout search_container;
    private View fragmentView = null;

    private Menu searchMenu = null;
    private DDGOverflowMenu overflowMenu = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        //Log.e("aaa", "Search on create: "+getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        //Log.e("aaa", "Search on destroy: "+getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.e("aaa", "Search on activity created: "+getId());

        search_container = (LinearLayout) fragmentView.findViewById(R.id.search_container);

        searchListView = (SearchListView) fragmentView.findViewById(R.id.search_list);
        savedSearchAdapter = new FavoriteResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());

        recentAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory());

        adapter = new SearchAdapter(getActivity());
        //adapter = new SeparatedListAdapter(getActivity());
        //adapter.addSection("recents", DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        adapter.addSection("recents", recentAdapter);
        adapter.addSection("favorites", savedSearchAdapter);

        headerPadding = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.temp_fake_header, searchListView, false);
        //searchListView.addHeaderView(headerPadding);

        searchListView.setAdapter(adapter);

        autoCompleteResultListView = (ListView) fragmentView.findViewById(R.id.autocomplete_list);
        autoCompleteResultListView.setDivider(null);

        headerPadding = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.temp_fake_header, autoCompleteResultListView, false);
        //autoCompleteResultListView.addHeaderView(headerPadding);

        autoCompleteResultListView.setOnItemClickListener(this);
        autoCompleteResultListView.setOnItemLongClickListener(this);
        autoCompleteResultListView.setVisibility(View.GONE);
        //fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.VISIBLE);

        fragmentView.findViewById(R.id.recent_saved_container).setVisibility(View.GONE);

        searchMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.main, searchMenu);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("aaa", "Search on resume: "+getId()+getTag());
        //setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_SEARCH_HOME_PAGE && getTag().equals(TAG_HOME_PAGE));//aaa
        setHasOptionsMenu(false);
        syncAdapters();

        if(DDGControlVar.isAutocompleteActive) {
            autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);
        } else {
            autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
        }

        search_container.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.e("aaa", "Search on pause: "+getId());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            search_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            search_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.e("aaa", "Search on stop: "+getId());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //Log.e("aaa", "search fragment on hidden changed, hidden: " + hidden);
        if(!hidden) {
            showAutoCompleteResults(false);

            syncAdapters();

            //DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getFilter().filter("");

            if(DDGControlVar.isAutocompleteActive) {
                autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);
                DDGControlVar.mDuckDuckGoContainer.tempAdapter.notifyDataSetChanged();
            } else {
                autoCompleteResultListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);
                DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
            }
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
            BusProvider.getInstance().post(new AutoCompleteResultClickEvent(position));
        } else {
            Object adapter = parent.getAdapter();
            Cursor c = null;
            HistoryObject obj = null;

            Object itemClicked = ((Adapter) adapter).getItem(position);
            if (itemClicked instanceof Cursor) {
                c = (Cursor) itemClicked;
                obj = new HistoryObject(c);
            }

            if (obj != null) {
                Log.e("aaa", "object: " + obj.toString());
                BusProvider.getInstance().post(new HistoryItemSelectedEvent(obj));
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return false;
    }

    @Subscribe
    public void onShowAutoCompleteResultsEvent(ShowAutoCompleteResultsEvent event) {
        //showSearch();
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

        LinearLayout.LayoutParams searchParams = (LinearLayout.LayoutParams) searchListView.getLayoutParams();

        ListView.LayoutParams headerParams = (ListView.LayoutParams) headerPadding.getLayoutParams();

        //Log.e("aaa", "top margin: "+searchParams.topMargin);
        //  Log.e("aaa", "header height: "+headerParams.height);

        //visibleHeight = visibleHeight - (searchParams.topMargin * 3);
        //visibleHeight = visibleHeight - (headerParams.height * 3);
        visibleHeight = visibleHeight - (searchListView.getPaddingTop() * 3);

        int maxItems = visibleHeight / itemHeight;
        int recentItems = 0;
        //Log.e("aaa", "total: "+totalHeight+" - visible: "+visibleHeight+" - max items: "+maxItems+" - search margin: "+searchParams.topMargin+" - item height: "+itemHeight);

        if(portrait && (totalHeight - visibleHeight) > (statusBar + navigationBar + actionBarHeight)) {
            //Log.e("aaa", "open keyboard");
            //Log.e("aaa", "max items: "+maxItems);

            recentItems = (maxItems - 1) <= recentAdapter.getCount() ? (maxItems - 1) : recentAdapter.getCount();

        } else {
            //Log.e("aaa", "close keyboard");
            //Log.e("aaa", "max items: "+maxItems);
            int halfItems = maxItems / 2;
            //recentItems = halfItems <= DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount() ? halfItems : DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount();
        }

        //Log.e("aaa", "recent items: "+recentItems);
        boolean shouldUpdateAdapters = recentItems!=0 && recentItems!=recentAdapter.getCount();
        //Log.e("aaa", "should update adapters: "+shouldUpdateAdapters);
        if(shouldUpdateAdapters) {/*
            DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory(recentItems));
            DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            searchListView.setLimit(recentItems);*/
            syncAdapters(recentItems);
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
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        recentAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory(limit));
        recentAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    private void syncAdapters() {
        //Log.e("aaa", "syncadapters");
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
        recentAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        recentAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        searchListView.setLimit(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount());
    }

    private void syncAdapters(int limit) {
        //Log.e("aaa", "syncadapters with limit: "+limit);
        //DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory(limit));
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
        recentAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory(limit));
        recentAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        //searchListView.setLimit(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getCount());
        searchListView.setLimit(recentAdapter.getCount());
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

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && searchMenu!=null) {
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(getActivity());
            //overflowMenu.setHeaderMenu(feedMenu);
            overflowMenu.setMenu(searchMenu);
            overflowMenu.show(event.anchor);

            Log.e("aaa", "shuld open feed menu now, feed menu != null");
        } else {
            Log.e("aaa", "shuld open feed menu now, feed menu == null");
        }
    }


}
