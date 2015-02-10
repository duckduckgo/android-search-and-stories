package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

public class FavoriteFragment extends Fragment {

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
        fragmentView = inflater.inflate(R.layout.fragment_favorite_recents, container, false);
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(),
                new String[] {getResources().getString(R.string.favorite_stories), getResources().getString(R.string.favorited_search)},
                new Fragment[] {new FavoriteFeedTabFragment(), new FavoriteResultTabFragment()});

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
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_SAVED);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("aaa", "configuration changed");
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_favorites).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putString("tag", savedTabHost.getCurrentTabTag());
	}
}