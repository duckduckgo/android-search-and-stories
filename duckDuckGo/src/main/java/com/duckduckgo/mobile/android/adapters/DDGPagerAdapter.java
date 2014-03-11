package com.duckduckgo.mobile.android.adapters;

import java.util.ArrayList;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.DDGViewPager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

public class DDGPagerAdapter extends PagerAdapter {
	
	ArrayList<View> mPageViews;
	
	public DDGPagerAdapter(Context context) {		
		LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mPageViews = new ArrayList<View>();

		mPageViews.add(inflater.inflate(R.layout.left_layout, null));
		mPageViews.add(inflater.inflate(R.layout.main, null));
	}
	
    public int getCount() {
        return 2;
    }
    
    public View getPageView(int position){
    	if(mPageViews.size() > position){
    		return mPageViews.get(position);
    	}
    	return null;
    }

    public Object instantiateItem(View collection, int position) {
    	
    	View view = mPageViews.get(position);

        ((DDGViewPager) collection).addView(view, 0);

        return view;
    	
//        LayoutInflater inflater = (LayoutInflater) collection.getContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        int resId = 0;
//        switch (position) {
//        case 0:
//            resId = R.layout.first_layout;
//            break;
//        case 1:
//            resId = R.layout.main;
//            break;
//        }
//
//        View view = inflater.inflate(resId, null);
//
//        ((DDGViewPager) collection).addView(view, 0);
//
//        return view;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((DDGViewPager) arg0).removeView((View) arg2);

    }


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }
    
    public float getPageWidth(int position)
    {
    if (position == 0)
        {
        return 0.806f;
        }
    return 1f;
    }
}