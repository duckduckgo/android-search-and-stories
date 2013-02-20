package com.duckduckgo.mobile.android.objects;

public class ParentHistoryObject {
	protected String type;
	protected String data;
	protected String url;
	protected String extraType;

	public ParentHistoryObject() {
		// no-arg constructor for ORMlite
		this.type = "";
		this.data = "";
		this.url = "";
		this.extraType = "";
	}
	
	public ParentHistoryObject(String type, String data, String url, String extraType) {
		this.type = type;
		this.data = data;
		this.url = url;
		this.extraType = extraType;
	}
	
	public ParentHistoryObject(String type, String data, String url) {
		this.type = type;
		this.data = data;
		this.url = url;
		this.extraType = "";
	}
	
	public ParentHistoryObject(String type, String data) {
		this.type = type;
		this.data = data;
		this.url = "";
		this.extraType = "";
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

