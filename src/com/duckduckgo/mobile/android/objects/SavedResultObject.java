package com.duckduckgo.mobile.android.objects;

import android.database.Cursor;

public class SavedResultObject extends ParentHistoryObject {
	
	public SavedResultObject(String type, String data, String url, String extraType) {
		super(type, data, url, extraType);
	}
	
	public SavedResultObject(String type, String data, String url) {
		super(type, data, url);
	}
	
	public SavedResultObject(String type, String data) {
		super(type, data);
	}

	public SavedResultObject(Cursor c) {
		this.type = "";
		this.data = c.getString(c.getColumnIndex("title"));
		this.url = c.getString(c.getColumnIndex("url"));
		this.extraType = c.getString(c.getColumnIndex("type"));
	}
	
}
