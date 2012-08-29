package com.duckduckgo.mobile.android.views;

import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainFeedListView extends ListView implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private OnMainFeedItemSelectedListener listener;
	private OnMainFeedItemLongClickListener longClickListener;

	
	public MainFeedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}
	
	public void setOnMainFeedItemSelectedListener(OnMainFeedItemSelectedListener listener) {
		this.listener = listener;
	}

	public void setOnMainFeedItemLongClickListener(OnMainFeedItemLongClickListener longClickListener) {
		this.longClickListener = longClickListener;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FeedObject obj = (FeedObject) getAdapter().getItem(position);
		
		if (obj != null) {
			if (listener != null) {
				listener.onMainFeedItemSelected(obj);
			}
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		FeedObject obj = (FeedObject) getAdapter().getItem(position);

		if (obj != null) {
			if (longClickListener != null) {
				longClickListener.onMainFeedItemLongClick(obj);
			}
		}
		return true;
	}

	public interface OnMainFeedItemSelectedListener {
		public void onMainFeedItemSelected(FeedObject feedObject);
	}

	public interface OnMainFeedItemLongClickListener {
		public void onMainFeedItemLongClick(FeedObject feedObject);
	}

}
