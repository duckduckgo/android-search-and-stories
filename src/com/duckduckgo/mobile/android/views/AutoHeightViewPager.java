package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by fgei on 4/14/17.
 */

public class AutoHeightViewPager extends ViewPager {
    public AutoHeightViewPager(Context context) {
        super(context);
    }
    public AutoHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 0;
        Log.e("auto_height", "child count: "+getChildCount());
        for(int i=0; i<getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int childHeight = child.getMeasuredHeight();
            if(childHeight > maxHeight) {
                maxHeight = childHeight;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
