package com.duckduckgo.mobile.android.util;

import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.ViewTreeObserver;

/**
 * Created by fgei on 4/21/17.
 */

public class CompatUtils {
    private CompatUtils() {
    }

    public static void getDisplaySize(Display display, Point size) {
        if(Build.VERSION.SDK_INT >= 13) {
            display.getSize(size);
        } else {
            getDisplaySizeCompat(display, size);
        }
    }

    @SuppressWarnings("deprecation")
    private static void getDisplaySizeCompat(Display display, Point size) {
        size.x = display.getWidth();
        size.y = display.getHeight();
    }

    public static void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if(Build.VERSION.SDK_INT >= 16) {
            viewTreeObserver.removeOnGlobalLayoutListener(listener);
        } else {
            removeOnGlobalLayoutListenerCompat(viewTreeObserver, listener);
        }
    }

    @SuppressWarnings("deprecation")
    private static void removeOnGlobalLayoutListenerCompat(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener listener) {
        viewTreeObserver.removeGlobalOnLayoutListener(listener);
    }
}
