package com.duckduckgo.mobile.android.fragment;

import android.animation.Animator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.duckduckgo.mobile.android.views.SlidingTabLayout;
import com.squareup.otto.Subscribe;

public class FavoriteFragment extends Fragment {

	public static final String TAG = "saved_fragment";

    private DDGPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    //private DDGActionBarManager

	private View fragmentView;

    private Menu favoriteMenu = null;
    private DDGOverflowMenu overflowMenu = null;

    private boolean isVisible = false;

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
        int searchesResId = (width >= getResources().getDimension(R.dimen.tab_big) ? R.string.favorite_search : R.string.favorite_search_narrow);

        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(),
                new String[] {getResources().getString(storiesResId), getResources().getString(searchesResId)},
                new Fragment[] {new FavoriteFeedTabFragment(), new FavoriteResultTabFragment()});

        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        //slidingTabLayout = (SlidingTabLayout) fragmentView.findViewById(R.id.sliding_tabs);
        //slidingTabLayout = DDGActionBarManager.getInstance().getSlidingTabLayout();
        //slidingTabLayout.setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.actionbar_tab_selected));
        DDGActionBarManager.getInstance().getSlidingTabLayout().setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.actionbar_tab_selected));

        //slidingTabLayout.setViewPager(viewPager);
        DDGActionBarManager.getInstance().getSlidingTabLayout().setViewPager(viewPager);

        //slidingTabLayout.setVisibility(View.VISIBLE);

        favoriteMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.main, favoriteMenu);

		if(savedInstanceState!=null) {
			//savedTabHost.setCurrentTabByTag(savedInstanceState.getString("tag"));
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        //viewPager.setAdapter(pagerAdapter);
        //DDGActionBarManager.getInstance().getSlidingTabLayout().setViewPager(viewPager);
        //setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_FAVORITE && DDGControlVar.homeScreenShowing);//aaa
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            viewPager.setAdapter(pagerAdapter);
            DDGActionBarManager.getInstance().getSlidingTabLayout().setViewPager(viewPager);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("aaa", "configuration changed");
        //slidingTabLayout.setViewPager(viewPager);
        DDGActionBarManager.getInstance().getSlidingTabLayout().setViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_favorites).setEnabled(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putString("tag", savedTabHost.getCurrentTabTag());
	}

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation anim = null;
        try {
            anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        } catch(Resources.NotFoundException e) {
            e.printStackTrace();
        }

        if(anim==null) {
            return null;
        }

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(enter) {
                    DDGActionBarManager.getInstance().showTabLayout();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(anim);


        return animSet;
    }

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && favoriteMenu!=null) {
            favoriteMenu.findItem(R.id.action_favorites).setEnabled(false);
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(getActivity());
            //overflowMenu.setHeaderMenu(feedMenu);
            overflowMenu.setMenu(favoriteMenu);
            overflowMenu.show(event.anchor);
        }
    }

}