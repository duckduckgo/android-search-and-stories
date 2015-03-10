package com.duckduckgo.mobile.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.SourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TempMainFeedAdapter extends RecyclerView.Adapter<TempMainFeedAdapter.ViewHolder> {
    private static final String TAG = "MainFeedAdapter";

    private Context context;
    private final LayoutInflater inflater;

    public View.OnClickListener sourceClickListener;
    public View.OnClickListener categoryClickListener;

    private SimpleDateFormat dateFormat;
    private Date lastFeedDate = null;

    private String markedItem = null;
    private String markedSource = null;
    private String markedCategory = null;

    private AlphaAnimation blinkanimation = null;

    private DDGOverflowMenu feedMenu = null;
    private Menu menu = null;

    public ArrayList<FeedObject> data;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewTitle;
        public final TextView textViewCategory;
        public final ImageButton imageButtonMenu;
        public final AsyncImageView imageViewBackground;
        public final AsyncImageView imageViewFeedIcon; // Bottom Left Icon (Feed Source)

        public ViewHolder(View v) {
            super(v);
            this.textViewTitle = (TextView) v.findViewById(R.id.feedTitleTextView);
            this.textViewCategory = (TextView) v.findViewById(R.id.feedCategoryTextView);
            this.imageButtonMenu = (ImageButton) v.findViewById(R.id.feedMenuButton);
            this.imageViewBackground = (AsyncImageView) v.findViewById(R.id.feedItemBackground);
            this.imageViewFeedIcon = (AsyncImageView) v.findViewById(R.id.feedItemSourceIcon);
        }
    }

    public TempMainFeedAdapter(Activity activity, Context context, View.OnClickListener sourceClickListener, View.OnClickListener categoryClickListener) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = new ArrayList<FeedObject>();

        this.sourceClickListener = sourceClickListener;
        this.categoryClickListener = categoryClickListener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // animation to use for blinking cue
        blinkanimation = new AlphaAnimation(1, 0.3f);
        blinkanimation.setDuration(300);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(2);
        blinkanimation.setRepeatMode(Animation.REVERSE);

        menu = new MenuBuilder(context);
        activity.getMenuInflater().inflate(R.menu.feed, menu);
        feedMenu = new DDGOverflowMenu(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.temp_main_feed_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FeedObject feed = data.get(position);

        URL feedUrl = null;

        if (feed != null) {

            final String feedId = feed.getId();

            //Download the background image

            if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
                Picasso.with(context)
                        .load(feed.getImageUrl())
                        .resize(DDGUtils.displayStats.feedItemWidth, DDGUtils.displayStats.feedItemHeight)
                        .centerCrop()
                        .placeholder(android.R.color.transparent)
                        .into(holder.imageViewBackground);
            }

            final String feedType = feed.getType();

            holder.imageViewFeedIcon.setType(feedType);	// stored source id in imageview

            //holder.imageViewFeedIcon.setOnClickListener(sourceClickListener);

            final View itemView = holder.itemView;
            final String sourceType = holder.imageViewFeedIcon.getType();

            holder.imageViewFeedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new SourceFilterEvent(itemView, sourceType, feed));
                }
            });

            /*
            holder.imageViewFeedIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DDGControlVar.targetSource!=null) {
                        DDGControlVar.targetSource=null;
                        unmarkSource();
                        getFilter().filter(feedType);
                    } else {
                        DDGControlVar.targetSource=feedType;
                        markSource(feedId);
                        getFilter().filter(feedType);
                    }
                }
            });*/
