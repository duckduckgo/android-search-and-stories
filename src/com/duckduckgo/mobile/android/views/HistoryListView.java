package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.HistoryCursorAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public class HistoryListView extends ListView implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private OnHistoryItemSelectedListener listener;
	private OnHistoryItemLongClickListener listenerLongClick;
	
	// notification for "Save Recent Searches" feature awareness
	private View leftRecentHeaderView = null;
	
	public HistoryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		leftRecentHeaderView = LayoutInflater.from(context).inflate(R.layout.recentsearch_notrecording_layout, null, false);
		
		this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}
	
	public void setOnHistoryItemSelectedListener(OnHistoryItemSelectedListener listener) {
		this.listener = listener;
	}
	
	public void setOnHistoryItemLongClickListener(OnHistoryItemLongClickListener listener) {
		this.listenerLongClick = listener;
	}
	
	public void setOnHeaderClickListener(OnClickListener listener) {
		leftRecentHeaderView.setOnClickListener(listener);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object adapter = getAdapter();
		Cursor c = null;
		HistoryObject obj = null;
		
		Object itemClicked = ((Adapter) adapter).getItem(position);		
		if(itemClicked instanceof Cursor) {
			c = (Cursor) itemClicked;
			obj = new HistoryObject(c);
		}
		
		if (obj != null) {
			if (listener != null)
				listener.onHistoryItemSelected(obj);
		}
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		
		Object adapter = getAdapter();		
		Cursor c = null;
		HistoryObject obj = null;
		
		Object itemClicked = ((Adapter) adapter).getItem(position);		
		if(itemClicked instanceof Cursor) {
			c = (Cursor) itemClicked;
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
		public void onHistoryItemSelected(HistoryObject historyObject);
	}
	
	public interface OnHistoryItemLongClickListener {
		public void onHistoryItemLongClick(HistoryObject historyObject);
	}
	
	/**
	 * Displays "not recording" indicator in left-menu if Save Searches option is disabled  
	 */
	public void displayRecordHistoryDisabled() {
		if(PreferencesManager.getRecordHistory()) {
    		if(findViewById(leftRecentHeaderView.getId()) != null) {
        		removeHeaderView(leftRecentHeaderView);
    		}
    	}
    	else {
    		if(findViewById(leftRecentHeaderView.getId()) == null) {
    			ListAdapter currentAdapter = getAdapter();
    			setAdapter(null);
    			addHeaderView(leftRecentHeaderView);
    			setAdapter(currentAdapter);
    		}
    	}
	}

}
