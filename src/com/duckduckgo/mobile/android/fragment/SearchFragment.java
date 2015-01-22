package com.duckduckgo.mobile.android.fragment;

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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.SavedSearchListView;
import com.squareup.otto.Subscribe;

public class SearchFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener {

    public static final String TAG = "search_fragment";

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

        recentSearchListView = (RecentSearchListView) fragmentView.findViewById(R.id.recentList);
        recentSearchListView.setDivider(null);
        //recentSearchAdapter = new RecentResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSearchHistory());
        //recentSearchListView.setAdapter(recentSearchAdapter);
        recentSearchListView.setAdapter(DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter);

        //setMaxItemVisible(recentSearchListView);

        savedSearchListView = (SavedSearchListView) fragmentView.findViewById(R.id.savedList);
        savedSearchListView.setDivider(null);
        savedSearchAdapter = new SavedResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
        savedSearchListView.setAdapter(savedSearchAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("aaa", "on resume");
        if(recentSearchListView.getCount()!=0) {
            setMaxItemVisible(recentSearchListView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(viewTreeObserver!=null) {
            //if(Build.VERSION.SDK_INT>=)
            //viewTreeObserver.removeOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("aaa", "Search fragment hidden: " + hidden);
    }

    @Subscribe
    public void onSyncAdaptersEvent(SyncAdaptersEvent event) {
        //recentSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        //recentSearchAdapter.notifyDataSetChanged();
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.notifyDataSetChanged();
        savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
        savedSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGlobalLayout() {
        int totalHeight = search_container.getRootView().getHeight();// - (int) getActivity().getResources().getDimension(R.dimen.actionbar_height);
        int visibleHeight = search_container.getHeight();

        int statusBar = getStatusBarHeight();
        int navigationBar = getNavigationBarHeight();
        Log.e("aaa", "status bar: "+statusBar);
        Log.e("aaa", "navigation bar: "+navigationBar);
        //int padding = recentSearchListView.getPaddingTop() * 3;
        totalHeight = totalHeight - statusBar - navigationBar - actionBarHeight;

        //if(2*visibleHeight<totalHeight) {
        if (visibleHeight < totalHeight) {
            Log.e("aaa", "open");
            Log.e("aaa", "total height: " + totalHeight);
            Log.e("aaa", "visible height: " + visibleHeight);
            Log.e("aaa", "difference: " + (totalHeight - visibleHeight));
            int itemHeight = (int) getActivity().getResources().getDimension(R.dimen.temp_item_height);
            Log.e("aaa", "item height: " + itemHeight);
            Log.e("aaa", "padding: "+recentSearchListView.getPaddingTop());
            //visibleHeight = visibleHeight - recentSearchListView.getPaddingBottom() - recentSearchListView.getPaddingTop() - savedSearchListView.getPaddingTop();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recentSearchListView.getLayoutParams();
            visibleHeight = visibleHeight - params.topMargin - params.bottomMargin - params.topMargin;
            Log.e("aaa", "new visible height: "+visibleHeight);
            int maxItems = visibleHeight / itemHeight;
            //ViewGroup.LayoutParams params = recentSearchListView.getLayoutParams();
            //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recentSearchListView.getLayoutParams();
            float newWeight = (float) (maxItems - 1) / maxItems;
            Log.e("aaa", "new weight: "+newWeight);
            params.weight = newWeight;
            recentSearchListView.setLayoutParams(params);
            //recentSearchListView.requestLayout();


            //int maxItems = (int) (visibleHeight / (itemHeight) + (recentSearchListView.getPaddingBottom() * 3));
            //maxItems = maxItems - 1;

            //ViewGroup.LayoutParams params = recentSearchListView.getLayoutParams();
            //int oldHeight = params.height;
            //int newHeight = (itemHeight * maxItems);
            //params.height = (itemHeight * maxItems);
            //listView.setLayoutParams(params);
            //listView.requestLayout();
            Log.e("aaa", "open, max items: "+maxItems);

        } else {
            //ViewGroup.LayoutParams params = recentSearchListView.getLayoutParams();
            //params.height = -2;
            //listView.setLayoutParams(params);
            //listView.requestLayout();
            Log.e("aaa", "close");
            Log.e("aaa", "total height: " + totalHeight);
            Log.e("aaa", "visible height: " + visibleHeight);
            Log.e("aaa", "difference: " + (totalHeight - visibleHeight));
            int itemHeight = (int) getActivity().getResources().getDimension(R.dimen.temp_item_height);
            Log.e("aaa", "item height: "+itemHeight);
            int maxItems = visibleHeight / itemHeight;
            Log.e("aaa", "max items: "+maxItems);
            //Log.e("aaa", "item height: "+item.getMeasuredHeight());
            //Log.e("aaa", "max items: "+(visibleHeight / item.getMeasuredHeight()));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recentSearchListView.getLayoutParams();
            params.weight = 0.5f;
            recentSearchListView.setLayoutParams(params);
            //recentSearchListView.requestLayout();
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
        Log.e("aaa", "--------------------inside calculate visible item");
        final View container = search_container;
        //Log.e("aaa", "search container measured height: "+search_container.getMeasuredHeight());
        viewTreeObserver = container.getViewTreeObserver();
        //viewTreeObserver.addOnGlobalLayoutListener(this);
        /*
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int totalHeight = container.getRootView().getHeight();// - (int) getActivity().getResources().getDimension(R.dimen.actionbar_height);
                int visibleHeight = container.getHeight();
                //if(2*visibleHeight<totalHeight) {
                if ((totalHeight - visibleHeight) > 300) {
                    Log.e("aaa", "open");
                    Log.e("aaa", "total height: " + totalHeight);
                    Log.e("aaa", "visible height: " + visibleHeight);
                    View item = listView.getAdapter().getView(0, null, listView);
                    item.measure(0, 0);
                    int itemHeight = (item.getMeasuredHeight() + listView.getDividerHeight());
                    Log.e("aaa", "item height: " + itemHeight);
                    int maxItems = (int) (visibleHeight / (itemHeight) + (listView.getPaddingBottom() * 3));
                    maxItems = maxItems - 1;

                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    int oldHeight = params.height;
                    int newHeight = (itemHeight * maxItems);
                    params.height = (itemHeight * maxItems);
                    //listView.setLayoutParams(params);
                    //listView.requestLayout();
                    //Log.e("aaa", "open, max items: "+maxItems);

                } else {
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.height = -2;
                    //listView.setLayoutParams(params);
                    //listView.requestLayout();
                    Log.e("aaa", "close");
                    Log.e("aaa", "total height: " + totalHeight);
                    Log.e("aaa", "visible height: " + visibleHeight);
                }

            }
        });*/

    }


}
