package com.duckduckgo.mobile.android.util;

public enum SCREEN {
	SCR_STORIES(0), SCR_RECENT_SEARCH(1), SCR_SAVED_FEED(2), SCR_DUCKMODE(3), SCR_WEBVIEW(4);
	
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
				return SCR_DUCKMODE;
			case 4:
				return SCR_WEBVIEW;
			default:
				return SCR_STORIES;
		}	
			
	}
	
	/**
	 * method to get associated ViewFlipper order in main.xml
	 * @return
	 */
	public int getFlipOrder() {
		switch(this) {
			case SCR_WEBVIEW:
				return 0;
			case SCR_STORIES:
				return 1;
			case SCR_RECENT_SEARCH:
				return 2;
			case SCR_DUCKMODE:
				return 3;
			case SCR_SAVED_FEED:
				return 4;
			default:
				return 1;
		}
	}
}