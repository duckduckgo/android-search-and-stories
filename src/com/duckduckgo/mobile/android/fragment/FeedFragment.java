package com.duckduckgo.mobile.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.events.RequestKeepFeedUpdatedEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.RequestSyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	public static final String TAG = "feed_fragment";

    private Activity activity = null;

	private MainFeedListView feedView = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
	private View fragmentView;

	private MainFeedAdapter feedAdapter = null;
	private MainFeedTask mainFeedTask = null;

	// for keeping filter source at same position
	public String source_m_objectId = null;
	public int source_m_itemHeight;
	public int source_m_yOffset;

    // for keeping filter category at same position
    public String category_m_objectId = null;
    public int category_m_itemHeight;
    public int category_m_yOffset;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private Menu feedMenu = null;
    private DDGOverflowMenu overflowMenu = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.e("aaa", "on create: "+getTag());
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        Log.e("aaa", "on destroy: "+getTag());
		BusProvider.getInstance().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentView =  inflater.inflate(R.layout.fragment_feed, container, false);
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        Log.e("aaa", "on activity created: "+getTag());
		setRetainInstance(true);
        setHasOptionsMenu(false);
        activity = getActivity();
		init();
        if(savedInstanceState!=null) {
            Log.e("aaa", "on activity created savedinstantestate!=null: "+getTag());
        } else {
            Log.e("aaa", "on activity created savedinstancestate==null: "+getTag());
        }
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("aaa", "on hidden changed, hidden "+hidden+" - "+getTag());
        if(!hidden) {
            keepFeedUpdated();
        }
    }

	@Override
	public void onResume() {
		super.onResume();

        Log.e("aaa", "on resume: "+getTag());

        //setHasOptionsMenu(DDGControlVar.START_SCREEN==SCREEN.SCR_STORIES && DDGControlVar.homeScreenShowing);
        //setHasOptionsMenu(false);
		// lock button etc. can cause MainFeedTask results to be useless for the Activity
		// which is restarted (onPostExecute becomes invalid for the new Activity instance)
		// ensure we refresh in such cases

		keepFeedUpdated();
	}

	@Override
	public void onPause() {
		super.onPause();
        Log.e("aaa", "on pause: "+getTag());
        swipeRefreshLayout.setRefreshing(false);
		if (mainFeedTask != null) {
			mainFeedTask.cancel(false);
			mainFeedTask = null;
		}
	}

    @Override
    public void onStop() {
        super.onStop();
        Log.e("aaa", "on stop: "+getTag());
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.action_stories).setEnabled(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(menu==null) {
            //return;
        }
        feedMenu = menu;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("aaa", "feed fragment on options item selected: "+item.getTitle());
        return super.onOptionsItemSelected(item);
    }

        @Override
    public void onRefresh() {
        // refresh the list
        DDGControlVar.hasUpdatedFeed = false;
        keepFeedUpdated();
    }

	public void init() {

		SourceClickListener sourceClickListener = new SourceClickListener();
        CategoryClickListener categoryClickListener = new CategoryClickListener();
		feedAdapter = new MainFeedAdapter(getActivity(), sourceClickListener, categoryClickListener);

		mainFeedTask = null;

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        feedView = (MainFeedListView) fragmentView.findViewById(R.id.feed_list_view);
		feedView.setAdapter(feedAdapter);

        feedMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.main, feedMenu);

	}

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
	}

    class CategoryClickListener implements View.OnClickListener {
        public void onClick(final View v) {
            // source filtering

            if(DDGControlVar.targetCategory != null){
                cancelCategoryFilter();
            }
            else {

                final View itemParent = (View) v.getParent();
                int pos = feedView.getPositionForView(itemParent);
                category_m_objectId = ((FeedObject) feedView.getItemAtPosition(pos)).getId();
                FeedObject feedObject = (FeedObject) feedView.getItemAtPosition(pos);
                category_m_itemHeight = itemParent.getHeight();
/*
                itemParent.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        //animateRemoval(feedView, v);
                    }
                });
*/
                Rect r = new Rect();
                Point offset = new Point();
                feedView.getChildVisibleRect(itemParent, r, offset);
                category_m_yOffset = offset.y;

                //String sourceType = ((AsyncImageView) v).getType();
                //String categoryType = feedObject.getCategory();
                DDGControlVar.targetCategory = feedObject.getCategory();
                Log.e("aaa", "category clicked: "+DDGControlVar.targetCategory);

                for(int i=0; i<feedAdapter.getCount();i++) {
                    FeedObject feed = feedAdapter.getItem(i);
                    if(feed.getCategory().equals(DDGControlVar.targetCategory)) {
                        //View view = feedAdapter.get

                    }
                }


                DDGControlVar.hasUpdatedFeed = false;
                keepFeedUpdated();
            }
            Log.e("aaa", "feedview count: "+feedView.getCount()+" - feed adapter count: "+feedAdapter.getCount());

        }
    }

