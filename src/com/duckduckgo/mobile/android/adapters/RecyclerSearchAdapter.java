package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 6/19/15.
 */
public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "recycler_search_adapter";

    private static final int TYPE_DIVIDER = 0;
    private static final int TYPE_ITEM_RECENT = 1;
    private static final int TYPE_ITEM_FAVORITE = 2;

    private Context context;
    private LayoutInflater inflater;

    private Cursor recentCursor;
    private Cursor favoriteCursor;

    private RecyclerRecentSearchAdapter recentAdapter;
    private RecyclerFavoriteSearchAdapter favoriteAdapter;

    public RecyclerSearchAdapter(Context context, RecyclerRecentSearchAdapter recentAdapter, RecyclerFavoriteSearchAdapter favoriteAdapter) {
        this.context = context;
        this.recentAdapter = recentAdapter;
        this.favoriteAdapter = favoriteAdapter;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType) {
            case TYPE_ITEM_RECENT:
                return recentAdapter.onCreateViewHolder(parent, viewType);
            case TYPE_ITEM_FAVORITE:
                return favoriteAdapter.onCreateViewHolder(parent, viewType);
            case TYPE_DIVIDER:
                return new DividerHolder(inflater.inflate(R.layout.search_divider, parent, false));
            default:
                return new DividerHolder(inflater.inflate(R.layout.search_divider, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_ITEM_RECENT:
                recentAdapter.onBindViewHolder((RecyclerRecentSearchAdapter.ViewHolder) holder, position);
                break;
            case TYPE_ITEM_FAVORITE:
                int favoritePosition = position;
                if(recentAdapter!=null && recentAdapter.getItemCount()>0) {
                    favoritePosition = position - recentAdapter.getItemCount() - 1;
                }
                favoriteAdapter.onBindViewHolder((RecyclerFavoriteSearchAdapter.ViewHolder) holder, favoritePosition);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        boolean divider = false;
        if(recentAdapter!=null && recentAdapter.getItemCount()>0) {
            count += recentAdapter.getItemCount();
            divider = true;
        }
        if(favoriteAdapter!=null) {
            count += favoriteAdapter.getItemCount();
        } else {
            divider = false;
        }
        if(divider) {
            count += 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_ITEM_RECENT;
        if(recentAdapter!=null && recentAdapter.getItemCount()>0) {
            if(favoriteAdapter!=null && favoriteAdapter.getItemCount()>0) {
                if(position==recentAdapter.getItemCount()) {
                    type = TYPE_DIVIDER;
                } else if(position>recentAdapter.getItemCount()) {
                    type = TYPE_ITEM_FAVORITE;
                }
            }
        } else {
            type = TYPE_ITEM_FAVORITE;
        }
        return type;
    }
/*
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewSearch;
        public final ImageView imageViewIcon;

        public ViewHolder(View v) {
            super(v);
            this.textViewSearch = (TextView) v.findViewById(R.id.item_text);
            this.imageViewIcon = (ImageView) v.findViewById(R.id.item_icon);
        }
    }*/

    public class DividerHolder extends RecyclerView.ViewHolder {

        public DividerHolder(View v) {
            super(v);
        }
    }
}
