package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;

import com.duckduckgo.mobile.android.fragment.OnboardingPageFragment;
import com.duckduckgo.mobile.android.util.OnboardingPageConfiguration;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingPageAdapter extends FragmentPagerAdapter {

    private final OnboardingPageConfiguration[] items;
    private Context context;

    public OnboardingPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        items = new OnboardingPageConfiguration[] {
                OnboardingPageConfiguration.getPrivacy(),
                OnboardingPageConfiguration.getNoAds(),
                OnboardingPageConfiguration.getNoTracking(),
                OnboardingPageConfiguration.getRight(),
                OnboardingPageConfiguration.getFadeOnboarding()
        };
    }

    @Override
    public Fragment getItem(int position) {
        OnboardingPageConfiguration onboardingPageConfiguration = items[position];
        return OnboardingPageFragment.newInstance(onboardingPageConfiguration, position);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @ColorInt
    public int getBackgroundColor(int position) {
        return ContextCompat.getColor(context, items[position].backgroundColor);
    }
}
