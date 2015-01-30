package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.SlidingTabLayout;

public class RecentsFragment extends Fragment {

    public static final String TAG = "recents_fragment";

    private ViewPager viewPager;
    private DDGPagerAdapter pagerAdapter;
    private SlidingTabLayout slidingTabLayout;

    private View fragmentView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_saved_recents, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(), new String[] {"Recents", "Recent searches"}, new Fragment[] {new RecentFeedTabFragment(), new RecentResultTabFragment()});
        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) fragmentView.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.actionbar_tab_selected));
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_RECENTS);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_history).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
