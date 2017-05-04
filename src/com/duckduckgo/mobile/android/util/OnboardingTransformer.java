package com.duckduckgo.mobile.android.util;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.OnboardingPageAdapter;
import com.duckduckgo.mobile.android.views.pageindicator.OnboardingPageIndicator;

import java.util.List;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingTransformer implements ViewPager.PageTransformer {

    private OnboardingPageAdapter onboardingPageAdapter;
    private OnboardingPageIndicator pageIndicator;
    private List<View> fadeViews;

    public OnboardingTransformer(OnboardingPageAdapter onboardingPageAdapter, OnboardingPageIndicator pageIndicator, List<View> fadeViews) {
        this.onboardingPageAdapter = onboardingPageAdapter;
        this.pageIndicator = pageIndicator;
        this.fadeViews = fadeViews;
    }

    @Override
    public void transformPage(View page, float position) {
        final View image = page.findViewById(R.id.icon_image_view);
        final float absolutePosition = Math.abs(position);
        int pageIndex = (Integer) page.getTag();

        if(position >= -1.0 && position <= 1.0) {
            if(position > 0) {
                int leftColor = onboardingPageAdapter.getBackgroundColor(pageIndex - 1);
                int rightColor = onboardingPageAdapter.getBackgroundColor(pageIndex);

                int color = blendARGB(rightColor, leftColor, position);
                page.setBackgroundColor(color);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    image.setScaleX(1 - absolutePosition);
                    image.setScaleY(1 - absolutePosition);
                    if(pageIndex == (onboardingPageAdapter.getCount() - 1)) {
                        for(View v : fadeViews) {
                            v.setAlpha(absolutePosition);
                        }
                    }
                }
                pageIndicator.setPositionSelected(pageIndex - 1, 1 - absolutePosition);
                pageIndicator.setPositionSelected(pageIndex, absolutePosition);
            } else if(position < 0) {
                int nextIndex = pageIndex + 1;
                int leftColor = onboardingPageAdapter.getBackgroundColor(pageIndex);
                int rightColor = onboardingPageAdapter.getBackgroundColor(nextIndex);

                int color = blendARGB(rightColor, leftColor, 1 - absolutePosition);
                page.setBackgroundColor(color);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    image.setScaleX(1 - absolutePosition);
                    image.setScaleY(1 - absolutePosition);
                    if(nextIndex == (onboardingPageAdapter.getCount() - 1)) {
                        for(View v : fadeViews) {
                            v.setAlpha(1 - absolutePosition);
                        }
                    }
                }
                pageIndicator.setPositionSelected(pageIndex, absolutePosition);
                pageIndicator.setPositionSelected(pageIndex + 1, 1 - absolutePosition);
            } else {
                page.setBackgroundColor(onboardingPageAdapter.getBackgroundColor(pageIndex));
            }
        } else {
            page.setBackgroundColor(onboardingPageAdapter.getBackgroundColor(pageIndex));
        }
    }

    public static int blendARGB(@ColorInt int color1, @ColorInt int color2,
                                @FloatRange(from = 0.0, to = 1.0) float ratio) {
        final float inverseRatio = 1 - ratio;
        float a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio;
        float r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio;
        float g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio;
        float b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio;
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }
}
