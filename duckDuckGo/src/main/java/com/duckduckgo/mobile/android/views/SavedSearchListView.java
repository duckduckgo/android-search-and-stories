package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;

public class SavedSearchListView extends ListView implements android.widget.AdapterView.OnItemLongClickListener {
	
	public SavedSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);		
		this.setOnItemLongClickListener(this);
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
			BusProvider.getInstance().post(new SavedSearchItemLongClickEvent(query));
		}
		
		return false;
	}

}
