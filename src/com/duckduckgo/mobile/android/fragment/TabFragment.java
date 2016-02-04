package com.duckduckgo.mobile.android.fragment;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;

/**
 * Created by fgei on 6/18/15.
 */
public abstract class TabFragment extends Fragment {

    protected abstract TabItem getFirstTabItem();
    protected abstract TabItem getSecondTabItem();
    protected abstract void setMenu(Menu menu);

    private ViewPager viewPager;
    private DDGPagerAdapter pagerAdapter;

    private TabItem firstTabItem;
    private TabItem secondTabItem;

    private View fragmentView = null;

    private Menu menu = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
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

        firstTabItem = getFirstTabItem();
        secondTabItem = getSecondTabItem();

        int storiesResId = (width >= getResources().getDimension(R.dimen.tab_small) ? firstTabItem.titleFull : firstTabItem.titleNarrow);
        int searchesResId = (width >= getResources().getDimension(R.dimen.tab_big) ? secondTabItem.titleFull : secondTabItem.titleNarrow);

        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(),
                new String[] {getResources().getString(storiesResId), getResources().getString(searchesResId)},
                new Fragment[] {firstTabItem.fragment, secondTabItem.fragment});
        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        DDGActionBarManager.getInstance().getSlidingTabLayout().setSelectedIndicatorColors(getActivity().getResources().getColor(R.color.actionbar_tab_selected));
        DDGActionBarManager.getInstance().getSlidingTabLayout().setViewPager(viewPager);

        menu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        setMenu(menu);
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
        DDGActionBarManager.getInstance().getSlidingTabLayout().setViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    class TabItem {

        public int titleFull;
        public int titleNarrow;
        public Fragment fragment;

        public TabItem(int titleFull, int titleNarrow, Fragment fragment) {
            this.titleFull = titleFull;
            this.titleNarrow = titleNarrow;
            this.fragment = fragment;
        }
    }
}
