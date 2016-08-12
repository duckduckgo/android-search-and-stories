package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.adapters.SearchAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;

public class SearchListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    int recentLimit = 0;

    public SearchListView(Context context) {
        super(context);
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    public SearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    public void setLimit(int recentLimit) {
        this.recentLimit = recentLimit;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < recentLimit) {
            Object adapter = getAdapter();
            Cursor c = null;
            HistoryObject obj = null;

            Object itemClicked = ((Adapter) adapter).getItem(position);
            if (itemClicked instanceof Cursor) {
                c = (Cursor) itemClicked;
                obj = new HistoryObject(c);
            }

            if (obj != null) {
                BusProvider.getInstance().post(new HistoryItemSelectedEvent(obj));
            }
        } else {
            Object adapter = getAdapter();
            Cursor c = null;
            String query = null;

            if (adapter instanceof SearchAdapter) {
                c = (Cursor) ((SearchAdapter) adapter).getItem(position);
                query = c.getString(c.getColumnIndex("query"));
            }

            if (query != null) {
                BusProvider.getInstance().post(new SavedSearchItemSelectedEvent(query));
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(position<recentLimit) {
            Object adapter = getAdapter();
            Cursor c = null;
            HistoryObject obj = null;

            Object itemClicked = ((Adapter) adapter).getItem(position);
            if(itemClicked instanceof Cursor) {
                c = (Cursor) itemClicked;
                obj = new HistoryObject(c);
            }

            if (obj != null) {
                BusProvider.getInstance().post(new HistoryItemLongClickEvent(obj));
                return true;
            }
        } else {
            Object adapter = getAdapter();
            Cursor c = null;
            String query = null;

            if(adapter instanceof SearchAdapter) {
                c = (Cursor) ((SearchAdapter) adapter).getItem(position);
                query = c.getString(c.getColumnIndex("query"));
            }

            if (query != null) {
                BusProvider.getInstance().post(new SavedSearchItemLongClickEvent(query));
                return true;
            }
        }
        return false;
    }

}
