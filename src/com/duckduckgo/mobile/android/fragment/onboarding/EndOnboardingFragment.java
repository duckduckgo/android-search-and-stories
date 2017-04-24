package com.duckduckgo.mobile.android.fragment.onboarding;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/10/17.
 */

public class EndOnboardingFragment extends BaseOnboardingFragment {

    public static final int BACKGROUND_COLOR = R.color.onboarding_right_background;
    //public static final int BACKGROUND_COLOR = R.color.colorPrimary;

    public static EndOnboardingFragment newInstance(int indexPosition) {
        EndOnboardingFragment f = new EndOnboardingFragment();
        f.setArguments(createArgs(indexPosition, false));
        return f;
    }

    @Override
    protected int getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    @Override
    protected int getIcon() {
        return 0;
    }

    @Override
    protected String getTitle() {
        return "";
    }

    @Override
    protected String getSubtitle() {
        return "";
    }
}
