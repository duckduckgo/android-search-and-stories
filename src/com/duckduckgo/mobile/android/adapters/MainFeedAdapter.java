package com.duckduckgo.mobile.android.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.DDGConstants;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainFeedAdapter extends ArrayAdapter<FeedObject> {
	private static final String TAG = "MainFeedAdapter";
	
	private final LayoutInflater inflater;
	
	//TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...
	protected final ImageDownloader imageDownloader;
	
	public MainFeedAdapter(Context context) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageDownloader = DDGApplication.getImageDownloader();
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
		URL iconUrl = null;
		URL feedUrl = null;
		if (feed != null) {
			//Download the background image
			if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
				imageDownloader.download(feed.getImageUrl(), holder.imageViewBackground);
			} else {
				imageDownloader.download(null, holder.imageViewBackground);
			}

			//Set the Title
			holder.textViewTitle.setText(feed.getTitle());
			
			//Set the URL Icon on the Left Hand Side
			if (feed.getUrl() != null && !feed.getUrl().equals("null")) {
				try {
					iconUrl = new URL(feed.getUrl());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				if (iconUrl != null) {
					String host = iconUrl.getHost();
					if (host.indexOf(".") != host.lastIndexOf(".")) {
						//Cut off the beginning, because we don't want/need it
						host = host.substring(host.indexOf(".")+1);
					}
					imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewUrlIcon);
				} else {
					imageDownloader.download(null, holder.imageViewUrlIcon);
				}
			} else {
				imageDownloader.download(null, holder.imageViewUrlIcon);
			}

			if (feed.getFeed() != null && !feed.getFeed().equals("null")) {
				try {
					feedUrl = new URL(feed.getFeed());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				if (feedUrl != null) {
					if (iconUrl != null) {
						if (iconUrl.getHost().equals(feedUrl.getHost())) {
							//They are the same, don't show it
							imageDownloader.download(null, holder.imageViewFeedIcon);
						} else {
							String host = feedUrl.getHost();
							if (host.indexOf(".") != host.lastIndexOf(".")) {
								//Cut off the beginning, because we don't want/need it
								host = host.substring(host.indexOf(".")+1);
							}							
							//They are different, so show it
							imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewFeedIcon);
						}
					} else {
						String host = feedUrl.getHost();
						if (host.indexOf(".") != host.lastIndexOf(".")) {
							//Cut off the beginning, because we don't want/need it
							host = host.substring(host.indexOf(".")+1);
						}
						imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewFeedIcon);
					}
				} else {
					imageDownloader.download(null, holder.imageViewFeedIcon);
				}
			} else {
				imageDownloader.download(null, holder.imageViewFeedIcon);
			}
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
			this.imageViewUrlIcon = imageViewUrlIcon;
		}
	}

	public void setList(List<FeedObject> feed) {
		this.clear();
		for (FeedObject next : feed) {
			this.add(next);
		}
	}

}
