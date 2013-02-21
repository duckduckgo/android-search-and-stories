package com.duckduckgo.mobile.android.objects;

import android.database.Cursor;

public class HistoryObject extends ParentHistoryObject {
	
	public HistoryObject(String type, String data, String url, String extraType, String feedId) {
		super(type, data, url, extraType, feedId);
	}
	
	public HistoryObject(String type, String data, String url, String extraType) {
		super(type, data, url, extraType);
	}
	
	public HistoryObject(String type, String data, String url) {
		super(type, data, url);
	}
	
	public HistoryObject(String type, String data) {
		super(type, data);
	}
	
	public HistoryObject(Cursor c) {
		this.type = c.getString(c.getColumnIndex("type"));
		this.data = c.getString(c.getColumnIndex("data"));
		this.url = c.getString(c.getColumnIndex("url"));
		this.extraType = c.getString(c.getColumnIndex("extraType"));
		this.feedId = c.getString(c.getColumnIndex("feedId"));
	}
}
