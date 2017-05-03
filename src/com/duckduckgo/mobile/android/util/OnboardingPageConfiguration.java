package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/30/17.
 */

public class OnboardingPageConfiguration {

    private static final int PRIVACY_TITLE = R.string.privacy_title;
    private static final int PRIVACY_SUBTITLE = R.string.privacy_subtitle;
    private static final int PRIVACY_ICON = R.drawable.illustration_1;
    private static final int PRIVACY_BACKGROUND_COLOR = R.color.onboarding_privacy_background;

    private static final int NO_ADS_TITLE = R.string.no_ads_title;
    private static final int NO_ADS_SUBTITLE = R.string.no_ads_subtitle;
    private static final int NO_ADS_ICON = R.drawable.illustration_2;
    private static final int NO_ADS_BACKGROUND_COLOR = R.color.onboarding_no_ads_background;

    private static final int NO_TRACKING_TITLE = R.string.no_tracking_title;
    private static final int NO_TRACKING_SUBTITLE = R.string.no_tracking_subtitle;
    private static final int NO_TRACKING_ICON = R.drawable.illustration_3;
    private static final int NO_TRACKING_BACKGROUND_COLOR = R.color.onboarding_no_tracking_background;

    private static final int RIGHT_TITLE = R.string.right_title;
    private static final int RIGHT_SUBTITLE = R.string.right_subtitle;
    private static final int RIGHT_ICON = R.drawable.illustration_4;
    private static final int RIGHT_BACKGROUND_COLOR = R.color.onboarding_right_background;

    public static OnboardingPageConfiguration getPrivacy() {
        return new OnboardingPageConfiguration(
                PRIVACY_TITLE,
                PRIVACY_SUBTITLE,
                PRIVACY_ICON,
                PRIVACY_BACKGROUND_COLOR
        );
    }

    public static OnboardingPageConfiguration getNoAds() {
        return new OnboardingPageConfiguration(
                NO_ADS_TITLE,
                NO_ADS_SUBTITLE,
                NO_ADS_ICON,
                NO_ADS_BACKGROUND_COLOR
        );
    }

    public static OnboardingPageConfiguration getNoTracking() {
        return new OnboardingPageConfiguration(
                NO_TRACKING_TITLE,
                NO_TRACKING_SUBTITLE,
                NO_TRACKING_ICON,
                NO_TRACKING_BACKGROUND_COLOR
        );
    }

    public static OnboardingPageConfiguration getRight() {
        return new OnboardingPageConfiguration(
                RIGHT_TITLE,
                RIGHT_SUBTITLE,
                RIGHT_ICON,
                RIGHT_BACKGROUND_COLOR
        );
    }

    public static OnboardingPageConfiguration getFadeOnboarding() {
        return new OnboardingPageConfiguration(0, 0, 0, RIGHT_BACKGROUND_COLOR);
    }

    public static int[] getBackgroundColors(Context context) {
        return new int[] {
                ContextCompat.getColor(context, PRIVACY_BACKGROUND_COLOR),
                ContextCompat.getColor(context, NO_ADS_BACKGROUND_COLOR),
                ContextCompat.getColor(context, NO_TRACKING_BACKGROUND_COLOR),
                ContextCompat.getColor(context, RIGHT_BACKGROUND_COLOR),
                ContextCompat.getColor(context, RIGHT_BACKGROUND_COLOR)
        };
    }

    public final int title;
    public final int subtitle;
    public final int icon;
    public final int backgroundColor;

    public OnboardingPageConfiguration(int title, int subtitle, int icon, int backgroundColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
    }
}
