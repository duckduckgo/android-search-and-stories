package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.views.SlidingTabLayout;

public class StoredItemsFragment extends Fragment {

    public static final String TAG = "Stored_items_fragment";
    public static final String SAVED_TAG = "saved_fragment";
    public static final String RECENTS_TAG = "recents_fragment";

    private static boolean saved = false;
    private static boolean recents = false;

    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private DDGPagerAdapter pagerAdapter;

    private View fragmentView = null;

    private String[] titles = null;
    private Fragment[] fragments = null;

    public static StoredItemsFragment newSavedInstance() {
        StoredItemsFragment fragment = new StoredItemsFragment();
        fragment.saved = true;
        return fragment;
    }

    public static StoredItemsFragment newRecentInstance() {
        StoredItemsFragment fragment = new StoredItemsFragment();
        fragment.recents = true;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        fragmentView = inflater.inflate(R.layout.fragment_saved_recents, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(saved) {
            titles = new String[] {"Favourites", "Favourited searches"};
            fragments = new Fragment[] {new SavedFeedTabFragment(), new SavedResultTabFragment()};
        } else {
            titles = new String[] {"Recents", "Recents searches"};
            fragments = null;
        }
        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(), titles, fragments);
        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) fragmentView.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);


    }

}
