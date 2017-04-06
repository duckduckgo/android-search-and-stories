package com.duckduckgo.mobile.android.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by fgei on 4/6/17.
 */

public class MiniPageIndicator extends LinearLayout {

    private ViewPager viewPager;
    private int numIndicator = 0;

    public MiniPageIndicator(Context context) {
        super(context);
    }

    public MiniPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public MiniPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MiniPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    private void setNumIndicator(int numIndicator) {
        this.numIndicator = numIndicator;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(0, 0, );
        for(int i=0; i<numIndicator; i++) {
            ImageView imageView = new ImageView(getContext());
        }
    }

    private class Indicator extends GradientDrawable {
        public Indicator() {
        }
    }
}
