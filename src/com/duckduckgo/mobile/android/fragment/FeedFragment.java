package com.duckduckgo.mobile.android.fragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.RequestKeepFeedUpdatedEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.RequestSyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCleanImageTaskEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.events.fontSizeEvents.FontSizeCancelScalingEvent;
import com.duckduckgo.mobile.android.events.fontSizeEvents.FontSizeOnProgressChangedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuCloseEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.CacheFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;
import com.duckduckgo.mobile.android.util.ReadArticlesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshMainFeedListView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class FeedFragment extends Fragment {

	public static final String TAG = "feed_fragment";

	private MainFeedListView feedView = null;
	private PullToRefreshMainFeedListView mPullRefreshFeedView = null;
	private View fragmentView;

	private MainFeedAdapter feedAdapter = null;
	private MainFeedTask mainFeedTask = null;

	// for keeping filter source at same position
	public String m_objectId = null;
	public int m_itemHeight;
	public int m_yOffset;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		setRetainInstance(true);
		init();
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
		if (mainFeedTask != null) {
			mainFeedTask.cancel(false);
			mainFeedTask = null;
		}
	}

	public void init() {
		mPullRefreshFeedView = (PullToRefreshMainFeedListView) fragmentView.findViewById(R.id.mainFeedItems);
		PreferencesManager.setPtrHeaderFontDefaults(mPullRefreshFeedView.getHeaderTextSize(), mPullRefreshFeedView.getHeaderSubTextSize());
		DDGControlVar.ptrHeaderSize = PreferencesManager.getPtrHeaderTextSize();
		DDGControlVar.ptrSubHeaderSize = PreferencesManager.getPtrHeaderSubTextSize();

		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.ptrSubHeaderSize);

		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.ptrSubHeaderSize);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshFeedView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MainFeedListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<MainFeedListView> refreshView) {
				mPullRefreshFeedView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				// refresh the list
				DDGControlVar.hasUpdatedFeed = false;
				keepFeedUpdated();
			}
		});

		SourceClickListener sourceClickListener = new SourceClickListener();
		feedAdapter = new MainFeedAdapter(getActivity(), sourceClickListener);

		mainFeedTask = null;

		feedView = mPullRefreshFeedView.getRefreshableView();
		feedView.setAdapter(feedAdapter);

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
				m_objectId = ((FeedObject) feedView.getItemAtPosition(pos)).getId();
				m_itemHeight = itemParent.getHeight();

				Rect r = new Rect();
				Point offset = new Point();
				feedView.getChildVisibleRect(itemParent, r, offset);
				m_yOffset = offset.y;

				String sourceType = ((AsyncImageView) v).getType();
				DDGControlVar.targetSource = sourceType;

				DDGControlVar.hasUpdatedFeed = false;
				keepFeedUpdated();
			}

		}
	}

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
		DDGControlVar.targetSource = null;
		DDGControlVar.hasUpdatedFeed = false;
		feedAdapter.unmark();
		keepFeedUpdated();
	}

	/**
	 * Refresh feed if it's not marked as updated
	 */
	@SuppressLint("NewApi")
	public void keepFeedUpdated(){
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
						if (DDGControlVar.automaticFeedUpdate || mPullRefreshFeedView.isRefreshing()
								|| DDGControlVar.changedSources) {
							mainFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							DDGControlVar.changedSources = false;
						}
					} else {
						cacheTask.execute();
						if (DDGControlVar.automaticFeedUpdate || mPullRefreshFeedView.isRefreshing()
								|| DDGControlVar.changedSources) {
							mainFeedTask.execute();
							DDGControlVar.changedSources = false;
						}
					}
				}
			} else {
				// complete the action anyway
				mPullRefreshFeedView.onRefreshComplete();
			}
		}
	}

	@Subscribe
	public void onFeedRetrieveSuccessEvent(FeedRetrieveSuccessEvent event) {
		if(event.requestType == REQUEST_TYPE.FROM_NETWORK) {
			synchronized(feedAdapter) {
				feedAdapter.clear();
			}
		}

		feedAdapter.addData(event.feed);
		feedAdapter.notifyDataSetChanged();

		// update pull-to-refresh header to reflect task completion
		mPullRefreshFeedView.onRefreshComplete();

		DDGControlVar.hasUpdatedFeed = true;

		// do this upon filter completion
		if(DDGControlVar.targetSource != null && m_objectId != null) {
			int nPos = feedView.getSelectionPosById(m_objectId);
			feedView.setSelectionFromTop(nPos,m_yOffset);
			// mark for blink animation (as a visual cue after list update)
			feedAdapter.mark(m_objectId);
		}

	}

	@Subscribe
	public void onFeedRetrieveErrorEvent(FeedRetrieveErrorEvent event) {
		if (DDGControlVar.mDuckDuckGoContainer.currentScreen != SCREEN.SCR_SAVED_FEED && mainFeedTask != null) {
			new FeedRequestFailureDialogBuilder(getActivity()).show();
		}

	}

	/**
	 * Handling both MainFeedItemSelectedEvent and SavedFeedItemSelectedEvent.
	 * (modify to handle independently when necessary)
	 * @param event
	 */
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		BusProvider.getInstance().post(new LeftMenuCloseEvent());
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
	public void onRequestKeepFeedUpdatedEvent(RequestKeepFeedUpdatedEvent event) {
		keepFeedUpdated();
	}

	@Subscribe
	public void onFeedCleanImageTaskEvent(FeedCleanImageTaskEvent event) {
		feedView.cleanImageTasks();
	}

	@Subscribe
	public void onFontSizeOnProgressChangedEvent(FontSizeOnProgressChangedEvent event) {
		feedAdapter.notifyDataSetInvalidated();
		// adjust Pull-to-Refresh
		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.ptrSubHeaderSize);
		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.ptrSubHeaderSize);
	}

	@Subscribe
	public void onFontSizeCancelScalingEvent(FontSizeCancelScalingEvent event) {
		feedAdapter.notifyDataSetInvalidated();

		mPullRefreshFeedView.setHeaderTextSize(PreferencesManager.getPtrHeaderTextSize());
		mPullRefreshFeedView.setHeaderSubTextSize(PreferencesManager.getPtrHeaderSubTextSize());

		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(PreferencesManager.getPtrHeaderTextSize());
		mPullRefreshFeedView.setLoadingSubTextSize(PreferencesManager.getPtrHeaderSubTextSize());
	}

}
