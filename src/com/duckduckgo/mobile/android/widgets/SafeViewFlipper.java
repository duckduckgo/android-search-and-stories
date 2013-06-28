package com.duckduckgo.mobile.android.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Workaround for http://code.google.com/p/android/issues/detail?id=6191
 * (ViewFlipper throws IllegalArgumentException in rotation scenarios)
 */
public class SafeViewFlipper extends ViewFlipper {
  protected final String TAG = "SafeViewFlipper";
  
  public SafeViewFlipper(Context context) {
    super(context);
  }

  public SafeViewFlipper(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override 
  protected void onDetachedFromWindow() {
    try {
      super.onDetachedFromWindow();
    } catch (IllegalArgumentException e) {
      stopFlipping();
    }
  }
}