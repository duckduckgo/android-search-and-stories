package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;
import com.duckduckgo.mobile.android.util.Onboarding;
import com.squareup.picasso.Picasso;

/**
 * Created by fgei on 4/11/17.
 */

public class BannerOnboardingAdapter extends PagerAdapter {

    private static final int NUM_PAGES = 4;

    public BannerOnboardingAdapter() {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        Onboarding.OnboardingValue onboardingValue;
        switch(position) {
            case 0:
                onboardingValue = Onboarding.getPrivacy();
                break;
            case 1:
                onboardingValue = Onboarding.getNoAds();
                break;
            case 2:
                onboardingValue = Onboarding.getNoTracking();
                break;
            case 3:
                onboardingValue = Onboarding.getRight();
                break;
            default:
                throw new IllegalArgumentException("Invalid position for "+getClass().getSimpleName()+": "+position);
        }

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.viewholder_onboarding, container, false);
        TextView title = (TextView) root.findViewById(R.id.title_text_view);
        title.setText(onboardingValue.title);
        TextView subtitle = (TextView) root.findViewById(R.id.subtitle_text_view);
        subtitle.setText(onboardingValue.subtitle);
        ImageView icon = (ImageView) root.findViewById(R.id.icon_image_view);
        icon.setImageResource(onboardingValue.icon);
        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
