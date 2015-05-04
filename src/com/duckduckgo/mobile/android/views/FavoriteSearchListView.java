package com.duckduckgo.mobile.android.views;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
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
            DDGOverflowMenu menuFeed = new DDGOverflowMenu(getContext());
            Menu menu = new MenuBuilder(getContext());
            ((DuckDuckGo) getContext()).getMenuInflater().inflate(R.menu.feed, menu);
            menu.findItem(R.id.action_add_favorite).setVisible(false);
            menuFeed.setMenu(menu);
            //menu.show(view);
            //menuFeed.show(view);

            ////menuFeed.showBelowAnchor(view);

            //menuFeed.showCentered(view);

            //DDGDialogMenu dialogMenu = new DDGDialogMenu();
            //dialogMenu.setMenu(menu);
            //dialogMenu.show();
            return true;
		}
		
		return false;
	}

}
