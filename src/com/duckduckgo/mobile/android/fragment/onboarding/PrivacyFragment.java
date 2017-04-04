package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public class PrivacyFragment extends BaseOnboardingFragment {

    public static final String TAG = "privacy_fragment";

    public static final int TITLE = R.string.privacy_title;
    public static final int SUBTITLE = R.string.privacy_subtitle;
    public static final int IMG = R.drawable.privacy;
    public static final int BACKGROUND_COLOR = R.color.onboarding_privacy_background;

    public static PrivacyFragment newInstance(int indexPosition) {
        PrivacyFragment f = new PrivacyFragment();
        f.setArguments(createArgs(indexPosition));
        return f;
    }

    @Override
    protected int getBackgroundColor() {
        return ContextCompat.getColor(getContext(), BACKGROUND_COLOR);
    }

    @Override
    protected int getIcon() {
        return IMG;
    }

    @Override
    protected String getTitle() {
        return getString(TITLE);
    }

    @Override
    protected String getSubtitle() {
        return getString(SUBTITLE);
    }
}
