package com.duckduckgo.mobile.android.adapters;
import com.duckduckgo.mobile.android.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryCursorAdapter extends CursorAdapter {

    public HistoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.recentsearch_list_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView textViewHistory = (TextView) view.findViewById(R.id.recentSearchText);
        textViewHistory.setText(cursor.getString(cursor.getColumnIndex("data")));
        
//        String extraType = cursor.getString(cursor.getColumnIndex("extraType"));
//        if(extraType.length() != 0) {
//          ImageView imageViewHistory = (ImageView) view.findViewById(R.id.recentSearchImage);
//        }
    }
}