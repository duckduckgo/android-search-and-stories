package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;
import com.duckduckgo.mobile.android.views.SlidingTabLayout;

public class SavedFragment extends Fragment {

	public static final String TAG = "saved_fragment";

    private DDGPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

	private View fragmentView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//BusProvider.getInstance().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//setRetainInstance(true);
        fragmentView = inflater.inflate(R.layout.fragment_saved_recents, container, false);
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(), new String[] {"Favourites", "Favourited searches"}, new Fragment[] {new SavedFeedTabFragment(), new SavedResultTabFragment()});

        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) fragmentView.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.actionbar_tab_selected));

        slidingTabLayout.setViewPager(viewPager);

		if(savedInstanceState!=null) {
			//savedTabHost.setCurrentTabByTag(savedInstanceState.getString("tag"));
		}
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("aaa", "configuration changed");
        slidingTabLayout.setViewPager(viewPager);
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putString("tag", savedTabHost.getCurrentTabTag());
	}
}