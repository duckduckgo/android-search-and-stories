package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
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

        int width;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();
        }

        int storiesResId = (width >= getResources().getDimension(R.dimen.tab_small) ? R.string.favorite_stories : R.string.favorite_stories_narrow);
        int searchesResId = (width >= getResources().getDimension(R.dimen.tab_big) ? R.string.favorited_search : R.string.favorited_search_narrow);

        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(),
                new String[] {getResources().getString(storiesResId), getResources().getString(searchesResId)},
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
        setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_FAVORITE && DDGControlVar.homeScreenShowing);
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