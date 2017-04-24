package com.duckduckgo.mobile.android.views.pageindicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.DimenUtils;

/**
 * Created by fgei on 4/6/17.
 */

public class BannerOnboardingPageIndicator extends LinearLayout {

    private static final int SELECTED_COLOR = R.color.mini_page_indicator_selected;
    private static final int UNSELECTED_COLOR = R.color.mini_page_indicator_unselected;

    private ViewPager viewPager;
    private int numIndicator = 0;
    private int positionSelected = 0;

    public BannerOnboardingPageIndicator(Context context) {
        super(context);
    }

    public BannerOnboardingPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public BannerOnboardingPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BannerOnboardingPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewPager(ViewPager viewPager) {
        setGravity(Gravity.CENTER_VERTICAL);
        this.viewPager = viewPager;
        setNumIndicator(viewPager.getAdapter().getCount());
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setPositionSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setPositionSelected(0);
    }

    private void setPositionSelected(int position) {
        for(int i=0; i < getChildCount(); i++) {
            ImageView child = (ImageView) getChildAt(i);
            child.setImageDrawable(i == position ? new SelectedIndicator() : new UnselectedIndicator());
        }
    }

    @ColorInt
    private int getSelectedColor() {
        return ContextCompat.getColor(getContext(), SELECTED_COLOR);
    }

    @ColorInt
    private int getUnselectedColor() {
        return ContextCompat.getColor(getContext(), UNSELECTED_COLOR);
    }

    private void setNumIndicator(int numIndicator) {
        this.numIndicator = numIndicator;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int)DimenUtils.convertDpToPixel(9, getContext()), 0, 0, 0);
        LayoutParams startParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startParam.setMargins(0, 0, 0, 0);
        for(int i=0; i<numIndicator; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(i==0 ? startParam : params);
            imageView.setImageDrawable(new UnselectedIndicator());
            addView(imageView);
        }
    }

    private class SelectedIndicator extends GradientDrawable {
        public SelectedIndicator() {
            setShape(OVAL);
            setColor(getSelectedColor());
            setSize((int) DimenUtils.convertDpToPixel(8, getContext()), (int) DimenUtils.convertDpToPixel(8, getContext()));
        }
    }

    private class UnselectedIndicator extends GradientDrawable {
        public UnselectedIndicator() {
            setShape(OVAL);
            setColor(getUnselectedColor());
            setSize((int) DimenUtils.convertDpToPixel(6, getContext()), (int) DimenUtils.convertDpToPixel(6, getContext()));
        }
    }
}
