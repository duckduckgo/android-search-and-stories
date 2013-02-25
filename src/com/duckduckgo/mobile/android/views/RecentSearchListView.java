package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.duckduckgo.mobile.android.adapters.HistoryCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.objects.history.ParentHistoryObject;
import com.duckduckgo.mobile.android.objects.history.SavedResultHistoryObject;

public class RecentSearchListView extends ListView implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private OnHistoryItemSelectedListener listener;
	private OnHistoryItemLongClickListener listenerLongClick;
	
	public RecentSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}
	
	public void setOnHistoryItemSelectedListener(OnHistoryItemSelectedListener listener) {
		this.listener = listener;
	}
	
	public void setOnHistoryItemLongClickListener(OnHistoryItemLongClickListener listener) {
		this.listenerLongClick = listener;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object adapter = getAdapter();
		Cursor c = null;
		ParentHistoryObject obj = null;
		
		if(adapter instanceof HistoryCursorAdapter) {		
			c = (Cursor) ((HistoryCursorAdapter) adapter).getItem(position);
			obj = new HistoryObject(c);
		}
		else if(adapter instanceof SavedResultCursorAdapter) {
			c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);
			obj = new SavedResultHistoryObject(c);
		}
		
		if (obj != null) {
			if (listener != null) {
				listener.onHistoryItemSelected(obj);
			}
		}
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		
		Object adapter = getAdapter();		
		Cursor c = null;
		ParentHistoryObject obj = null;
		
		if(adapter instanceof HistoryCursorAdapter) {		
			c = (Cursor) ((HistoryCursorAdapter) adapter).getItem(position);
			obj = new HistoryObject(c);
		}
		else if(adapter instanceof SavedResultCursorAdapter) {
			c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);
			obj = new SavedResultHistoryObject(c);
		}
		// XXX Recent Search view has a header view, so we receive HeaderviewListAdapter
		else if(adapter instanceof HeaderViewListAdapter) {			
			c = (Cursor) ((HeaderViewListAdapter) adapter).getItem(position);
			obj = new HistoryObject(c);
		}
		
		if (obj != null) {
			if (listenerLongClick != null) {
				listenerLongClick.onHistoryItemLongClick(obj);
			}
		}
		
		return false;
	}
	
	public interface OnHistoryItemSelectedListener {
		public void onHistoryItemSelected(ParentHistoryObject historyObject);
	}
	
	public interface OnHistoryItemLongClickListener {
		public void onHistoryItemLongClick(ParentHistoryObject historyObject);
	}

}
