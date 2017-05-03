package com.duckduckgo.mobile.android.fragment.onboarding;

import com.duckduckgo.mobile.android.util.OnboardingPageConfiguration;

/**
 * Created by fgei on 4/4/17.
 */

public class RightFragment extends BaseOnboardingFragment {

    public static final String TAG = "right_fragment";

    public static RightFragment newInstance(int indexPosition) {
        return newInstance(indexPosition, false);
    }

    public static RightFragment newInstance(int indexPosition, boolean isHeader) {
        RightFragment f = new RightFragment();
        f.setArguments(createArgs(indexPosition, isHeader));
        return f;
    }

    @Override
    protected OnboardingPageConfiguration getOnboardingPageConfiguration() {
        return OnboardingPageConfiguration.getRight();
    }
}
