package com.duckduckgo.mobile.android.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestObject {
	private final String phrase;
	private final int score;
	private final String snippet;
	private final String imageUrl;
	
	public SuggestObject(JSONObject obj) throws JSONException {
		this.phrase = obj.getString("phrase");
		this.score = obj.getInt("score");
		this.snippet = obj.getString("snippet");
		this.imageUrl = obj.getString("image");
	}
	
	public String getPhrase() {
		return phrase;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getSnippet() {
		return snippet;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
}
