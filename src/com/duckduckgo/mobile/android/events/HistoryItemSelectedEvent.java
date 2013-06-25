package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.history.HistoryObject;

public class HistoryItemSelectedEvent extends Event {
	
	public HistoryObject historyObject;

	public HistoryItemSelectedEvent(HistoryObject historyObject){
		this.historyObject = historyObject;
	}
	
}
