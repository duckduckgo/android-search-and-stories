package com.duckduckgo.mobile.android.views;

import com.duckduckgo.mobile.android.objects.HistoryObject;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

public class RecentSearchListView extends ListView implements android.widget.AdapterView.OnItemClickListener {

	private OnHistoryItemSelectedListener listener;
	
	public RecentSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
	}
	
	public void setOnHistoryItemSelectedListener(OnHistoryItemSelectedListener listener) {
		this.listener = listener;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Cursor c = (Cursor) getAdapter().getItem(position);
		HistoryObject obj = new HistoryObject(c);
		
		if (obj != null) {
			if (listener != null) {
				listener.onHistoryItemSelected(obj);
			}
		}
	}
	
	public interface OnHistoryItemSelectedListener {
		public void onHistoryItemSelected(HistoryObject historyObject);
	}

}
