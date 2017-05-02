package com.duckduckgo.mobile.android.fragment.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.Onboarding;

/**
 * Created by fgei on 4/10/17.
 */

public class EndOnboardingFragment extends BaseOnboardingFragment {

    public static EndOnboardingFragment newInstance(int indexPosition) {
        EndOnboardingFragment f = new EndOnboardingFragment();
        f.setArguments(createArgs(indexPosition, false));
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasContent(false);
    }

    @Override
    protected Onboarding.OnboardingValue getOnboardingValue() {
        return Onboarding.getFadeOnboarding();
    }
}
