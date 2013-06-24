package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.squareup.picasso.Picasso;

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
		Object item = getAdapter().getItem(position);
		FeedObject obj = null;
		if(item instanceof FeedObject) {
			obj = (FeedObject) item;
		}
		else if(item instanceof SQLiteCursor) {
			obj = new FeedObject(((SQLiteCursor) item));
		}
		
		if (obj != null) {
			if (listener != null) {
				listener.onMainFeedItemSelected(obj);
			}
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Object item = getAdapter().getItem(position);
		FeedObject obj = null;
		if(item instanceof FeedObject) {
			obj = (FeedObject) item;
		}
		else if(item instanceof SQLiteCursor) {
			obj = new FeedObject(((SQLiteCursor) item));
		}

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

	public void setSelectionById(String id) {
		int itemCount = getCount();
		for(int i=0; i < itemCount ; ++i) {
			if(((FeedObject) getItemAtPosition(i)).getId().equals(id)) {
				setSelection(i);
				break;
			}
		}
	}
	
	/**
	 * Find the item given by its ID and return its position in the list
	 * @param id item ID
	 * @return item position in the list
	 */
	public int getSelectionPosById(String id) {
		int headerViewCount = getHeaderViewsCount();
		int itemCount = getCount() - headerViewCount;
		for(int i=headerViewCount; i < itemCount ; ++i) {
			if(((FeedObject) getItemAtPosition(i)).getId().equals(id)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void cleanImageTasks() {
		int count = getCount();
		for(int i=0;i<count;i++) {
			View v = getChildAt(i);
			if(v != null) {
				AsyncImageView iv = (AsyncImageView) v.findViewById(R.id.feedItemBackground);
				Picasso.with(getContext()).cancelRequest(iv);
			}
		}
	}
}
