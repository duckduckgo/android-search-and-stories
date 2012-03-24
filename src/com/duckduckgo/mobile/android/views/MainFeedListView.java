package com.duckduckgo.mobile.android.views;

import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainFeedListView extends ListView implements android.widget.AdapterView.OnItemClickListener {

	private OnMainFeedItemSelectedListener listener;
	
	public MainFeedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
	}
	
	public void setOnMainFeedItemSelectedListener(OnMainFeedItemSelectedListener listener) {
		this.listener = listener;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FeedObject obj = (FeedObject) getAdapter().getItem(position);
		
		if (obj != null) {
			if (listener != null) {
				listener.onMainFeedItemSelected(obj);
			}
		}
	}
	
	public interface OnMainFeedItemSelectedListener {
		public void onMainFeedItemSelected(FeedObject feedObject);
	}

}
