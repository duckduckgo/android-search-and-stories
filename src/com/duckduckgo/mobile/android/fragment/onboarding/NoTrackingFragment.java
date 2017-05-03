package com.duckduckgo.mobile.android.fragment.onboarding;

import com.duckduckgo.mobile.android.util.OnboardingPageConfiguration;

/**
 * Created by fgei on 4/4/17.
 */

public class NoTrackingFragment extends BaseOnboardingFragment {

    public static final String TAG = "no_tracking_fragment";

    public static NoTrackingFragment newInstance(int indexPosition) {
        return newInstance(indexPosition, false);
    }

    public static NoTrackingFragment newInstance(int indexPosition, boolean isHeader) {
        NoTrackingFragment f = new NoTrackingFragment();
        f.setArguments(createArgs(indexPosition, isHeader));
        return f;
    }

    @Override
    protected OnboardingPageConfiguration getOnboardingPageConfiguration() {
        return OnboardingPageConfiguration.getNoTracking();
    }
}
