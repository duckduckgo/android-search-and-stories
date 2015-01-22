package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;

public class RecentFeedCursorAdapter extends CursorAdapter {

    public RecentFeedCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View retView = inflater.inflate(R.layout.recentsearch_list_layout, parent, false);
        //View retView = inflater.inflate(R.layout.temp_search_layout, parent, false);
        //View retView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        View retView = inflater.inflate(R.layout.temp_main_feed_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final String id = cursor.getString(cursor.getColumnIndex("_id"));
        final String type = cursor.getString(cursor.getColumnIndex("type"));
        final String data = cursor.getString(cursor.getColumnIndex("data"));
        final String url = cursor.getString(cursor.getColumnIndex("url"));
        final String extraType = cursor.getString(cursor.getColumnIndex("extraType"));
        final String feedId = cursor.getString(cursor.getColumnIndex("feedId"));

        final TextView textViewTitle = (TextView) view.findViewById(R.id.feedTitleTextView);
        final TextView textViewCategory = (TextView) view.findViewById(R.id.feedCategotyTextView);
        final AsyncImageView imageViewBackground = (AsyncImageView) view.findViewById(R.id.feedItemBackground);
        final AsyncImageView imageViewFeedIcon = (AsyncImageView) view.findViewById(R.id.feedItemSourceIcon);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.feedWrapper);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.feed);

        //if(DDGApplication.getDB().)

        textViewTitle.setText(data);
        textViewTitle.setTextColor(Color.BLACK);

        //TextView text = (TextView) view.findViewById(android.R.id.text1);
        //text.setText("id: "+id+" - type: "+type+" - data: "+data+" - url: "+url+" - extraType: "+extraType+" - feedId: "+feedId);

    }
}
