package com.duckduckgo.mobile.android.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public class PageIndicator extends LinearLayout {

    private static final int SELECTED_COLOR = R.color.page_indicator_selected;
    private static final int UNSELECTED_COLOR = R.color.page_indicator_unselected;

    private int numIndicator = 0;
    private int selectedItem = 0;
    private ViewPager viewPager;

    public PageIndicator(Context context) {
        super(context);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        setNumIndicator(viewPager.getAdapter().getCount());
        setPositionSelected(selectedItem);/*
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
        });*/
    }

    private void setNumIndicator(int numIndicator) {
        this.numIndicator = numIndicator;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, (int)convertDpToPixel(3.75f, getContext()), 0);
        for(int i=0; i<numIndicator; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(params);
            imageView.setImageDrawable(new Indicator());
            addView(imageView);
        }
    }

    public void setPositionSelected(int position, float percentage) {
        if(percentage == 1) selectedItem = position;
        int color = blendARGB(getSelectedColor(), getUnselectedColor(), percentage);
        ImageView child = (ImageView) getChildAt(position);
        ((Indicator)child.getDrawable()).setColor(color);
    }

    public void _setPositionSelected(int position, float percentage) {
        float inversePercentage = 1 - percentage;
        //ImageView prevSelected = (ImageView) getChildAt(selectedItem);
        //ImageView nextSelected = (ImageView) getChildAt(position);
        int prevColor = blendARGB(getSelectedColor(), getUnselectedColor(), inversePercentage);
        int nextColor = blendARGB(getSelectedColor(), getUnselectedColor(), percentage);
        //((Indicator)prevSelected.getDrawable()).setColor(prevColor);
        //((Indicator)nextSelected.getDrawable()).setColor(nextColor);
        for(int i=0; i<getChildCount(); i++) {
            ImageView child = (ImageView) getChildAt(i);
            int newColor = getUnselectedColor();
            if(position == i) newColor = nextColor;
            if(selectedItem == i) newColor = prevColor;
            ((Indicator)child.getDrawable()).setColor(newColor);
        }
        if(percentage == 1) {
            selectedItem = position;
            //setPositionSelected(position);
        }
    }

    private void setPositionSelected(int position) {
        this.selectedItem = position;
        for(int i=0; i< getChildCount(); i++) {
            ImageView child = (ImageView) getChildAt(i);
            ((Indicator)child.getDrawable()).setColor(i == position ? getSelectedColor() : getUnselectedColor());
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

    private class Indicator extends GradientDrawable {
        public Indicator() {
            setShape(OVAL);
            setSize((int)convertDpToPixel(10, getContext()), (int)convertDpToPixel(10, getContext()));
            setColor(getUnselectedColor());
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

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
