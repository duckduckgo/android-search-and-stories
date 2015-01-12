package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class RecentResultCursorAdapter extends CursorAdapter {

    public RecentResultCursorAdapter(Context context, Cursor c) {
        super(context, c);
        //super context, c, flags api 11
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View retView = inflater.inflate(R.layout.recentsearch_list_layout, parent, false);
        //View retView = inflater.inflate(R.layout.temp_search_layout, parent, false);
        View retView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        final String data = cursor.getString(cursor.getColumnIndex("data"));
        //final String type = cursor.getString(cursor.getColumnIndex("type"));

        TextView textViewTitle = (TextView) view.findViewById(android.R.id.text1);
        textViewTitle.setText(data);
    }
}
