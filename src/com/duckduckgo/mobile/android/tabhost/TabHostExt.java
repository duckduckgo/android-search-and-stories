package com.duckduckgo.mobile.android.tabhost;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

public class TabHostExt extends FragmentTabHost {

    public TabHostExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabHostExt(Context context) {
        super(context);
    }

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        // leave it empty here. It looks that when you use hard keyboard,
        // this method will be called and the focus will be token.
    }
}