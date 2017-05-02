package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.Onboarding;

/**
 * Created by fgei on 4/4/17.
 */

public class NoAdsFragment extends BaseOnboardingFragment {

    public static final String TAG = "no_ads_fragment";

    public static NoAdsFragment newInstance(int indexPosition) {
        return newInstance(indexPosition, false);
    }

    public static NoAdsFragment newInstance(int indexPosition, boolean isHeader) {
        NoAdsFragment f = new NoAdsFragment();
        f.setArguments(createArgs(indexPosition, isHeader));
        return f;
    }

    @Override
    protected Onboarding.OnboardingValue getOnboardingValue() {
        return Onboarding.getNoAds();
    }
}
