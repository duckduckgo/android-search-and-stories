package com.duckduckgo.mobile.android.actions;

import com.duckduckgo.mobile.android.events.externalEvents.ExternalEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SearchExternalEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;

public class CommonActions {

	static public ShareEvent getShareEvent(SESSIONTYPE sessionType, FeedObject feedObject, String webViewUrl) {
		switch(sessionType) {
	        case SESSION_FEED:
	      	  return new ShareFeedEvent(feedObject.getTitle(), feedObject.getUrl());
	        case SESSION_SEARCH:
	      	  final String query = DDGUtils.getQueryIfSerp(webViewUrl);
	      	  return new ShareSearchEvent(query);
	        case SESSION_BROWSE:
	      	  return new ShareWebPageEvent(webViewUrl, webViewUrl);
	    }
		return null;
	}
	
	static public ExternalEvent getExternalEvent(SESSIONTYPE sessionType, String webViewUrl) {
		switch(sessionType) {
	        case SESSION_SEARCH:
	      	  final String query = DDGUtils.getQueryIfSerp(webViewUrl);
	      	  return new SearchExternalEvent(query);
	        default:
	      	  return new SendToExternalBrowserEvent(webViewUrl);
        }
	}
	
	static public SaveEvent getSaveEvent(SESSIONTYPE sessionType, FeedObject feedObject, String webViewUrl) {
		switch(sessionType) {
	  	  case SESSION_SEARCH:
	  		  return new SaveSearchEvent(webViewUrl);
	  	  case SESSION_FEED:
	  		  return new SaveStoryEvent(feedObject);
		  }
		return null;
	}
	
	
}
