package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;

public class RecentSearchListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public RecentSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "recent search list view - on item click");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "recent search list view - on item long click");

        Object adapter = getAdapter();
        Cursor c = null;
        String data = null;


        if(adapter instanceof RecentResultCursorAdapter) {
            c = (Cursor) ((RecentResultCursorAdapter) adapter).getItem(position);
            data = c.getString(c.getColumnIndex("data"));
        }

        if(data!=null) {
            Log.e("aaa", "recent search list view item long click!");
            //BusProvider.getInstance().post(new Rece);//todo event
            return true;
        }

        return false;
    }
}
