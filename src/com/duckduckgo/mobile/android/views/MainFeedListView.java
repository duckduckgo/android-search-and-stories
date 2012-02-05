package com.duckduckgo.mobile.android.views;

import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainFeedListView extends ListView implements android.widget.AdapterView.OnItemClickListener {

	public MainFeedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FeedObject obj = (FeedObject) getAdapter().getItem(position);
		
		if (obj != null) {
			String url = obj.getUrl();
			if (url != null) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				view.getContext().startActivity(intent);
			}
		}
	}

}
