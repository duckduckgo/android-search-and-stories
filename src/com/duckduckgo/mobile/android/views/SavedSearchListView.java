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
			String title = c.getString(c.getColumnIndex("title"));
			String url = c.getString(c.getColumnIndex("url"));
			if (listener != null)
				listener.onSavedSearchItemSelected(query, title, url);
		}
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		
		Object adapter = getAdapter();		
		Cursor c = null;
		String query = null;
		String title = null, url = null;
		
		if(adapter instanceof SavedResultCursorAdapter) {
			c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);
			query = c.getString(c.getColumnIndex("query"));
			title = c.getString(c.getColumnIndex("title"));
			url = c.getString(c.getColumnIndex("url"));
		}
		
		if (listenerLongClick != null) {
				listenerLongClick.onSavedSearchItemLongClick(query, title, url);
		}
		
		return false;
	}
	
	public interface OnSavedSearchItemSelectedListener {
		public void onSavedSearchItemSelected(String query, String title, String url);
	}
	
	public interface OnSavedSearchItemLongClickListener {
		public void onSavedSearchItemLongClick(String query, String title, String url);
	}

}
