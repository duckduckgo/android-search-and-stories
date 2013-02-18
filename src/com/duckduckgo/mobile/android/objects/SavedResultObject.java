package com.duckduckgo.mobile.android.objects;

import android.database.Cursor;

public class SavedResultObject {
	private final String type;
	private final String data;
	private final String url;
	private final String extraType;

	public SavedResultObject() {
		// no-arg constructor for ORMlite
		this.type = "";
		this.data = "";
		this.url = "";
		this.extraType = "";
	}
	
	public SavedResultObject(String type, String data, String url, String extraType) {
		this.type = type;
		this.data = data;
		this.url = url;
		this.extraType = extraType;
	}
	
	public SavedResultObject(String type, String data, String url) {
		this.type = type;
		this.data = data;
		this.url = url;
		this.extraType = "";
	}
	
	public SavedResultObject(String type, String data) {
		this.type = type;
		this.data = data;
		this.url = "";
		this.extraType = "";
	}
	
	public SavedResultObject(Cursor c) {
		this.type = "";
		this.data = c.getString(c.getColumnIndex("title"));
		this.url = c.getString(c.getColumnIndex("url"));
		this.extraType = c.getString(c.getColumnIndex("type"));
	}
	
	@Override
	public String toString() {
		String string = "{";
		string = string.concat("type:" + this.type + "\n");
		string = string.concat("data:" + this.data + "\n");
		string = string.concat("url:" + this.url + "\n");
		string = string.concat("extraType:" + this.extraType + "}");
		return string;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getData() {
		return data;
	}
	
	public String getType() {
		return type;
	}
	
	public String getExtraType() {
		return extraType;
	}
}
