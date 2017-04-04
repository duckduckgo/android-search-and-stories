package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public class NoAdsFragment extends BaseOnboardingFragment {

    public static final String TAG = "no_ads_fragment";

    public static final int TITLE = R.string.no_ads_title;
    public static final int SUBTITLE = R.string.no_ads_subtitle;
    public static final int IMG = R.drawable.noads;
    public static final int BACKGROUND_COLOR = R.color.onboarding_no_ads_background;

    public static NoAdsFragment newInstance(int indexPosition) {
        NoAdsFragment f = new NoAdsFragment();
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
