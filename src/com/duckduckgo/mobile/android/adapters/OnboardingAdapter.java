package com.duckduckgo.mobile.android.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.duckduckgo.mobile.android.fragment.onboarding.BaseOnboardingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.EndOnboardingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingAdapter extends FragmentPagerAdapter {

    private final BaseOnboardingFragment[] items;

    public OnboardingAdapter(FragmentManager fm) {
        super(fm);
        items = new BaseOnboardingFragment[] {
                PrivacyFragment.newInstance(0),
                NoAdsFragment.newInstance(1),
                NoTrackingFragment.newInstance(2),
                RightFragment.newInstance(3),
                EndOnboardingFragment.newInstance(4)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return items[position];
    }

    @Override
    public int getCount() {
        return items.length;
    }
}
