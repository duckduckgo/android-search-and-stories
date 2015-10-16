package com.duckduckgo.mobile.android.adapters;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;

public class FavoriteResultCursorAdapter extends CursorAdapter {
	
    public FavoriteResultCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.item_search, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

    	final String data = cursor.getString(cursor.getColumnIndex("title"));
        TextView title = (TextView) view.findViewById(R.id.item_text);
        if(title!=null ) {
            title.setText(capitalizeWords(data));
        }
        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        if(icon!=null) {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
        }

    }

    public String capitalizeWords(String input) {
        StringBuilder out = new StringBuilder();
        for(int i=0; i<input.length(); i++) {
            if(i==0 || (i>0 && input.charAt(i-1)==' ')) {
                out.append(input.substring(i, i+1).toUpperCase());
            } else {
                out.append(input.substring(i, i+1));
            }
        }
        return out.toString();
    }
}