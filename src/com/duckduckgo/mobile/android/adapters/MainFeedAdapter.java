package com.duckduckgo.mobile.android.adapters;

import java.util.List;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFeedAdapter extends ArrayAdapter<FeedObject> {
	private static final String TAG = "MainFeedAdapter";
	
	private final LayoutInflater inflater;
	
	//TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...
	protected final ImageDownloader imageDownloader = new ImageDownloader(new ImageCache());
	
	public MainFeedAdapter(Context context) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		if (cv == null) {
			cv = inflater.inflate(R.layout.main_feed_layout, null);
			cv.setTag(new Holder((TextView)cv.findViewById(R.id.feedTitleTextView),
					             (AsyncImageView)cv.findViewById(R.id.feedItemBackground),
					             (AsyncImageView)cv.findViewById(R.id.feedItemSourceIcon),
					             (AsyncImageView)cv.findViewById(R.id.feedItemUrlIcon)));
		}
		
		FeedObject feed = getItem(position);
		
		final Holder holder = (Holder) cv.getTag();
		
		if (feed != null) {
			holder.textViewTitle.setText(feed.getTitle());
			if (feed.getFavicon() != null && !feed.getFavicon().equals("null")) {
				//attempt to show based on favicon
				imageDownloader.download(feed.getFavicon(), holder.imageViewFeedIcon);
			} else if (feed.getFeed() != null && !feed.getFeed().equals("null")) {
				//TODO: attempt to show based on feed url lookup
				imageDownloader.download(null, holder.imageViewFeedIcon);
			} else {
				imageDownloader.download(null, holder.imageViewFeedIcon);
			}
			
			if (feed.getUrl() != null && !feed.getUrl().equals("null")) {
				//TODO: Get the icon from the url and then use it to display...
				imageDownloader.download(null, holder.imageViewUrlIcon);
			} else {
				imageDownloader.download(null, holder.imageViewUrlIcon);
			}
			
			//TODO: Feed object needs to have a background image url to tell us, or we can't go there
			//TODO: For now, implement the iPhone version of grabbing a random image
			//holder.imageViewBackground.setBackgroundResource(R.drawable.testbackground);
		}
		
		return cv;
	}
	
	class Holder {
		final TextView textViewTitle;
		final AsyncImageView imageViewBackground;
		final AsyncImageView imageViewFeedIcon; //Top Right Icon (Feed Source)
		final AsyncImageView imageViewUrlIcon; //Bottom Left Icon (Icon for linked page)
		
		public Holder(final TextView textViewTitle, final AsyncImageView imageViewBackground,
					  final AsyncImageView imageViewFeedIcon, final AsyncImageView imageViewUrlIcon) {
			this.textViewTitle = textViewTitle;
			this.imageViewBackground = imageViewBackground;
			this.imageViewFeedIcon = imageViewFeedIcon;
			this.imageViewFeedIcon.setShouldHideOnDefault(true);
			this.imageViewUrlIcon = imageViewUrlIcon;
			this.imageViewUrlIcon.setShouldHideOnDefault(true);
		}
	}

	public void setList(List<FeedObject> feed) {
		this.clear();
		for (FeedObject next : feed) {
			this.add(next);
		}
	}

}
