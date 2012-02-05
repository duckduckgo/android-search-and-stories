package com.duckduckgo.mobile.android.adapters;

import java.util.List;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFeedAdapter extends ArrayAdapter<FeedObject> {
	private final LayoutInflater inflater;
	
	public MainFeedAdapter(Context context) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		if (cv == null) {
			cv = inflater.inflate(R.layout.main_feed_layout, null);
			cv.setTag(new Holder((TextView)cv.findViewById(R.id.feedTitleTextView), (ImageView)cv.findViewById(R.id.feedItemBackground)));
		}
		
		FeedObject feed = getItem(position);
		
		final Holder holder = (Holder) cv.getTag();
		
		if (feed != null) {
			holder.textViewTitle.setText(feed.getTitle());
			//holder.imageViewBackground.setBackgroundResource(R.drawable.testbackground);
		}
		
		return cv;
	}
	
	class Holder {
		final TextView textViewTitle;
		final ImageView imageViewBackground;
		
		public Holder(final TextView textViewTitle, final ImageView imageViewBackground) {
			this.textViewTitle = textViewTitle;
			this.imageViewBackground = imageViewBackground;
		}
	}

	public void setList(List<FeedObject> feed) {
		this.clear();
		for (FeedObject next : feed) {
			this.add(next);
		}
	}

}
