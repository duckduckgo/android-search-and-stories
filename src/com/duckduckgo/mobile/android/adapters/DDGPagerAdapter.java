package com.duckduckgo.mobile.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DDGPagerAdapter extends FragmentPagerAdapter{

    private String[] titles = null;
    private Fragment[] fragments = null;

    public DDGPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public DDGPagerAdapter(FragmentManager fm, String[] titles, Fragment[] fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        if(fragments==null) return 0;
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(titles==null || titles.length<position)
            return "";
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        if(fragments==null || fragments.length<position)
            return null;
        return fragments[position];
    }
}
