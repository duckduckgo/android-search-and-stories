package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public class RightFragment extends BaseOnboardingFragment {

    public static final String TAG = "right_fragment";

    public static final int TITLE = R.string.right_title;
    public static final int SUBTITLE = R.string.right_subtitle;
    public static final int IMG = R.drawable.right;
    public static final int BACKGROUND_COLOR = R.color.onboarding_right_background;

    public static RightFragment newInstance(int indexPosition) {
        RightFragment f = new RightFragment();
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
