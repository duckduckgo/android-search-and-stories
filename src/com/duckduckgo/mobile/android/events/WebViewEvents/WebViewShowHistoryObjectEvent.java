package com.duckduckgo.mobile.android.events.WebViewEvents;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;

public class WebViewShowHistoryObjectEvent extends Event {

	public HistoryObject historyObject;

	public WebViewShowHistoryObjectEvent(HistoryObject historyObject) {
		this.historyObject = historyObject;
	}
}
