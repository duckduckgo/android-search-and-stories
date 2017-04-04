package com.duckduckgo.mobile.android.adapters;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;
    public OnboardingAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return PrivacyFragment.newInstance(position);
            case 1:
                return NoAdsFragment.newInstance(position);
            case 2:
                return NoTrackingFragment.newInstance(position);
            case 3:
                return RightFragment.newInstance(position);
            default:
                throw new IllegalArgumentException("Wrong position for Onboarding adapter: "+position);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
