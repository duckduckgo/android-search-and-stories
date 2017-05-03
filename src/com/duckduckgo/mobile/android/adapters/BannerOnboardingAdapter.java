package com.duckduckgo.mobile.android.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.OnboardingPageConfiguration;

/**
 * Created by fgei on 4/11/17.
 */

public class BannerOnboardingAdapter extends PagerAdapter {

    private final OnboardingPageConfiguration[] items;

    public BannerOnboardingAdapter() {
        items = new OnboardingPageConfiguration[] {
                OnboardingPageConfiguration.getPrivacy(),
                OnboardingPageConfiguration.getNoAds(),
                OnboardingPageConfiguration.getNoTracking(),
                OnboardingPageConfiguration.getRight()
        };
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        OnboardingPageConfiguration onboardingPageConfiguration = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.viewholder_onboarding, container, false);
        populate(root, onboardingPageConfiguration);
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
        return items.length;
    }

    public OnboardingPageConfiguration getItem(int position) {
        return items[position];
    }

    private void populate(View rootView, OnboardingPageConfiguration onboardingPageConfiguration) {
        TextView title = (TextView) rootView.findViewById(R.id.title_text_view);
        title.setText(onboardingPageConfiguration.title);
        TextView subtitle = (TextView) rootView.findViewById(R.id.subtitle_text_view);
        subtitle.setText(onboardingPageConfiguration.subtitle);
        ImageView icon = (ImageView) rootView.findViewById(R.id.icon_image_view);
        icon.setImageResource(onboardingPageConfiguration.icon);
    }
}
