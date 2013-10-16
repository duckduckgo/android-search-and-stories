package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.HideKeyboardEvent;

public class DDGViewPager extends ViewPager {

    private boolean enabled;
	private boolean dispatchTouch;
	
    public DDGViewPager(Context context) {
		super(context);
        this.enabled = true;
		this.dispatchTouch = true;
        initOnPageChangeListener();
	}

    public DDGViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
        this.dispatchTouch = true;
        initOnPageChangeListener();
    }

    private void initOnPageChangeListener() {
        setOnPageChangeListener(new SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(isLeftMenuOpen()){
                    BusProvider.getInstance().post(new HideKeyboardEvent(200));
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }

    public boolean isLeftMenuOpen() {
    	return getCurrentItem() == 0;
    }
    
    /**
     * Switch between two pages, left and right
     */
    public void switchPage() {
    	int otherItem = getCurrentItem() == 0 ? 1 : 0;
		setCurrentItem(otherItem);
    }

    public void hideMenu() {
        setCurrentItem(1);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	if(dispatchTouch)
    		return super.dispatchTouchEvent(ev);
    	return false;
    }
    
    public void setDispatchTouch(boolean dispatchTouch) {
    	this.dispatchTouch = dispatchTouch;
    }
}