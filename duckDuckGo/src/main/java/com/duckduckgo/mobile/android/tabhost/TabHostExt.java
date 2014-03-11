package com.duckduckgo.mobile.android.tabhost;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.fragment.SavedFeedTabFragment;
import com.duckduckgo.mobile.android.fragment.SavedResultTabFragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

public class TabHostExt extends FragmentTabHost {

    public TabHostExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabHostExt(Context context) {
        super(context);
    }
    
    public void addDefaultTabs(){
		addDefaultTab(getResources().getString(R.string.SavedSearches), SavedResultTabFragment.class);
		addDefaultTab(getResources().getString(R.string.SavedStories), SavedFeedTabFragment.class);
    }
    
	private void addDefaultTab(String label, Class<?> intentClass) {
		Intent intent = new Intent(getContext(), intentClass);
		TabHostExt.TabSpec spec = (TabHostExt.TabSpec) newTabSpec(label);

		View tabIndicator = LayoutInflater.from(getContext()).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(label);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		addTab(spec, intentClass, null);
	}

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        // leave it empty here. It looks that when you use hard keyboard,
        // this method will be called and the focus will be token.
    }
}