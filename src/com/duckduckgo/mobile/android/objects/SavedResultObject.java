package com.duckduckgo.mobile.android.objects;

import android.database.sqlite.SQLiteCursor;

import com.duckduckgo.mobile.android.DDGApplication;

public class SavedResultObject {
	private final String url;
	private final String title;
	private final String id;
	private final String imageUrl;
		
	public SavedResultObject() {
		// no-arg constructor for ORMlite
		this.url = "";
		this.title = "";
		this.id = "";
		this.imageUrl = "";
	}
	
	public SavedResultObject(String title, String url) {
		this.id = "";
		this.title = title;
		this.url = url;
		this.imageUrl = "";
	}
	
	public SavedResultObject(String title, String url, String imageUrl) {
		this.id = "";
		this.title = title;
		this.url = url;
		if(imageUrl != null) {
			this.imageUrl = imageUrl;
		}
		else {
			this.imageUrl = "";
		}
	}
	
	public SavedResultObject(SQLiteCursor cursor) {
		this.id = cursor.getString(cursor.getColumnIndex("_id"));
		this.title = cursor.getString(cursor.getColumnIndex("title"));
		this.url = cursor.getString(cursor.getColumnIndex("url"));
		this.imageUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
	}
	
	@Override
	public String toString() {
		String string = "{";
		
		string = string.concat("url:" + this.url + "\n");
		string = string.concat("title:" + this.title + "\n");
		string = string.concat("id:" + this.id + "\n");
		string = string.concat("image: " + this.imageUrl + "}");
		
		return string;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public boolean isSaved() {		
		return DDGApplication.getDB().isSavedInOthers(title, url);
	}
}
