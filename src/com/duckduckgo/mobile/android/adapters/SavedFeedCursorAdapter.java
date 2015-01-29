package com.duckduckgo.mobile.android.adapters;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.squareup.picasso.Picasso;

public class SavedFeedCursorAdapter extends CursorAdapter {
		
    public SavedFeedCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.temp_main_feed_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

    	final String feedId = cursor.getString(cursor.getColumnIndex("_id"));
    	final String title = cursor.getString(cursor.getColumnIndex("title"));
    	final String feedType = cursor.getString(cursor.getColumnIndex("type"));
    	final String imageUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
    	final String feedContent = cursor.getString(cursor.getColumnIndex("feed"));
        final String category = cursor.getString(cursor.getColumnIndex("category"));
    	
    	final TextView textViewTitle = (TextView) view.findViewById(R.id.feedTitleTextView);
        final TextView textViewCategory = (TextView) view.findViewById(R.id.feedCategoryTextView);
    	final AsyncImageView imageViewBackground = (AsyncImageView) view.findViewById(R.id.feedItemBackground);
    	final AsyncImageView imageViewFeedIcon = (AsyncImageView) view.findViewById(R.id.feedItemSourceIcon);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.feedWrapper);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.feed);
    			

    	URL feedUrl = null;

    	//Download the background image
    	if (imageUrl != null && !imageUrl.equals("null")) {
    		Picasso.with(context)
    		.load(imageUrl)
    		.resize(DDGUtils.displayStats.feedItemWidth, DDGUtils.displayStats.feedItemHeight)
		    .centerCrop()
    		.placeholder(android.R.color.transparent)
    		.into(imageViewBackground);
    	}

    	imageViewFeedIcon.setType(feedType);	// stored source id in imageview
//    	imageViewFeedIcon.setOnClickListener(sourceClickListener);

    	final View iconParent = (View) view.findViewById(R.id.feedWrapper);
    	iconParent.post(new Runnable() {
    		public void run() {
    			// Post in the parent's message queue to make sure the parent
    			// lays out its children before we call getHitRect()
    			Rect delegateArea = new Rect();
    			AsyncImageView delegate = imageViewFeedIcon;
    			delegate.getHitRect(delegateArea);
    			delegateArea.top = 0;
    			delegateArea.bottom = iconParent.getBottom();
    			delegateArea.left = 0;
    			// right side limit also considers the space that is available from TextView, without text displayed
    			// in TextView padding area on the left
    			delegateArea.right = textViewTitle.getLeft() + textViewTitle.getPaddingLeft();
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
    	textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.mainTextSize);
    	textViewTitle.setText(title);

        //Set the Category
        textViewCategory.setText(category.toUpperCase());

    	if(DDGControlVar.readArticles.contains(feedId)){
    		textViewTitle.setTextColor(Color.GRAY);
    	}

    	if (feedContent != null && !feedContent.equals("null")) {
    		try {
    			feedUrl = new URL(feedContent);
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
    				imageViewFeedIcon.setBitmap(bitmap);
    			}
    			else {
    				Picasso.with(context)
    				.load(DDGConstants.ICON_LOOKUP_URL + host + ".ico")
    				.placeholder(android.R.color.transparent)
    				.into(imageViewFeedIcon);
    			}
    		}
    	}
        final String url;
        if(feedUrl==null) {
            url = null;
        } else {
            url = feedUrl.toString();
        }

        toolbar.getMenu().findItem(R.id.action_add_favourites).setVisible(false);
        //aaa todo check why removing a feed inflate more items in other menus
        //aaaa solved!

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_remove_favourites:
                        BusProvider.getInstance().post(new UnSaveStoryEvent(feedId));
                        //toolbar.
                        return true;
                    case R.id.action_share:
                        if(url!=null) {
                            BusProvider.getInstance().post(new ShareFeedEvent(title, url));
                        }
                        return true;
                    case R.id.action_external:
                        if(url!=null) {
                            BusProvider.getInstance().post(new SendToExternalBrowserEvent(null, url));
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });


    }
}