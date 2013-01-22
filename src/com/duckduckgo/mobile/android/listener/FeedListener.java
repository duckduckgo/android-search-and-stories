package com.duckduckgo.mobile.android.listener;

import java.util.List;

import com.duckduckgo.mobile.android.objects.FeedObject;

public interface FeedListener {
	public void onFeedRetrieved(List<FeedObject> feed, boolean fromCache);
	public void onFeedRetrievalFailed();
}