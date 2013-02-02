package com.duckduckgo.mobile.android.util;

public enum SCREEN {
	SCR_STORIES(0), SCR_RECENT_SEARCH(1), SCR_SAVED_FEED(2), SCR_SETTINGS(3);
	
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
				return SCR_STORIES;
			case 1:
				return SCR_RECENT_SEARCH;
			case 2:
				return SCR_SAVED_FEED;
			case 3:
				return SCR_SETTINGS;
			default:
				return SCR_STORIES;
		}	
			
	}
}