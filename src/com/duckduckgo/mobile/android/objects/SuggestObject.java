package com.duckduckgo.mobile.android.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestObject {
	private String phrase;
	private int score;
	private String snippet;
	private String imageUrl;
	
	public SuggestObject(JSONObject obj) throws JSONException {
		this.phrase = obj.getString("phrase");
		this.score = obj.optInt("score", 0); //Optional Field
		this.snippet = obj.optString("snippet", null); //Optional Field
		this.imageUrl = obj.optString("image", null); //Optional Field
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
