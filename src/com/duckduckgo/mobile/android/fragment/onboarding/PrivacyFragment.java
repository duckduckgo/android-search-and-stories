package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.Onboarding;

/**
 * Created by fgei on 4/4/17.
 */

public class PrivacyFragment extends BaseOnboardingFragment {

    public static final String TAG = "privacy_fragment";

    public static PrivacyFragment newInstance(int indexPosition) {
        return newInstance(indexPosition, false);
    }

    public static PrivacyFragment newInstance(int indexPosition, boolean isHeader) {
        PrivacyFragment f = new PrivacyFragment();
        f.setArguments(createArgs(indexPosition, isHeader));
        return f;
    }

    @Override
    protected Onboarding.OnboardingValue getOnboardingValue() {
        return Onboarding.getPrivacy();
    }
}
