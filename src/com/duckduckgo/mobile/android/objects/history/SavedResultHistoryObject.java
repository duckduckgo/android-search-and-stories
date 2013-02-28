package com.duckduckgo.mobile.android.objects.history;

import android.database.Cursor;

public class SavedResultHistoryObject extends ParentHistoryObject {
	
	public SavedResultHistoryObject(String type, String data, String url) {
		super(type, data, url);
	}
	
	public SavedResultHistoryObject(String type, String data) {
		super(type, data);
	}

	public SavedResultHistoryObject(Cursor c) {
		this.data = c.getString(c.getColumnIndex("query"));
	}
	
}
