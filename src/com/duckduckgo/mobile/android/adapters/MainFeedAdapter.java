package com.duckduckgo.mobile.android.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;


public class MainFeedAdapter extends ArrayAdapter<FeedObject> {
	private static final String TAG = "MainFeedAdapter";
	
	private final LayoutInflater inflater;
	
	public boolean scrolling = false;
	
	public OnClickListener sourceClickListener;
	
	private int markedItem = -1;
	
	private AlphaAnimation blinkanimation = null;
	
	//TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...
	protected final ImageDownloader imageDownloader;
				
	public MainFeedAdapter(Context context, OnClickListener sourceClickListener) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageDownloader = DDGApplication.getImageDownloader();
		this.sourceClickListener = sourceClickListener;
		
		// animation to use for blinking cue
		blinkanimation = new AlphaAnimation(1, 0.3f);
		blinkanimation.setDuration(300);
		blinkanimation.setInterpolator(new LinearInterpolator());
		blinkanimation.setRepeatCount(2);
		blinkanimation.setRepeatMode(Animation.REVERSE);
	}
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		if (cv == null) {
			cv = inflater.inflate(R.layout.main_feed_layout, null);
			cv.setTag(new Holder((TextView)cv.findViewById(R.id.feedTitleTextView),
					             (AsyncImageView)cv.findViewById(R.id.feedItemBackground),
					             (AsyncImageView)cv.findViewById(R.id.feedItemSourceIcon)));
		}
		
		FeedObject feed = getItem(position);
		
		final Holder holder = (Holder) cv.getTag();
		URL feedUrl = null;

		if (feed != null) {			
			
			//Download the background image
			if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
				imageDownloader.download(feed.getImageUrl(), holder.imageViewBackground, scrolling);
			} else {
				imageDownloader.download(null, holder.imageViewBackground, scrolling);
			}
			
			String feedType = feed.getType();
			
			holder.imageViewFeedIcon.setType(feedType);	// stored source id in imageview
			holder.imageViewFeedIcon.setOnClickListener(sourceClickListener);
			
			final View iconParent = (View) cv.findViewById(R.id.feedWrapper);
			iconParent.post(new Runnable() {
	            public void run() {
	                // Post in the parent's message queue to make sure the parent
	                // lays out its children before we call getHitRect()
	                Rect delegateArea = new Rect();
	                AsyncImageView delegate = holder.imageViewFeedIcon;
	                delegate.getHitRect(delegateArea);
	                delegateArea.top = 0;
	                delegateArea.bottom = iconParent.getBottom();
	                delegateArea.left = 0;
	                // right side limit also considers the space that is available from TextView, without text displayed
	                // in TextView padding area on the left
	                delegateArea.right = holder.textViewTitle.getLeft() + holder.textViewTitle.getPaddingLeft();
	                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
	                        delegate);
	                // give the delegate to an ancestor of the view we're delegating the area to
	                if (View.class.isInstance(delegate.getParent())) {
	                    ((View) delegate.getParent())
	                            .setTouchDelegate(expandedArea);
	                }
	            };
	        });

			//Set the Title
			holder.textViewTitle.setText(feed.getTitle());
			
			String feedId = feed.getId();
			// FIXME : it'd be good to reset color to default color for textview in layout XML
			holder.textViewTitle.setTextColor(Color.WHITE);
			if(DDGControlVar.readArticles.contains(feedId)){
				holder.textViewTitle.setTextColor(Color.GRAY);
			}
			
			if (feed.getFeed() != null && !feed.getFeed().equals("null")) {
				try {
					feedUrl = new URL(feed.getFeed());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
				if (feedUrl != null) {
						String host = feedUrl.getHost();
						if (host.indexOf(".") != host.lastIndexOf(".")) {
							//Cut off the beginning, because we don't want/need it
							host = host.substring(host.indexOf(".")+1);
						}
						
						Bitmap bitmap = DDGApplication.getImageCache().getBitmapFromCache("DUCKDUCKICO--" + feedType, false);
						if(bitmap != null){
							holder.imageViewFeedIcon.setBitmap(bitmap);
						}
						else {
							imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewFeedIcon, scrolling);
						}
				} else {
					imageDownloader.download(null, holder.imageViewFeedIcon, scrolling);
				}
			} else {
				imageDownloader.download(null, holder.imageViewFeedIcon, scrolling);
			}
		}
		
		if(position == markedItem && cv != null) {			
			cv.startAnimation(blinkanimation);
			
			// only blink once
			unmark();
		}

		return cv;
	}
	

	public void setList(List<FeedObject> feed) {
		this.clear();
		for (FeedObject next : feed) {
			this.add(next);
		}
	}
	
	/**
	 * Mark a list item position to be blinked
	 * @param itemPos
	 */
	public void mark(int itemPos) {
		markedItem = itemPos;
	}
	
	public void unmark() {
		markedItem = -1;
	}

}
