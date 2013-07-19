package com.duckduckgo.mobile.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.duckduckgo.mobile.android.fragment.LeftFragment;
import com.duckduckgo.mobile.android.fragment.MainFragment;

public class DDGPagerAdapter extends FragmentPagerAdapter {
	
	LeftFragment leftFragment;
	MainFragment mainFragment;
	
	public DDGPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		leftFragment = new LeftFragment();
		mainFragment = new MainFragment();
	}
	
    public int getCount() {
        return 2;
    }
    
    @Override
    public Fragment getItem(int position) {
    	switch(position) {
    	case 0:
    		return leftFragment;
    	default:
    		return mainFragment;
    	}
    }
    
    public float getPageWidth(int position)
    {
    if (position == 0)
        {
        return 0.806f;
        }
    return 1f;
    }
}