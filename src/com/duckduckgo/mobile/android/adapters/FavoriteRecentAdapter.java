package com.duckduckgo.mobile.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/18/17.
 */

public class FavoriteRecentAdapter extends RecyclerView.Adapter<FavoriteRecentAdapter.ViewHolder> {

    public static final String TAG = "favorite_recent_adapter";

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewSearch;
        public final ImageView imageViewIcon;
        public ViewHolder(View v) {
            super(v);
            this.textViewSearch = (TextView) v.findViewById(R.id.item_text);
            this.imageViewIcon = (ImageView) v.findViewById(R.id.item_icon);
        }
    }
    public class DividerViewHolder extends RecyclerView.ViewHolder {
        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
