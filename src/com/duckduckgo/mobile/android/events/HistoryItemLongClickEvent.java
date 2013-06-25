package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.history.HistoryObject;

public class HistoryItemLongClickEvent extends Event {
	
	public HistoryObject historyObject;

	public HistoryItemLongClickEvent(HistoryObject historyObject){
		this.historyObject = historyObject;
	}
	
}
