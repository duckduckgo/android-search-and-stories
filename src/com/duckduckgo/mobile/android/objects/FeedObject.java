package com.duckduckgo.mobile.android.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedObject {
	private final String feed;
	private final String favicon;
	private final String description;
	private final String timestamp;
	private final String url;
	private final String title;
	private final String id;
	private final String category;
	
	public FeedObject(JSONObject obj) throws JSONException {
		this.feed = obj.getString("feed");
		this.favicon = obj.getString("favicon");
		this.description = obj.getString("description");
		this.timestamp = obj.getString("timestamp");
		this.url = obj.getString("url");
		this.title = obj.getString("title");
		this.id = obj.getString("id");
		this.category = obj.getString("category");
	}
	
	public String getFeed() {
		return feed;
	}
	
	public String getFavicon() {
		return favicon;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getDescription() {
		return description;
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
	
	public String getCategory() {
		return category;
	}
}
