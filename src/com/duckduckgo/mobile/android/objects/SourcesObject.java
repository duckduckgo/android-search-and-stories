package com.duckduckgo.mobile.android.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class SourcesObject implements SectionedListItem {
	private final String description;
	private final String link;
	private final int selectedByDefault;
	private final String title;
	private final String id;
	private final String category;
	private final String imageUrl;
	
	public SourcesObject(JSONObject obj) throws JSONException {
		this.description = obj.getString("description");
		this.selectedByDefault = obj.getInt("default");
		this.link = obj.getString("link");
		this.title = obj.getString("title");
		this.id = obj.getString("id");
		this.category = obj.getString("category");
		this.imageUrl = obj.optString("image");
	}
	
	@Override
	public String toString() {
		String string = "{";
		
		string = string.concat("description:" + this.description + "\n");
		string = string.concat("default:" + this.selectedByDefault + "\n");
		string = string.concat("link:" + this.link + "\n");
		string = string.concat("title:" + this.title + "\n");
		string = string.concat("id:" + this.id + "\n");
		string = string.concat("category:" + this.category + "\n");
		string = string.concat("image: " + this.imageUrl + "}");
		
		return string;
	}
	
	public int getDefault() {
		return selectedByDefault;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLink() {
		return link;
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

	@Override
	public boolean isSection() {
		return false;
	}
}
