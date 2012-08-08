package com.duckduckgo.mobile.android.util;

public enum SCREEN {
	SCR_NEWS_FEED(0), SCR_RECENT_SEARCH(1);
	
	private int code;
	
	private SCREEN(int c) {
		   code = c;
		 }
		 
	public int getCode() {
		   return code;
	}
	
	public static SCREEN getByCode(int code){
		switch(code){
			case 0:
				return SCR_NEWS_FEED;
			case 1:
				return SCR_RECENT_SEARCH;
			default:
				return SCR_NEWS_FEED;
		}	
			
	}
}