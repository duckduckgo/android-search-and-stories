package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public class NoTrackingFragment extends BaseOnboardingFragment {

    public static final String TAG = "no_tracking_fragment";

    public static final int TITLE = R.string.no_tracking_title;
    public static final int SUBTITLE = R.string.no_tracking_subtitle;
    public static final int IMG = R.drawable.notrack3;
    public static final int BACKGROUND_COLOR = R.color.onboarding_no_tracking_background;

    public static NoTrackingFragment newInstance(int indexPosition) {
        return newInstance(indexPosition, false);
    }

    public static NoTrackingFragment newInstance(int indexPosition, boolean isHeader) {
        NoTrackingFragment f = new NoTrackingFragment();
        f.setArguments(createArgs(indexPosition, isHeader));
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
