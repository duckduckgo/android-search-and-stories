package com.duckduckgo.mobile.android.objects;

public class PageTypes {
	public static final String StoryWithReadability = "FR";
	public static final String StoryWithoutReadability = "F";
	public static final String Query = "R";
	public static final String WebPage = "W";
	
	public static Boolean isStory(String pageType){
		return pageType.equals(StoryWithReadability)||
				pageType.equals(StoryWithoutReadability);
	}
	
	public static Boolean isStoryWithReadabiliy(String pageType){
		return pageType.equals(StoryWithReadability);
	}
	
	public static Boolean isWebPage(String pageType){
		return pageType.equals(WebPage);
	}
	
	public static Boolean isQuery(String pageType){
		return pageType.equals(Query);
	}
}
