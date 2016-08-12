package com.duckduckgo.mobile.android.util;

import android.graphics.Typeface;

public class DDGConstants {
	public static String USER_AGENT = "DDG-Android-%version";
//	public static final String USER_AGENT = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
	public static final String AUTO_COMPLETE_URL = "https://duckduckgo.com/ac/?q=";
	public static final String SEARCH_URL = "https://www.duckduckgo.com/?ko=-1&q=";
    public static final String SEARCH_URL_JAVASCRIPT_DISABLED = "https://duckduckgo.com/html/?q=";
    	public static final String SEARCH_URL_ONION = "http://3g2upl4pq6kufc4m.onion/?ko=-1&q=";
	public static final String MAIN_FEED_URL = "https://watrcoolr.duckduckgo.com/watrcoolr.js?o=json";
	public static final String SOURCES_URL = "https://watrcoolr.duckduckgo.com/watrcoolr.js?o=json&type_info=1";
	public static final String ICON_LOOKUP_URL = "http://i2.duck.co/i/";
	
	public static final String STORIES_JSON_PATH = "stories.json";
	public static final String SOURCE_JSON_PATH = "source.json";
	public static final String SOURCE_SIMPLE_PATH = "simple.kry";

//	public static Typeface TTF_HELVETICA_NEUE_MEDIUM = null;
	//public static Typeface TTF_ROBOTO_BOLD = null;
	//public static Typeface TTF_ROBOTO_MEDIUM = null;

    public static final int ALWAYS_INTERNAL = 0;
    public static final int ALWAYS_EXTERNAL = 1;

    public static final int CONFIRM_CLEAR_HISTORY = 100;
    public static final int CONFIRM_CLEAR_COOKIES = 200;
    public static final int CONFIRM_CLEAR_WEB_CACHE = 300;
}
