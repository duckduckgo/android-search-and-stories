package com.duckduckgo.mobile.android.actions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.utils.URLEncodedUtils;

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
	
	static private String getQueryFromURL(String url) {
		try {
			URI searchUrl = new URI(url);
			List<NameValuePair> params =URLEncodedUtils.parse(searchUrl, "UTF-8");
	  		for(NameValuePair param : params) {
	  			if(param.getName().equals("q"))
	  				return param.getValue();
	  		}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	static public SaveEvent getSaveEvent(SESSIONTYPE sessionType, FeedObject feedObject, String webViewUrl) {
		switch(sessionType) {
	  	  case SESSION_SEARCH:
	  		  String query = getQueryFromURL(webViewUrl);
	  		  if(query != null) {
	  			  return new SaveSearchEvent(query);
	  		  }
	  	  case SESSION_FEED:
	  		  return new SaveStoryEvent(feedObject);
		  }
		return null;
	}
	
	
}
