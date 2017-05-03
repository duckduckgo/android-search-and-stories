package com.duckduckgo.mobile.android.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/30/17.
 */

public class OnboardingPageConfiguration implements Parcelable {

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

    private static final int INVALID_RES_ID = -1;

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
        return new OnboardingPageConfiguration(RIGHT_BACKGROUND_COLOR);
    }

    public final int title;
    public final int subtitle;
    public final int icon;
    public final int backgroundColor;
    public final boolean hasContent;

    public OnboardingPageConfiguration(int title, int subtitle, int icon, int backgroundColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
        this.hasContent = true;
    }

    public OnboardingPageConfiguration(int backgroundColor) {
        this.title = INVALID_RES_ID;
        this.subtitle = INVALID_RES_ID;
        this.icon = INVALID_RES_ID;
        this.backgroundColor = backgroundColor;
        this.hasContent = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.title);
        dest.writeInt(this.subtitle);
        dest.writeInt(this.icon);
        dest.writeInt(this.backgroundColor);
        dest.writeByte(this.hasContent ? (byte) 1 : (byte) 0);
    }

    protected OnboardingPageConfiguration(Parcel in) {
        this.title = in.readInt();
        this.subtitle = in.readInt();
        this.icon = in.readInt();
        this.backgroundColor = in.readInt();
        this.hasContent = in.readByte() != 0;
    }

    public static final Creator<OnboardingPageConfiguration> CREATOR = new Creator<OnboardingPageConfiguration>() {
        @Override
        public OnboardingPageConfiguration createFromParcel(Parcel source) {
            return new OnboardingPageConfiguration(source);
        }

        @Override
        public OnboardingPageConfiguration[] newArray(int size) {
            return new OnboardingPageConfiguration[size];
        }
    };
}
