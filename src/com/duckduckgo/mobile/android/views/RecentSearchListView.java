package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;

public class RecentSearchListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public RecentSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "recent search list view - on item click");
        Object adapter = getAdapter();
        Cursor c = null;
        HistoryObject obj = null;

        Object itemClicked = ((Adapter) adapter).getItem(position);
        if(itemClicked instanceof Cursor) {
            c = (Cursor) itemClicked;
            obj = new HistoryObject(c);
        }

        if (obj != null) {
            Log.e("aaa", "object: "+obj.toString());
            BusProvider.getInstance().post(new HistoryItemSelectedEvent(obj));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "recent search list view - on item long click");

        Object adapter = getAdapter();
        Cursor c = null;
        HistoryObject obj = null;

        Object itemClicked = ((Adapter) adapter).getItem(position);
        if(itemClicked instanceof Cursor) {
            c = (Cursor) itemClicked;
            obj = new HistoryObject(c);
        }

        if (obj != null) {
            Log.e("aaa", "history object: "+obj.toString());/*
            Menu menu = new MenuBuilder(getContext());
            ((Activity)getContext()).getMenuInflater().inflate(R.menu.feed, menu);
            if(DDGApplication.getDB().isSavedSearch(obj.getData())) {
                menu.findItem(R.id.action_add_favorite).setVisible(false);
                menu.findItem(R.id.action_remove_favorite).setVisible(true);
            } else {
                menu.findItem(R.id.action_add_favorite).setVisible(true);
                menu.findItem(R.id.action_remove_favorite).setVisible(false);
            }

            DDGDialogMenu dialog = new DDGDialogMenu();
            dialog.setMenu(menu);
            dialog.show(((ActionBarActivity)getContext()).getSupportFragmentManager(), DDGDialogMenu.TAG);
            */
            BusProvider.getInstance().post(new HistoryItemLongClickEvent(obj));
        }

        return true;
    }
}
