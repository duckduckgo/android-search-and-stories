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
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.ReadabilityFeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.events.RequestKeepFeedUpdatedEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.CacheFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;
import com.duckduckgo.mobile.android.util.ReadArticlesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshMainFeedListView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class FeedFragment extends Fragment {

	public static final String TAG = "feed_fragment";

	private MainFeedListView feedView = null;
	private PullToRefreshMainFeedListView mPullRefreshFeedView = null;

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
		return inflater.inflate(R.layout.fragment_feed, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		keepFeedUpdated();
	}

	public void init() {
		mPullRefreshFeedView = (PullToRefreshMainFeedListView) getView().findViewById(R.id.mainFeedItems);//feed fragment
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
		DDGControlVar.mDuckDuckGoContainer.feedAdapter = new MainFeedAdapter(getActivity(), sourceClickListener);

		DDGControlVar.mDuckDuckGoContainer.mainFeedTask = null;

		feedView = mPullRefreshFeedView.getRefreshableView();
		feedView.setAdapter(DDGControlVar.mDuckDuckGoContainer.feedAdapter);

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
				((DuckDuckGo)getActivity()).syncAdapters();//aaa todo change to event
			}
			//((DuckDuckGo)getActivity()).searchOrGoToUrl(url, SESSIONTYPE.SESSION_FEED);//aaa todo change to event
			BusProvider.getInstance().post(new RequestOpenWebPageEvent(url, SESSIONTYPE.SESSION_FEED));
		}

		if(ReadArticlesManager.addReadArticle(feedObject)){
			DDGControlVar.mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();
		}
	}

	public void feedItemSelected(String feedId) {
		FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
		feedItemSelected(feedObject);
	}

	/**
	 * save feed by object or by the feed id
	 *
	 * @param feedObject
	 * @param pageFeedId
	 */
	public void itemSaveFeed(FeedObject feedObject, String pageFeedId) {//feed fragment
		if(feedObject != null) {
			if(DDGApplication.getDB().existsAllFeedById(feedObject.getId())) {
				DDGApplication.getDB().makeItemVisible(feedObject.getId());
			}
			else {
				DDGApplication.getDB().insertVisible(feedObject);
			}
		}
		else if(pageFeedId != null && pageFeedId.length() != 0){
			DDGApplication.getDB().makeItemVisible(pageFeedId);
		}
	}

	/**
	 * Cancels source filter applied with source icon click from feed item
	 */
	public void cancelSourceFilter() {//feed fragment
		DDGControlVar.targetSource = null;
		DDGControlVar.mDuckDuckGoContainer.feedAdapter.unmark();
		DDGControlVar.hasUpdatedFeed = false;
		keepFeedUpdated();
	}

	/**
	 * Refresh feed if it's not marked as updated
	 */
	@SuppressLint("NewApi")
	public void keepFeedUpdated(){//feed fragment
		//if(torIntegration.isOrbotRunningAccordingToSettings()) { //todo
			if (!DDGControlVar.hasUpdatedFeed) {
				if (DDGControlVar.userAllowedSources.isEmpty() && !DDGControlVar.userDisallowedSources.isEmpty()) {
					// respect user choice of empty source list: show nothing
					BusProvider.getInstance().post(new FeedRetrieveSuccessEvent(new ArrayList<FeedObject>(),
							REQUEST_TYPE.FROM_CACHE));
				} else {
					// cache
					CacheFeedTask cacheTask = new CacheFeedTask((DuckDuckGo)getActivity());//todo change the context

					// for HTTP request
					DDGControlVar.mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(feedView);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						cacheTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						if (DDGControlVar.automaticFeedUpdate || mPullRefreshFeedView.isRefreshing()
								|| DDGControlVar.changedSources) {
							DDGControlVar.mDuckDuckGoContainer.mainFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							DDGControlVar.changedSources = false;
						}
					} else {
						cacheTask.execute();
						if (DDGControlVar.automaticFeedUpdate || mPullRefreshFeedView.isRefreshing()
								|| DDGControlVar.changedSources) {
							DDGControlVar.mDuckDuckGoContainer.mainFeedTask.execute();
							DDGControlVar.changedSources = false;
						}
					}
				}
			} else {
				// complete the action anyway
				mPullRefreshFeedView.onRefreshComplete();
			}
		//}
	}

	@Subscribe
	public void onFeedRetrieveSuccessEvent(FeedRetrieveSuccessEvent event) {//feed fragment
		Log.e("aaa", "fragment on feed retrieve success event");
		if(event.requestType == REQUEST_TYPE.FROM_NETWORK) {
			synchronized(DDGControlVar.mDuckDuckGoContainer.feedAdapter) {
				DDGControlVar.mDuckDuckGoContainer.feedAdapter.clear();
			}
		}

		DDGControlVar.mDuckDuckGoContainer.feedAdapter.addData(event.feed);
		DDGControlVar.mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();

		// update pull-to-refresh header to reflect task completion
		mPullRefreshFeedView.onRefreshComplete();

		DDGControlVar.hasUpdatedFeed = true;

		// do this upon filter completion
		if(DDGControlVar.targetSource != null && m_objectId != null) {
			int nPos = feedView.getSelectionPosById(m_objectId);
			feedView.setSelectionFromTop(nPos,m_yOffset);
			// mark for blink animation (as a visual cue after list update)
			DDGControlVar.mDuckDuckGoContainer.feedAdapter.mark(m_objectId);
		}

	}

	@Subscribe
	public void onFeedRetrieveErrorEvent(FeedRetrieveErrorEvent event) {//feed fragment
		Log.e("aaa", "fragment on feed retrieve error event");
		if (DDGControlVar.mDuckDuckGoContainer.currentScreen != SCREEN.SCR_SAVED_FEED && DDGControlVar.mDuckDuckGoContainer.mainFeedTask != null) {
			new FeedRequestFailureDialogBuilder((DuckDuckGo)getActivity()).show();//todo change context
		}

	}

	@Subscribe
	public void onReadabilityFeedRetrieveSuccessEvent(ReadabilityFeedRetrieveSuccessEvent event) {//feed fragment
		Log.e("aaa", "fragment on readability feed retrieve success event");
		if(event.feed.size() != 0) {
			DDGControlVar.currentFeedObject = event.feed.get(0);
			DDGControlVar.mDuckDuckGoContainer.lastFeedUrl = DDGControlVar.currentFeedObject.getUrl();
			//mainWebView.readableAction(DDGControlVar.currentFeedObject);//todo event
		}
	}

	@Subscribe
	public void onRequestKeepFeedUpdatedEvent(RequestKeepFeedUpdatedEvent event) {
		keepFeedUpdated();
	}


}
