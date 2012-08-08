package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

public class RecentSearchListView extends ListView implements android.widget.AdapterView.OnItemClickListener {

	private OnRecentSearchItemSelectedListener listener;
	
	public RecentSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
	}
	
	public void setOnRecentSearchItemSelectedListener(OnRecentSearchItemSelectedListener listener) {
		this.listener = listener;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String obj = (String) getAdapter().getItem(position);
		
		if (obj != null) {
			if (listener != null) {
				listener.onRecentSearchItemSelected(obj);
			}
		}
	}
	
	public interface OnRecentSearchItemSelectedListener {
		public void onRecentSearchItemSelected(String recentQuery);
	}

}
