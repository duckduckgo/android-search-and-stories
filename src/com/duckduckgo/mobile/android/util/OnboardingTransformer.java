package com.duckduckgo.mobile.android.util;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingTransformer implements ViewPager.PageTransformer {

    private int[] backgroundColors;

    public OnboardingTransformer(int[] backgroundColors) {
        this.backgroundColors = backgroundColors;
    }

    @Override
    public void transformPage(View page, float position) {
        final View image = page.findViewById(R.id.icon_image_view);
        final float absolutePosition = Math.abs(position);
        int pageIndex = (Integer) page.getTag();

        if(position >= -1.0 && position <= 1.0) {
            if(position > 0) {
                int leftColor = backgroundColors[pageIndex - 1];
                int rightColor = backgroundColors[pageIndex];

                int color = blendARGB(rightColor, leftColor, position);
                page.setBackgroundColor(color);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    image.setScaleX(1 - absolutePosition);
                    image.setScaleY(1 - absolutePosition);
                }
            } else if(position < 0) {
                int leftColor = backgroundColors[pageIndex];
                int rightColor = backgroundColors[pageIndex + 1];

                int color = blendARGB(rightColor, leftColor, 1 - absolutePosition);
                page.setBackgroundColor(color);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    image.setScaleX(1 - absolutePosition);
                    image.setScaleY(1 - absolutePosition);
                }
            } else {
                page.setBackgroundColor(backgroundColors[pageIndex]);
            }
        } else {
            page.setBackgroundColor(backgroundColors[pageIndex]);
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
