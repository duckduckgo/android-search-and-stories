package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;

import java.util.ArrayList;

public class RecyclerFavoriteSearchAdapter extends RecyclerView.Adapter<RecyclerFavoriteSearchAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    public Cursor cursor;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewSearch;
        public final ImageView imageViewIcon;

        public ViewHolder(View v) {
            super(v);
            this.textViewSearch = (TextView) v.findViewById(R.id.item_text);
            this.imageViewIcon = (ImageView) v.findViewById(R.id.item_icon);
        }
    }

    public RecyclerFavoriteSearchAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View v = inflater.inflate(R.layout.temp_search_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        final String data = cursor.getString(cursor.getColumnIndex("query"));

        holder.textViewSearch.setText(capitalizeWords(data));
        holder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new SavedSearchItemSelectedEvent(data));
                Log.e("aaa", "favorite search on click: "+data);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //BusProvider.getInstance().post(new SavedSearchItemLongClickEvent(data));
                Log.e("aaa", "favorite search on long click: "+data);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if(oldCursor!=null) {
            oldCursor.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if(cursor==newCursor) {
            return null;
        }
        Cursor oldCursor = cursor;
        cursor = newCursor;
        if(cursor!=null) {
            notifyDataSetChanged();
        }
        return oldCursor;
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
