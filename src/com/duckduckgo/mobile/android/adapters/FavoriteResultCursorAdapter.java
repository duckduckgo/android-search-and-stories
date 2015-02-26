package com.duckduckgo.mobile.android.adapters;
import android.content.Context;
import android.database.Cursor;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.pasteEvents.SavedSearchPasteEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;

public class FavoriteResultCursorAdapter extends CursorAdapter {
	
    public FavoriteResultCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View retView = inflater.inflate(R.layout.recentsearch_list_layout, parent, false);
        View retView = inflater.inflate(R.layout.temp_search_layout, parent, false);
        //View retView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

    	final String data = cursor.getString(cursor.getColumnIndex("query"));
    	/*
        TextView textViewHistory = (TextView) view.findViewById(R.id.recentSearchText);
        textViewHistory.setText(data);
        textViewHistory.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.recentTextSize);
        
        AsyncImageView imageViewHistory = (AsyncImageView) view.findViewById(R.id.recentSearchImage);
        imageViewHistory.setImageResource(R.drawable.icon_history_search);


        // query use button
        ImageView buttonHistory = (ImageView) view.findViewById(R.id.recentSearchPaste);
        buttonHistory.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View v) {
        		BusProvider.getInstance().post(new SavedSearchPasteEvent(data));
        	}
        });*/
        TextView title = (TextView) view.findViewById(R.id.item_text);
        //TextView textViewTitle = (TextView) view.findViewById(android.R.id.text1);
        if(title!=null ) {
            title.setText(capitalizeWords(data));
        }
        //title.setText(data.substring(0,1).toUpperCase()+data.substring(1));

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