/*
            final View iconParent = (View) view.findViewById(R.id.feedWrapper);
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
*/
            //Set the Title
            //holder.textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.mainTextSize);
            holder.textViewTitle.setText(feed.getTitle());
			/*
			String feedId = feed.getId();

			if(DDGControlVar.readArticles.contains(feedId)){
				holder.textViewTitle.setTextColor(Color.GRAY);
			}*/

            // FIXME : it'd be good to reset color to default color for textview in layout XML
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title));
            if(DDGControlVar.readArticles.contains(feedId)){
                holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title_viewed));
            }

            //set the category
            //todo insert size
            final String category = feed.getCategory();
            holder.textViewCategory.setText(category.toUpperCase());/*
            holder.textViewCategory.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("aaa", "catgory clicked: "+category);
                }
            });*/
            //holder.textViewCategory.setOnClickListener(categoryClickListener);
            holder.textViewCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DDGControlVar.targetCategory!=null) {
                        DDGControlVar.targetCategory=null;
                        BusProvider.getInstance().post(new FeedCancelCategoryFilterEvent());
                    } else {
                        DDGControlVar.targetCategory = category;
                        filterCategory(category);
                    }
                }
            });

            /*
            final View view = cv;
            holder.textViewCategory.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DDGControlVar.targetCategory!=null) {
                        Log.e("aaa", "should remove filter: "+category);
                        DDGControlVar.targetCategory = null;
                        unmarkCategory();
                        //getFilter().filter(category);
                    } else if(DDGControlVar.targetSource!=null) {
                        Log.e("aaa", "must add filter: "+category);
                        //DDGControlVar.targetCategory = category;
                        //markCategory(feedId);
                        //getFilter().filter(category);

                    }
                    view.animate().setDuration(1000).alpha(0)
                }
            });*/

            holder.imageButtonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(feedMenu==null) {
                        feedMenu = new DDGOverflowMenu(context);
                    }
                    if(feedMenu.isShowing()) {
                        //feedMenu.dismiss();
                        return;
                    } else {
                        if(DDGApplication.getDB().isSaved(feedId)) {
                            menu.findItem(R.id.action_add_favorite).setVisible(false);
                            menu.findItem(R.id.action_remove_favorite).setVisible(true);
                        } else {
                            menu.findItem(R.id.action_add_favorite).setVisible(true);
                            menu.findItem(R.id.action_remove_favorite).setVisible(false);
                        }
                        feedMenu.setFeed(feed);
                        feedMenu.setMenu(menu);
                        feedMenu.showFeedMenu(holder.imageButtonMenu);
                    }
                }
            });

/*
			//set the toolbar Menu
            if(DDGApplication.getDB().isSaved(feedId)) {
                holder.toolbar.getMenu().findItem(R.id.action_add_favorite).setVisible(false);
                holder.toolbar.getMenu().findItem(R.id.action_remove_favorite).setVisible(true);
            } else {
                holder.toolbar.getMenu().findItem(R.id.action_add_favorite).setVisible(true);
                holder.toolbar.getMenu().findItem(R.id.action_remove_favorite).setVisible(false);
            }
			holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem menuItem) {
					switch(menuItem.getItemId()) {
						case R.id.action_add_favorite:
							Log.e("aaa", "action add favourites");
                            BusProvider.getInstance().post(new SaveStoryEvent(feed));
                            holder.toolbar.getMenu().findItem(R.id.action_add_favorite).setVisible(false);
                            holder.toolbar.getMenu().findItem(R.id.action_remove_favorite).setVisible(true);
							//add to favourites
							return true;
                        case R.id.action_remove_favorite:
                            BusProvider.getInstance().post(new UnSaveStoryEvent(feed.getId()));
                            holder.toolbar.getMenu().findItem(R.id.action_add_favorite).setVisible(true);
                            holder.toolbar.getMenu().findItem(R.id.action_remove_favorite).setVisible(false);
                            return true;
						case R.id.action_share:
							Log.e("aaa", "action share");
                            BusProvider.getInstance().post(new ShareFeedEvent(feed.getTitle(), feed.getUrl()));
							//action share
							return true;
						case R.id.action_external:
							Log.e("aaa", "action external view in chrome");
                            BusProvider.getInstance().post(new SendToExternalBrowserEvent(getContext(), feed.getUrl()));
							//view in chrome
							return true;
						default:
							return false;
					}
				}
			});*/

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
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BusProvider.getInstance().post(new MainFeedItemSelectedEvent(feed));
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //BusProvider.getInstance().post(new MainFeedItemLongClickEvent(feed));
                        if(feedMenu==null) {
                            feedMenu = new DDGOverflowMenu(context);
                        }
                        if(!feedMenu.isShowing()) {
                            if(DDGApplication.getDB().isSaved(feedId)) {
                                menu.findItem(R.id.action_add_favorite).setVisible(false);
                                menu.findItem(R.id.action_remove_favorite).setVisible(true);
                            } else {
                                menu.findItem(R.id.action_add_favorite).setVisible(true);
                                menu.findItem(R.id.action_remove_favorite).setVisible(false);
                            }
                            feedMenu.setFeed(feed);
                            feedMenu.setMenu(menu);
                            feedMenu.showFeedMenu(holder.imageButtonMenu);
                        }

                        return true;
                    }
                });

            }
        }


        if(holder.itemView != null) {
            if((markedItem != null && markedItem.equals(feed.getId())) || (markedSource!=null && markedSource.equals(feed.getId()))) {
                blinkanimation.reset();
                holder.itemView.startAnimation(blinkanimation);
            }
            else {
                holder.itemView.setAnimation(null);
            }
        }
    }

    public FeedObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<FeedObject> newData) {
        this.data = (ArrayList) newData;
        notifyDataSetChanged();
    }

    public void filterCategory(String category) {
        int i = data.size() -1;
        for(; i>=0; i--) {
            if(!data.get(i).getCategory().equals(category)) {
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void markSource(String itemId) {
        markedSource = itemId;
    }

    public void unmarkSource() {
        markedSource = null;
    }
}
