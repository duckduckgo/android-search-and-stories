package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class FavoriteFeedListView extends ListView implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

    public FavoriteFeedListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "favorite feed list view on click");
        Object item = getAdapter().getItem(position);
        FeedObject obj = null;
        if (item instanceof SQLiteCursor) {
            obj = new FeedObject(((SQLiteCursor) item));
        }

        if (obj != null) {
            Log.e("aaa", "obj: "+obj.toString());
            BusProvider.getInstance().post(new MainFeedItemSelectedEvent(obj));
        }
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = getAdapter().getItem(position);
        FeedObject obj = null;
        if (item instanceof SQLiteCursor) {
            obj = new FeedObject(((SQLiteCursor) item));
        }

        if (obj != null) {
            BusProvider.getInstance().post(new SavedFeedItemLongClickEvent(obj));
        }
        return true;
    }
}
