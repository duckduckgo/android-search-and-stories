package com.duckduckgo.mobile.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.adapters.RecyclerMainFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.events.RequestKeepFeedUpdatedEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.RequestSyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.SourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCleanImageTaskEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.CacheFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;
import com.duckduckgo.mobile.android.util.ReadArticlesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	public static final String TAG = "feed_fragment";

    private Activity activity = null;

    private RecyclerView recyclerView = null;
	//private MainFeedListView feedView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
	private View fragmentView;

    private RecyclerMainFeedAdapter recyclerAdapter = null;
	//private MainFeedAdapter feedAdapter = null;
	private MainFeedTask mainFeedTask = null;

    private RecyclerView.LayoutManager layoutManager;

	// for keeping filter source at same position
	public String source_m_objectId = null;
	public int source_m_itemHeight;
	public int source_m_yOffset;

    // for keeping filter category at same position
    public String category_m_objectId = null;
    public int category_m_itemHeight;
    public int category_m_yOffset;

    private Menu feedMenu = null;
    private DDGOverflowMenu overflowMenu = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentView =  inflater.inflate(R.layout.fragment_feed, container, false);
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
        //setHasOptionsMenu(true);
        activity = getActivity();
		init();
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            keepFeedUpdated();
        }
    }

	@Override
	public void onResume() {
		super.onResume();

		// lock button etc. can cause MainFeedTask results to be useless for the Activity
		// which is restarted (onPostExecute becomes invalid for the new Activity instance)
		// ensure we refresh in such cases

		keepFeedUpdated();
	}

	@Override
	public void onPause() {
		super.onPause();
        swipeRefreshLayout.setRefreshing(false);
		if (mainFeedTask != null) {
			mainFeedTask.cancel(false);
			mainFeedTask = null;
		}
	}
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_stories).setEnabled(false);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // refresh the list
        DDGControlVar.hasUpdatedFeed = false;
        keepFeedUpdated();
    }

	public void init() {

		//SourceClickListener sourceClickListener = new SourceClickListener();
        //CategoryClickListener categoryClickListener = new CategoryClickListener();
		//feedAdapter = new MainFeedAdapter(activity, sourceClickListener, categoryClickListener);
        //recyclerAdapter = new RecyclerMainFeedAdapter(activity, sourceClickListener, categoryClickListener);
        recyclerAdapter = new RecyclerMainFeedAdapter(activity);

		mainFeedTask = null;

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.feed_list_view);

        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);


        feedMenu = new MenuBuilder(activity);
        activity.getMenuInflater().inflate(R.menu.main, feedMenu);

	}
/*
    class SourceClickListener implements View.OnClickListener {
		public void onClick(View v) {
			// source filtering

			if(DDGControlVar.targetSource != null){
				cancelSourceFilter();
			}
			else {

				View itemParent = (View) v.getParent().getParent();
				int pos = feedView.getPositionForView(itemParent);
				source_m_objectId = ((FeedObject) feedView.getItemAtPosition(pos)).getId();
                FeedObject feedObject = (FeedObject) feedView.getItemAtPosition(pos);
				source_m_itemHeight = itemParent.getHeight();

				Rect r = new Rect();
				Point offset = new Point();
				feedView.getChildVisibleRect(itemParent, r, offset);
				source_m_yOffset = offset.y;

				String sourceType = ((AsyncImageView) v).getType();
				DDGControlVar.targetSource = sourceType;

				DDGControlVar.hasUpdatedFeed = false;
				keepFeedUpdated();
			}

		}
	}*/
