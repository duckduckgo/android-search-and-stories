package com.duckduckgo.mobile.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

	private TabHostExt savedTabHost = null;

    private DDGPagerAdapter pagerAdapter;
    private ViewPager viewPager;

	private View fragmentView;

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
		setRetainInstance(true);
		//fragmentView = inflater.inflate(R.layout.fragment_saved, container, false);
        fragmentView = inflater.inflate(R.layout.fragment_saved_recents, container, false);
		//init();
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        //pagerAdapter = new DDGPagerAdapter(new String[] {"Favourites", "Favourited Searches"});
        pagerAdapter = new DDGPagerAdapter(getChildFragmentManager(), new String[] {"Favourites", "Favourited searches"}, new Fragment[] {new SavedFeedTabFragment(), new SavedResultTabFragment()});
        //SamplePagerAdapter adapter = new SamplePagerAdapter();
        //SuperFragmentPageradapter adapter2 = new SuperFragmentPageradapter(getChildFragmentManager());

        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        //viewPager.setAdapter(adapter);
        //viewPager.setAdapter(adapter2);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) fragmentView.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);

        slidingTabLayout.setViewPager(viewPager);

		if(savedInstanceState!=null) {
			//savedTabHost.setCurrentTabByTag(savedInstanceState.getString("tag"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putString("tag", savedTabHost.getCurrentTabTag());
	}
/*
	public void init() {
		//savedTabHost= (TabHostExt) fragmentView.findViewById(R.id.savedTabHost);
		//savedTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		//savedTabHost.addDefaultTabs();
	}

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o==view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return "Item "+(position+1);
            if(position==0)
                return "recent";
            return "recent searches";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.temp_fragment, container, false);
            container.addView(view);

            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText("ciao "+position);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    class SuperFragmentPageradapter extends FragmentPagerAdapter {

        public SuperFragmentPageradapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position==0 ? "uno" : "DUE";
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if(position==0) {
                return new Fragment1();
            } else {
                return new Fragment2();
            }
        }
    }

    public static class Fragment1 extends android.support.v4.app.Fragment {
        public Fragment1() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.temp_fragment, container, false);
            TextView text = (TextView) root.findViewById(R.id.text);
            text.setText("FRAGMENT numero 1");
            return root;
        }
    }

    public static class Fragment2 extends android.support.v4.app.Fragment {
        public Fragment2() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.temp_fragment, container, false);
            TextView text = (TextView) root.findViewById(R.id.text);
            text.setText("FRAGMENT numero 2222");
            return root;
        }
    }*/
}