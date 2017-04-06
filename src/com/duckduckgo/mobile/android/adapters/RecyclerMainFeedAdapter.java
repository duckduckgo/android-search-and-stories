package com.duckduckgo.mobile.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.SourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.duckduckgo.mobile.android.views.PageIndicator;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecyclerMainFeedAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "MainFeedAdapter";

    private static final int ITEM_TYPE_FEED = 0;
    private static final int ITEM_TYPE_HEADER = 1;

    private Context context;
    private final LayoutInflater inflater;
    private FragmentManager fragmentManager;

    private DDGOverflowMenu feedMenu = null;
    private Menu menu = null;

    public List<FeedObject> data;

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewTitle;
        public final FrameLayout frameCategoryContainer;
        public final TextView textViewCategory;
        public final FrameLayout frameMenuContainer;
        public final ImageView imageViewMenu;
        public final AsyncImageView imageViewBackground;
        public final AsyncImageView imageViewFeedIcon; // Bottom Left Icon (Feed Source)

        public FeedViewHolder(View v) {
            super(v);
            this.textViewTitle = (TextView) v.findViewById(R.id.feedTitleTextView);
            this.frameCategoryContainer = (FrameLayout) v.findViewById(R.id.feedCategoryContainer);
            this.textViewCategory = (TextView) v.findViewById(R.id.feedCategoryTextView);
            this.frameMenuContainer = (FrameLayout) v.findViewById(R.id.feedMenuContainer);
            this.imageViewMenu = (ImageView) v.findViewById(R.id.feedMenuImage);
            this.imageViewBackground = (AsyncImageView) v.findViewById(R.id.feedItemBackground);
            this.imageViewFeedIcon = (AsyncImageView) v.findViewById(R.id.feedItemSourceIcon);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public final ViewPager viewPager;
        public final FragmentPagerAdapter adapter;
        public final PageIndicator pageIndicator;
        public final Button button;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.instruction_button);
            viewPager = (ViewPager) itemView.findViewById(R.id.view_pager);
            adapter = new OnboardingAdapter(fragmentManager, true);
            viewPager.setAdapter(adapter);
            pageIndicator = (PageIndicator) itemView.findViewById(R.id.page_indicator);
            pageIndicator.setViewPager(viewPager);
            pageIndicator.setVisibility(View.GONE);
        }
    }

    public RecyclerMainFeedAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = new ArrayList<>();

        menu = new MenuBuilder(context);
        ((Activity)context).getMenuInflater().inflate(R.menu.feed, menu);
        feedMenu = new DDGOverflowMenu(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE_HEADER) return new HeaderViewHolder(inflater.inflate(R.layout.viewholder_feed_header, parent, false));
        return new FeedViewHolder(inflater.inflate(R.layout.item_main_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position == 0) {
            //that's the header;
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Use DuckDucko")
                            .setMessage("to do")
                            .setPositiveButton("Done", null);
                    builder.create().show();
                }
            });
            return;
        }
        final FeedObject feed = data.get(position);

        final FeedViewHolder feedHolder = (FeedViewHolder) holder;

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
                        .into(feedHolder.imageViewBackground);
            }

            final String feedType = feed.getType();

            feedHolder.imageViewFeedIcon.setType(feedType);	// stored source id in imageview

            final View itemView = feedHolder.itemView;
            final String sourceType = feedHolder.imageViewFeedIcon.getType();

            feedHolder.imageViewFeedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DDGControlVar.targetSource!=null) {
                        DDGControlVar.targetSource=null;
                        BusProvider.getInstance().post(new FeedCancelSourceFilterEvent());
                    } else {
                        DDGControlVar.targetSource = sourceType;
                        DDGControlVar.hasUpdatedFeed = false;
                        BusProvider.getInstance().post(new SourceFilterEvent(itemView, sourceType, feed));
                        filterSource(sourceType);
                    }
                }
            });
            //Set the Title
            feedHolder.textViewTitle.setText(feed.getTitle());

            // FIXME : it'd be good to reset color to default color for textview in layout XML
            feedHolder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title));
            if(DDGControlVar.readArticles.contains(feedId)){
                feedHolder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title_viewed));
            }

            //set the category
            //todo insert size
            final String category = feed.getCategory();
            feedHolder.textViewCategory.setText(category.toUpperCase());
            feedHolder.frameCategoryContainer.setOnClickListener(new View.OnClickListener() {
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

            feedHolder.frameMenuContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(feedHolder.imageViewMenu, feed);
                }
            });

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
                        feedHolder.imageViewFeedIcon.setBitmap(bitmap);
                    }
                }

                feedHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BusProvider.getInstance().post(new MainFeedItemSelectedEvent(feed));
                    }
                });

                feedHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showMenu(feedHolder.imageViewMenu, feed);
                        return true;
                    }
                });

            }
        }
    }

    public FeedObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size() + 1; //header
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return ITEM_TYPE_HEADER;
        return ITEM_TYPE_FEED;
        //return super.getItemViewType(position);
    }

    private void showMenu(View anchor, FeedObject feed) {
        if(feedMenu==null) {
            feedMenu = new DDGOverflowMenu(context);
        }
        if(!feedMenu.isShowing()) {
            if(DDGApplication.getDB().isSaved(feed.getId())) {
                menu.findItem(R.id.action_add_favorite).setVisible(false);
                menu.findItem(R.id.action_remove_favorite).setVisible(true);
            } else {
                menu.findItem(R.id.action_add_favorite).setVisible(true);
                menu.findItem(R.id.action_remove_favorite).setVisible(false);
            }
            feedMenu.setFeed(feed);
            feedMenu.setMenu(menu);
            feedMenu.showFeedMenu(anchor);
        }

    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void addData(List<FeedObject> newData) {
        this.data = (ArrayList) newData;
        notifyDataSetChanged();
    }

    public void addSourceData(List<FeedObject> newSources) {
        for(FeedObject newFeed : newSources) {
            boolean isPresent = false;
            for(FeedObject feed : data) {
                if(feed.getId().equals(newFeed.getId())) {
                    isPresent = true;
                }
            }
            if(!isPresent) {
                data.add(newFeed);
                notifyItemInserted(data.size());
            }
        }
    }

    public void filterCategory(String category) {
        int i = data.size() - 1;
        for(; i>=0; i--) {
            if(!data.get(i).getCategory().equals(category)) {
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    private void filterSource(String source) {
        int i = data.size() - 1;
        for(; i>=0; i--) {
            if(!data.get(i).getType().equals(source)) {
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }
}
