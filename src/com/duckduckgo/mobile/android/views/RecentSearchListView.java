package com.duckduckgo.mobile.android.views;

import com.duckduckgo.mobile.android.adapters.HistoryCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.objects.HistoryObject;
import com.duckduckgo.mobile.android.objects.SavedResultObject;

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
		Object adapter = getAdapter();
		
		if(adapter instanceof HistoryCursorAdapter) {		
			Cursor c = (Cursor) ((HistoryCursorAdapter) adapter).getItem(position);
			HistoryObject obj = new HistoryObject(c);
			
			if (obj != null) {
				if (listener != null) {
					listener.onHistoryItemSelected(obj);
				}
			}
		}
		else if(adapter instanceof SavedResultCursorAdapter) {
			Cursor c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);		
			SavedResultObject obj = new SavedResultObject(c);
			
			if (obj != null) {
				if (listener != null) {
					listener.onSavedResultSelected(obj);
				}
			}
		}
		
	}
	
	public interface OnHistoryItemSelectedListener {
		public void onHistoryItemSelected(HistoryObject historyObject);
		public void onSavedResultSelected(SavedResultObject savedResultObject);
	}

}
