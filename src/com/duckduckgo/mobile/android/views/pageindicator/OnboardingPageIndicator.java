package com.duckduckgo.mobile.android.views.pageindicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
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
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, (int)DimenUtils.convertDpToPixel(3.75f, getContext()), 0);
        for(int i=0; i<numIndicator; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(params);
            imageView.setImageDrawable(new Indicator());
            addView(imageView);
        }
    }

    public void setPositionSelected(int position, float percentage) {
        if(percentage == 1) selectedItem = position;
        int color = ColorUtils.blendARGB(getSelectedColor(), getUnselectedColor(), percentage);
        ImageView child = (ImageView) getChildAt(position);
        if(child == null) return;
        ((Indicator)child.getDrawable()).setColor(color);
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
            setSize((int)DimenUtils.convertDpToPixel(10, getContext()), (int) DimenUtils.convertDpToPixel(10, getContext()));
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
