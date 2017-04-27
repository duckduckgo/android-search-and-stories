package com.duckduckgo.mobile.android.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

/**
 * Created by fgei on 4/7/17.
 */

public class ColorUtils {
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