/*
    private void animateRemoval(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = feedAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = feedView.getPositionForView(viewToRemove);
        feedAdapter.remove(feedAdapter.getItem(position));

        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = feedAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(1000).translationY(0);

                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(1000).translationY(0);
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }*/

	public void feedItemSelected(FeedObject feedObject) {
		// keep a reference, so that we can reuse details while saving
		DDGControlVar.currentFeedObject = feedObject;
		DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_FEED;

		String url = feedObject.getUrl();
		if (url != null) {
			if(!DDGApplication.getDB().existsVisibleFeedById(feedObject.getId())) {
				DDGApplication.getDB().insertFeedItem(feedObject);
				BusProvider.getInstance().post(new RequestSyncAdaptersEvent());

			}
			BusProvider.getInstance().post(new RequestOpenWebPageEvent(url, SESSIONTYPE.SESSION_FEED));
		}

		if(ReadArticlesManager.addReadArticle(feedObject)){
			feedAdapter.notifyDataSetChanged();
		}
	}

	public void feedItemSelected(String feedId) {
		FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
		feedItemSelected(feedObject);
	}

	/**
	 * Cancels source filter applied with source icon click from feed item
	 */
	public void cancelSourceFilter() {
        Log.e("aaa", "inside cancel source filter");
		DDGControlVar.targetSource = null;
		DDGControlVar.hasUpdatedFeed = false;
		feedAdapter.unmark();
		keepFeedUpdated();
	}

    /**
     * Cancels target filter applied with source icon click from feed item
     */
    public void cancelCategoryFilter() {
        Log.e("aaa", "inside cancel category filter");
        DDGControlVar.targetCategory = null;
        DDGControlVar.hasUpdatedFeed = false;
        feedAdapter.unmarkCategory();

        //feedAdapter.getFilter().filter("");--temp

        //feedAdapter.unmark();
        //keepFeedUpdated();
    }

	/**
	 * Refresh feed if it's not marked as updated
	 */
	@SuppressLint("NewApi")
	public void keepFeedUpdated(){
        Log.e("aaa", "keep feed updated");
		if(TorIntegrationProvider.getInstance(getActivity()).isOrbotRunningAccordingToSettings()) {
			if (!DDGControlVar.hasUpdatedFeed) {
				if (DDGControlVar.userAllowedSources.isEmpty() && !DDGControlVar.userDisallowedSources.isEmpty()) {
					// respect user choice of empty source list: show nothing
					BusProvider.getInstance().post(new FeedRetrieveSuccessEvent(new ArrayList<FeedObject>(),
							REQUEST_TYPE.FROM_CACHE));
				} else {
					// cache
					CacheFeedTask cacheTask = new CacheFeedTask(getActivity());

					// for HTTP request
					mainFeedTask = new MainFeedTask(feedView);

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

	@Subscribe
	public void onFeedRetrieveSuccessEvent(FeedRetrieveSuccessEvent event) {
        Log.e("aaa", "on fed retrieve success, size: "+event.feed.size()+" - request type: "+event.requestType);
		if(event.requestType == REQUEST_TYPE.FROM_NETWORK) {
			synchronized(feedAdapter) {
				feedAdapter.clear();
			}
		}

		feedAdapter.addData(event.feed);

        //feedAdapter.getFilter().filter("");temp

		feedAdapter.notifyDataSetChanged();

		// update pull-to-refresh header to reflect task completion
        swipeRefreshLayout.setRefreshing(false);

		DDGControlVar.hasUpdatedFeed = true;

		// do this upon filter completion
		if(DDGControlVar.targetSource != null && source_m_objectId != null) {
			int nPos = feedView.getSelectionPosById(source_m_objectId);
			feedView.setSelectionFromTop(nPos,source_m_yOffset);
			// mark for blink animation (as a visual cue after list update)
			feedAdapter.mark(source_m_objectId);
		}

	}

	@Subscribe
	public void onFeedRetrieveErrorEvent(FeedRetrieveErrorEvent event) {
        //aaa
		//if (DDGControlVar.mDuckDuckGoContainer.currentScreen != SCREEN.SCR_SAVED_FEED && mainFeedTask != null) {
        if (activity!=null && DDGControlVar.mDuckDuckGoContainer.currentScreen != SCREEN.SCR_FAVORITE && mainFeedTask != null) {
			new FeedRequestFailureDialogBuilder(activity).show();
		}
        swipeRefreshLayout.setRefreshing(false);
	}

	/**
	 * Handling both MainFeedItemSelectedEvent and SavedFeedItemSelectedEvent.
	 * (modify to handle independently when necessary)
	 * @param event
	 */
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		if(event.feedObject==null) {
			feedItemSelected(event.feedId);
		} else {
			feedItemSelected(event.feedObject);
		}
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
		feedView.cleanImageTasks();
	}

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && feedMenu!=null) {
            feedMenu.findItem(R.id.action_stories).setEnabled(false);
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(getActivity());
            //overflowMenu.setHeaderMenu(feedMenu);
            overflowMenu.setMenu(feedMenu);
            //overflowMenu.setAnimationStyle(R.style.DDGPopupAnimation);
            overflowMenu.show(event.anchor);

            Log.e("aaa", "shuld open feed menu now, feed menu != null");
        } else {
            Log.e("aaa", "shuld open feed menu now, feed menu == null");
        }
    }

}
