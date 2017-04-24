package com.duckduckgo.mobile.android.views.pageindicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.ColorUtils;
import com.duckduckgo.mobile.android.util.DimenUtils;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingPageIndicator extends LinearLayout {

    private static final int SELECTED_COLOR = R.color.page_indicator_selected;
    private static final int UNSELECTED_COLOR = R.color.page_indicator_unselected;

    private int numIndicator = 0;
    private int selectedItem = 0;
    private ViewPager viewPager;

    public OnboardingPageIndicator(Context context) {
        super(context);
    }

    public OnboardingPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public OnboardingPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public OnboardingPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewPager(ViewPager viewPager, int count) {
        this.viewPager = viewPager;
        setGravity(Gravity.CENTER_VERTICAL);
        setNumIndicator(count);
        setPositionSelected(selectedItem);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        setNumIndicator(viewPager.getAdapter().getCount());
        setPositionSelected(selectedItem);
    }

    private void setNumIndicator(int numIndicator) {
        this.numIndicator = numIndicator;
        LayoutParams startParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startParams.setMargins(0, 0, 0, 0);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int)DimenUtils.convertDpToPixel(5, getContext()), 0, 0, 0);
        for(int i=0; i<numIndicator; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(i == 0 ? startParams : params);
            //imageView.setBackgroundResource(
                    //R.drawable.onboarding_pageindicator_background);
            //imageView.setBackground(new Background(getUnselectedColor()).create());
            setImageViewBackground(imageView, new Background(getUnselectedColor()).create());
            imageView.setImageDrawable(new Indicator(getSelectedColor()));
            addView(imageView);
        }
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.loupe);
        addView(imageView);
    }

    public void setPositionSelected(int position, float percentage) {
        if(percentage == 1) selectedItem = position;
        //int color = ColorUtils.blendARGB(getSelectedColor(), getUnselectedColor(), percentage);
        if(position == getChildCount() - 1) return;
        ImageView child = (ImageView) getChildAt(position);
        if(child == null) return;
        //((Indicator)child.getDrawable()).setColor(color);//todo indicator
        ((Indicator)child.getDrawable()).setColor(percentage);
    }

    public void _setPositionSelected(int position, float percentage) {
        float inversePercentage = 1 - percentage;
        //ImageView prevSelected = (ImageView) getChildAt(selectedItem);
        //ImageView nextSelected = (ImageView) getChildAt(position);
        int prevColor = ColorUtils.blendARGB(getSelectedColor(), getUnselectedColor(), inversePercentage);
        int nextColor = ColorUtils.blendARGB(getSelectedColor(), getUnselectedColor(), percentage);
        //((Indicator)prevSelected.getDrawable()).setColor(prevColor);
        //((Indicator)nextSelected.getDrawable()).setColor(nextColor);
        for(int i=0; i<getChildCount(); i++) {
            ImageView child = (ImageView) getChildAt(i);
            int newColor = getUnselectedColor();
            if(position == i) newColor = nextColor;
            if(selectedItem == i) newColor = prevColor;
            //((Indicator)child.getDrawable()).setColor(newColor);//todo indicator
        }
        if(percentage == 1) {
            selectedItem = position;
        }
    }

    private void setPositionSelected(int position) {
        this.selectedItem = position;
        for(int i=0; i< getChildCount() - 1; i++) {
            ImageView child = (ImageView) getChildAt(i);
            //((Indicator)child.getDrawable()).setColor(i == position ? getSelectedColor() : getUnselectedColor());//todo indicator
            ((Indicator)child.getDrawable()).setSelected(i == position);
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

    @SuppressWarnings("deprecation")
    private void setImageViewBackground(ImageView imageView, Drawable background) {
        if(Build.VERSION.SDK_INT >= 16) {
            imageView.setBackground(background);
        } else {
            imageView.setBackgroundDrawable(background);
        }
    }

    private class Background {
        @ColorInt private final int BACKGROUND_COLOR;
        private LayerDrawable background;
        public Background(@ColorInt int backgroundColor) {
            BACKGROUND_COLOR = backgroundColor;
            GradientDrawable firstItem = new GradientDrawable();
            firstItem.setColor(Color.TRANSPARENT);
            firstItem.setShape(GradientDrawable.OVAL);
            firstItem.setSize((int)DimenUtils.convertDpToPixel(10, getContext()), (int)DimenUtils.convertDpToPixel(10, getContext()));
            GradientDrawable secondItem = new GradientDrawable();

            secondItem.setColor(BACKGROUND_COLOR);
            secondItem.setShape(GradientDrawable.OVAL);
            secondItem.setSize((int)DimenUtils.convertDpToPixel(8, getContext()), (int)DimenUtils.convertDpToPixel(8, getContext()));
            background = new LayerDrawable(new Drawable[] {firstItem, secondItem});
            int pixelPadding = (int)DimenUtils.convertDpToPixel(1, getContext());
            background.setLayerInset(1, pixelPadding, pixelPadding, pixelPadding, pixelPadding);
        }

        public Drawable create() {
            return background;
        }
    }

    private class Indicator extends GradientDrawable {
        @ColorInt private final int SELECTED_COLOR;
        public Indicator(@ColorInt int color) {
            SELECTED_COLOR = color;
            setShape(OVAL);
            setSize((int)DimenUtils.convertDpToPixel(10, getContext()), (int)DimenUtils.convertDpToPixel(10, getContext()));
            setColor(SELECTED_COLOR);
        }
        public void setSelected(boolean selected) {
            setColor(selected ? SELECTED_COLOR : Color.TRANSPARENT);
        }
        public void setColor(@FloatRange(from = 0.0, to = 1.0) float percentage) {
            int color = ColorUtils.blendARGB(SELECTED_COLOR, Color.TRANSPARENT, percentage);
            setColor(color);
        }
    }

    private class OldIndicator extends GradientDrawable {
        public OldIndicator() {
            setShape(OVAL);
            setSize((int)DimenUtils.convertDpToPixel(8, getContext()), (int) DimenUtils.convertDpToPixel(8, getContext()));
            setColor(getUnselectedColor());
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.selectedItem = selectedItem;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setPositionSelected(savedState.selectedItem);
    }

    private static class SavedState extends BaseSavedState {
        int selectedItem;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            selectedItem = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selectedItem);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
