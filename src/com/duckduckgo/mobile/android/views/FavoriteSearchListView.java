package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.adapters.FavoriteResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;

public class FavoriteSearchListView extends ListView implements AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {
	
	public FavoriteSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object adapter = getAdapter();
        Cursor c = null;
        String query = null;

        if(adapter instanceof FavoriteResultCursorAdapter) {
            c = (Cursor) ((FavoriteResultCursorAdapter) adapter).getItem(position);
            query = c.getString(c.getColumnIndex("query"));
        }

        if(query!=null) {
            BusProvider.getInstance().post(new SavedSearchItemSelectedEvent(query));
        }
    }
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		
		Object adapter = getAdapter();		
		Cursor c = null;
		String query = null;
		
		if(adapter instanceof FavoriteResultCursorAdapter) {
			c = (Cursor) ((FavoriteResultCursorAdapter) adapter).getItem(position);
			query = c.getString(c.getColumnIndex("query"));
		}
		
		if (query != null) {
			BusProvider.getInstance().post(new SavedSearchItemLongClickEvent(query));
            return true;
		}
		
		return false;
	}

}
