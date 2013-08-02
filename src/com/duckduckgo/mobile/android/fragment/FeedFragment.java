package com.duckduckgo.mobile.android.fragment;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.AfterSwitchPostEvent;
import com.duckduckgo.mobile.android.events.CleanFeedDownloadsEvent;
import com.duckduckgo.mobile.android.events.ResetScreenStateEvent;
import com.duckduckgo.mobile.android.events.SearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.SourceFilterCancelEvent;
import com.duckduckgo.mobile.android.events.SourceIconsTaskCompleteEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveErrorEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedUpdateRequestEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeChangeEvent;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.squareup.otto.Subscribe;

public class FeedFragment extends Fragment {
	private View contentView;
	
	public MainFeedListView feedView;
	public PullToRefreshMainFeedListView mPullRefreshFeedView;
			
	// for keeping filter source at same position
	public String m_objectId = null;
	public int m_itemHeight;
	public int m_yOffset;
	
	SourceClickListener sourceClickListener = new SourceClickListener();
    MainFeedAdapter feedAdapter;

    public MainFeedTask mainFeedTask = null;
	
	private void initialise() {
		
		mPullRefreshFeedView = (PullToRefreshMainFeedListView) contentView.findViewById(R.id.mainFeedItems);
		DDGControlVar.ptrHeaderSize = PreferencesManager.getPtrHeaderTextSize(mPullRefreshFeedView.getHeaderTextSize());
		DDGControlVar.ptrSubHeaderSize = PreferencesManager.getPtrHeaderSubTextSize(mPullRefreshFeedView.getHeaderSubTextSize());
		
		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.ptrSubHeaderSize);
		
		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.ptrSubHeaderSize);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshFeedView.setOnRefreshListener(new OnRefreshListener<MainFeedListView>() {
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
        
        feedAdapter = new MainFeedAdapter(getActivity(), sourceClickListener);
		
		feedView = mPullRefreshFeedView.getRefreshableView();
        feedView.setAdapter(feedAdapter);
                
        mainFeedTask = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// update feeds
		// https://app.asana.com/0/2891531242889/2858723303746
		DDGControlVar.hasUpdatedFeed = false;
		keepFeedUpdated();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	setRetainInstance(true);
    	contentView = inflater.inflate(R.layout.feed_fragment_view, container, false);
		initialise();
        return contentView;
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
	
	
	class SourceClickListener implements OnClickListener {
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
	
	private void feedItemSelected(FeedObject feedObject) {
		// keep a reference, so that we can reuse details while saving
		DDGControlVar.currentFeedObject = feedObject;
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_FEED;		
		
		String url = feedObject.getUrl();
		if (url != null) {
			if(!DDGApplication.getDB().existsVisibleFeedById(feedObject.getId())) {
				DDGApplication.getDB().insertFeedItem(feedObject);
				BusProvider.getInstance().post(new SyncAdaptersEvent());		
			}
			BusProvider.getInstance().post(new AfterSwitchPostEvent(SCREEN.SCR_WEBVIEW, 
					new SearchOrGoToUrlEvent(url, SESSIONTYPE.SESSION_FEED)));
		}
		
		if(ReadArticlesManager.addReadArticle(feedObject)){
			feedAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * Cancels source filter applied with source icon click from feed item
	 */
	public void cancelSourceFilter() {
		DDGControlVar.targetSource = null;
		feedAdapter.unmark();
		DDGControlVar.hasUpdatedFeed = false;
		keepFeedUpdated();
	}
	
	/**
	 * Refresh feed if it's not marked as updated
	 */
	@SuppressLint("NewApi")
	public void keepFeedUpdated(){
		Activity activity = getActivity();
		if(TorIntegrationProvider.getInstance(activity).isOrbotRunningAccordingToSettings()){
			if (!DDGControlVar.hasUpdatedFeed) {
				if(DDGControlVar.userAllowedSources.isEmpty() && !DDGControlVar.userDisallowedSources.isEmpty()) {
					// respect user choice of empty source list: show nothing
					BusProvider.getInstance().post(new FeedRetrieveSuccessEvent(new ArrayList<FeedObject>(), 
							REQUEST_TYPE.FROM_CACHE));
				}
				else {				
					// cache
					CacheFeedTask cacheTask = new CacheFeedTask(activity);
				
					// for HTTP request
					mainFeedTask = new MainFeedTask(getActivity());
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						cacheTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						mainFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					else {
						cacheTask.execute();
						mainFeedTask.execute();
					}
				}
			}
		}
		else{
            // complete the action anyway
            mPullRefreshFeedView.onRefreshComplete();
        }
	}
	
	
	@Subscribe
	public void onResetScreenState(ResetScreenStateEvent event) {
		DDGControlVar.currentFeedObject = null;
	}
	
	@Subscribe
	public void onFontSizeChange(FontSizeChangeEvent event) {
		
		DDGControlVar.mainTextSize = DDGControlVar.prevMainTextSize + event.diffPixel;
		feedAdapter.notifyDataSetInvalidated();
		
		DDGControlVar.ptrHeaderSize = DDGControlVar.prevPtrHeaderSize + event.diff;
		DDGControlVar.ptrSubHeaderSize = DDGControlVar.prevPtrSubHeaderSize + event.diff;
		
		// adjust Pull-to-Refresh
		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.ptrSubHeaderSize);
		
		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.ptrSubHeaderSize);
	}
    
	@Subscribe
	public void onFontSizeCancel(FontSizeCancelEvent event) {
		DDGControlVar.mainTextSize = DDGControlVar.prevMainTextSize;
		
		feedAdapter.notifyDataSetInvalidated();
		
		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.prevPtrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.prevPtrSubHeaderSize);
		
		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.prevPtrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.prevPtrSubHeaderSize);
		
		DDGControlVar.prevMainTextSize = 0;		
		DDGControlVar.prevPtrHeaderSize = 0;
		DDGControlVar.prevPtrSubHeaderSize = 0;
	}
	
	@Subscribe
	public void onSourceIconsTaskComplete(SourceIconsTaskCompleteEvent event) {
		mPullRefreshFeedView.getRefreshableView().invalidateViews();
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mainFeedTask != null) {
			mainFeedTask.cancel(false);
			mainFeedTask = null;
		}
	}

	@Subscribe
	public void onFeedUpdateRequest(FeedUpdateRequestEvent event) {
		keepFeedUpdated();
	}
	
	@Subscribe
	public void onSourceFilterCancel(SourceFilterCancelEvent event) {
		cancelSourceFilter();
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
		if (DDGControlVar.currentScreen != SCREEN.SCR_SAVED_FEED && mainFeedTask != null) {
			new FeedRequestFailureDialogBuilder(getActivity()).show();
		}

	}
	
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		feedItemSelected(event.feedObject);
	}
	
	public void onCleanFeedDownloads(CleanFeedDownloadsEvent event) {
		feedView.cleanImageTasks();
	}

}