/*
    class CategoryClickListener implements View.OnClickListener {
        public void onClick(final View v) {
            // category filtering

            if(DDGControlVar.targetCategory != null){
                cancelCategoryFilter();
            }
            else {

                final View itemParent = (View) v.getParent();
                int pos = feedView.getPositionForView(itemParent);
                category_m_objectId = ((FeedObject) feedView.getItemAtPosition(pos)).getId();
                FeedObject feedObject = (FeedObject) feedView.getItemAtPosition(pos);
                category_m_itemHeight = itemParent.getHeight();

                Rect r = new Rect();
                Point offset = new Point();
                feedView.getChildVisibleRect(itemParent, r, offset);
                category_m_yOffset = offset.y;

                DDGControlVar.targetCategory = feedObject.getCategory();

                for(int i=0; i<feedAdapter.getCount();i++) {
                    FeedObject feed = feedAdapter.getItem(i);
                    if(feed.getCategory().equals(DDGControlVar.targetCategory)) {
                        //View view = feedAdapter.get

                    }
                }


                DDGControlVar.hasUpdatedFeed = false;
                keepFeedUpdated();
            }

        }
    }*/

    public void feedItemSelected(FeedObject feedObject) {
        if(ReadArticlesManager.addReadArticle(feedObject)){
            recyclerAdapter.notifyDataSetChanged();
        }
    }

	/**
	 * Cancels source filter applied with source icon click from feed item
	 */
	public void cancelSourceFilter() {
		DDGControlVar.targetSource = null;
		DDGControlVar.hasUpdatedFeed = false;
		//feedAdapter.unmark();
		keepFeedUpdated();
	}

    /**
     * Cancels target filter applied with source icon click from feed item
     */
    public void cancelCategoryFilter() {
        DDGControlVar.targetCategory = null;
        DDGControlVar.hasUpdatedFeed = false;
        //feedAdapter.unmarkCategory();
        keepFeedUpdated();
    }

	/**
	 * Refresh feed if it's not marked as updated
	 */
	@SuppressLint("NewApi")
	public void keepFeedUpdated(){
		//if(TorIntegrationProvider.getInstance(activity).isOrbotRunningAccordingToSettings()) {
        if(DDGControlVar.mDuckDuckGoContainer.torIntegration.isOrbotRunningAccordingToSettings()) {
			if (!DDGControlVar.hasUpdatedFeed) {
                if(!canUpdateFeed()) {
					// respect user choice of empty source list: show nothing
					BusProvider.getInstance().post(new FeedRetrieveSuccessEvent(new ArrayList<FeedObject>(),
							REQUEST_TYPE.FROM_CACHE));
				} else {
					// cache
					CacheFeedTask cacheTask = new CacheFeedTask(activity);

					// for HTTP request
                    mainFeedTask = new MainFeedTask(recyclerView);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						cacheTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						if (DDGControlVar.automaticFeedUpdate || swipeRefreshLayout.isRefreshing()
								|| DDGControlVar.changedSources) {
							mainFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							DDGControlVar.changedSources = false;
						}
					} else {
						cacheTask.execute();
						if (DDGControlVar.automaticFeedUpdate || swipeRefreshLayout.isRefreshing()
								|| DDGControlVar.changedSources) {
							mainFeedTask.execute();
							DDGControlVar.changedSources = false;
						}
					}
				}
			} else {
				// complete the action anyway
                swipeRefreshLayout.setRefreshing(false);
			}
		}
	}

    public void cleanImageTasks() {
        int count = recyclerView.getChildCount();
        for(int i=0;i<count;i++) {
            View v = recyclerView.getChildAt(i);
            if(v != null) {
                AsyncImageView iv = (AsyncImageView) v.findViewById(R.id.feedItemBackground);
                if(activity!=null) {
                    Picasso.with(activity).cancelRequest(iv);
                }
            }
        }
    }

    public boolean canUpdateFeed() {
        if(!DDGControlVar.userAllowedSources.isEmpty()) {
            return true;
        }
        if(DDGControlVar.defaultSources.isEmpty()) {
            return true;
        }
        for(String source : DDGControlVar.defaultSources) {
            if(!DDGControlVar.userDisallowedSources.contains(source)) {
                return true;
            }
        }
        return false;
    }

	@Subscribe
	public void onFeedRetrieveSuccessEvent(FeedRetrieveSuccessEvent event) {
        if(DDGControlVar.targetSource!=null) {
            if (event.requestType == REQUEST_TYPE.FROM_NETWORK) {
                recyclerAdapter.addSourceData(event.feed);
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (event.requestType == REQUEST_TYPE.FROM_NETWORK) {
                //synchronized (feedAdapter) {
                    //feedAdapter.clear();
                //}
                synchronized (recyclerAdapter) {
                    recyclerAdapter.clear();
                }
            }

            recyclerAdapter.addData(event.feed);

            // update pull-to-refresh header to reflect task completion
            swipeRefreshLayout.setRefreshing(false);

            DDGControlVar.hasUpdatedFeed = true;

            // do this upon filter completion
            if (DDGControlVar.targetSource != null && source_m_objectId != null) {
                //int nPos = feedView.getSelectionPosById(source_m_objectId);
                //feedView.setSelectionFromTop(nPos,source_m_yOffset);
                // mark for blink animation (as a visual cue after list update)
                //  feedAdapter.mark(source_m_objectId);

                int pos = -1;
                for (int i = 0; pos < 0 && i < recyclerAdapter.getItemCount(); i++) {
                    if (recyclerAdapter.getItem(i).getId().equals(source_m_objectId)) {
                        pos = i;
                    }
                }
                recyclerView.offsetChildrenVertical(source_m_yOffset);
            }
            if (DDGControlVar.targetCategory != null) {
                recyclerAdapter.filterCategory(DDGControlVar.targetCategory);
            }

        }
	}

	@Subscribe
	public void onFeedRetrieveErrorEvent(FeedRetrieveErrorEvent event) {
        if (activity!=null && !activity.isFinishing() && DDGControlVar.mDuckDuckGoContainer.currentScreen != SCREEN.SCR_FAVORITE && mainFeedTask != null) {
			new FeedRequestFailureDialogBuilder(activity).show();
		}
        swipeRefreshLayout.setRefreshing(false);
	}
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		if(event.feedObject!=null) {
            feedItemSelected(event.feedObject);
		}
	}

    @Subscribe
    public void onSourceFilterEvent(SourceFilterEvent event) {
        keepFeedUpdated();
    }

	@Subscribe
	public void onFeedCancelSourceFilterEvent(FeedCancelSourceFilterEvent event) {
		cancelSourceFilter();
	}

    @Subscribe
    public void onFeedCancelCategoryFilterEvent(FeedCancelCategoryFilterEvent event) {
        cancelCategoryFilter();
    }

	@Subscribe
	public void onRequestKeepFeedUpdatedEvent(RequestKeepFeedUpdatedEvent event) {
		keepFeedUpdated();
	}

	@Subscribe
	public void onFeedCleanImageTaskEvent(FeedCleanImageTaskEvent event) {
		cleanImageTasks();
	}

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && feedMenu!=null) {
            feedMenu.findItem(R.id.action_stories).setEnabled(false);
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(activity);
            overflowMenu.setMenu(feedMenu);
            overflowMenu.show(event.anchor);
        }
    }

}
