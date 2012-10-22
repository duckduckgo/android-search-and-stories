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
	private final String imageUrl;
	private final String type;
	
	public FeedObject() {
		// no-arg constructor for ORMlite
		this.feed = "";
		this.favicon = "";
		this.description = "";
		this.timestamp = "";
		this.url = "";
		this.title = "";
		this.id = "";
		this.category = "";
		this.imageUrl = "";
		this.type = "";
	}
	
	public FeedObject(String id, String title, String description, String feed, String url, String imageUrl,
			String favicon, String timestamp, String category, String type) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.feed = feed;
		this.url = url;
		this.imageUrl = imageUrl;
		this.favicon = favicon;
		this.timestamp = timestamp;
		this.category = category;
		this.type = type;
	}
	
	public FeedObject(JSONObject obj) throws JSONException {
		this.feed = obj.getString("feed");
		this.favicon = obj.getString("favicon");
		this.description = obj.getString("description");
		this.timestamp = obj.getString("timestamp");
		this.url = obj.getString("url");
		this.title = obj.getString("title");
		this.id = obj.getString("id");
		this.category = obj.getString("category");
		this.imageUrl = obj.optString("image");
		this.type = obj.getString("type");
	}
	
	@Override
	public String toString() {
		String string = "{";
		
		string = string.concat("feed:" + this.feed + "\n");
		string = string.concat("favicon:" + this.favicon + "\n");
		string = string.concat("description:" + this.description + "\n");
		string = string.concat("timestamp:" + this.timestamp + "\n");
		string = string.concat("url:" + this.url + "\n");
		string = string.concat("title:" + this.title + "\n");
		string = string.concat("id:" + this.id + "\n");
		string = string.concat("category:" + this.category + "\n");
		string = string.concat("image: " + this.imageUrl + "}");
		string = string.concat("type: " + this.type + "}");
		
		return string;
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
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public String getType() {
		return type;
	}
}
