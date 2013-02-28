package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;

public class SavedSearchListView extends ListView implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private OnSavedSearchItemSelectedListener listener;
	private OnSavedSearchItemLongClickListener listenerLongClick;
	
	public SavedSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}
	
	public void setOnSavedSearchItemSelectedListener(OnSavedSearchItemSelectedListener listener) {
		this.listener = listener;
	}
	
	public void setOnSavedSearchItemLongClickListener(OnSavedSearchItemLongClickListener listener) {
		this.listenerLongClick = listener;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object adapter = getAdapter();
		Cursor c = null;
		
		if(adapter instanceof SavedResultCursorAdapter) {
			c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);
			String query = c.getString(c.getColumnIndex("query"));
			if (listener != null)
				listener.onSavedSearchItemSelected(query);
		}
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		
		Object adapter = getAdapter();		
		Cursor c = null;
		String query = null;
		
		if(adapter instanceof SavedResultCursorAdapter) {
			c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);
			query = c.getString(c.getColumnIndex("query"));
		}
		
		if (query != null) {
			if (listenerLongClick != null) {
				listenerLongClick.onSavedSearchItemLongClick(query);
			}
		}
		
		return false;
	}
	
	public interface OnSavedSearchItemSelectedListener {
		public void onSavedSearchItemSelected(String query);
	}
	
	public interface OnSavedSearchItemLongClickListener {
		public void onSavedSearchItemLongClick(String query);
	}

}
