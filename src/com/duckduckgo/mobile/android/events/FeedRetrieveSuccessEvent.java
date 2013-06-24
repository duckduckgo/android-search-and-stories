package com.duckduckgo.mobile.android.events;

import java.util.List;

import android.widget.HeaderViewListAdapter;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.handmark.pulltorefresh.library.PullToRefreshMainFeedListView;


public class FeedRetrieveSuccessEvent extends Event {
	private REQUEST_TYPE requestType;
	public PullToRefreshMainFeedListView mPullToRefreshListView;
	private MainFeedListView feedView;
	private List<FeedObject> feed;
	private MainFeedAdapter feedAdapter;
	private DuckDuckGo activity;

	public FeedRetrieveSuccessEvent(List<FeedObject> feed, REQUEST_TYPE requestType, 
			DuckDuckGo activity){
		this.requestType = requestType;
		this.feed = feed;
		this.activity = activity;
	}
	
	public void process() {
		this.mPullToRefreshListView = activity.mPullRefreshFeedView;
		this.feedView = mPullToRefreshListView.getRefreshableView();
		this.feedAdapter = (MainFeedAdapter) ((HeaderViewListAdapter) feedView.getAdapter()).getWrappedAdapter();
		
		if(requestType == REQUEST_TYPE.FROM_NETWORK) {
			synchronized(feedAdapter) {
				feedAdapter.clear();
			}
		}
		
		feedAdapter.addData(feed);
		feedAdapter.notifyDataSetChanged();
		
		// update pull-to-refresh header to reflect task completion
		mPullToRefreshListView.onRefreshComplete();
		
		
		DDGControlVar.hasUpdatedFeed = true;
		
		// do this upon filter completion
		if(DDGControlVar.targetSource != null && activity.m_objectId != null) {
			int nPos = feedView.getSelectionPosById(activity.m_objectId);
			feedView.setSelectionFromTop(nPos,activity.m_yOffset);
			// mark for blink animation (as a visual cue after list update)
			feedAdapter.mark(activity.m_objectId);
		}
		
	};
}