package com.duckduckgo.mobile.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;

/**
 * Created by fgei on 4/11/17.
 */

public class BannerOnboardingAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;

    public BannerOnboardingAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return PrivacyFragment.newInstance(position, true);
            case 1:
                return NoAdsFragment.newInstance(position, true);
            case 2:
                return NoTrackingFragment.newInstance(position, true);
            case 3:
                return RightFragment.newInstance(position, true);
            default:
                throw new IllegalArgumentException("Wrong position for Onboarding adapter: "+position);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
