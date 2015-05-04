package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;

public class RecentFeedListView extends ListView implements android.widget.AdapterView.OnItemClickListener{//}, android.widget.AdapterView.OnItemLongClickListener {

    public RecentFeedListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnItemClickListener(this);
        //this.setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "recent feed list view on click");
        Object item = getAdapter().getItem(position);
        //FeedObject obj = null;
        HistoryObject obj = null;
        Cursor c;
        if (item instanceof SQLiteCursor) {
            //obj = new FeedObject(((SQLiteCursor) item));
            c = (Cursor) item;
            obj = new HistoryObject(c);
        }

        if (obj != null) {
            Log.e("aaa", "object: "+obj.toString());
            BusProvider.getInstance().post(new HistoryItemSelectedEvent(obj));
            //Log.e("aaa", "obj!=null, "+obj.toString());
            //FeedObject feed = DDGApplication.getDB().selectFeedById(obj.getFeedId());
            /*
            if(feed==null) {
                Log.e("aaa", "feed == null");
            } else {
                Log.e("aaa", "feed!=null: "+feed.toString());
                BusProvider.getInstance().post(new MainFeedItemSelectedEvent(feed));
            }*/
            //BusProvider.getInstance().post(new MainFeedItemSelectedEvent(obj));
        }
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Log.e("aaa", "recent feed list view - on item long click");

        Object adapter = getAdapter();
        Cursor c = null;
        HistoryObject obj = null;

        Object itemClicked = ((Adapter) adapter).getItem(position);
        if(itemClicked instanceof Cursor) {
            c = (Cursor) itemClicked;
            obj = new HistoryObject(c);
        }

        if (obj != null) {
            Log.e("aaa", "history object: "+obj.toString());
            BusProvider.getInstance().post(new HistoryItemLongClickEvent(obj));
            return true;
        }

        return false;
        /*
        Object item = getAdapter().getItem(position);
        FeedObject obj = null;
        if (item instanceof SQLiteCursor) {
            //obj = new FeedObject(((SQLiteCursor) item));
        }

        if (obj != null) {
            //BusProvider.getInstance().post(new SavedFeedItemLongClickEvent(obj));
        }
        return true;*/
    }
}
