package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.content.Intent;

import com.duckduckgo.mobile.android.R;

/*
 * A class that, well, shares stuff :)
 */
public class Sharer {
	
	  public static void shareWebPage(Context context, String title, String url) {
		  Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Story link via DuckDuckGo for Android: "+ url);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
			sendIntent.setType("text/plain");
			context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.SharePage)));
	  }
	  
	  public static void shareStory(Context context, String title, String url) {
		  Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, formatShareText(context, title, url));
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
			sendIntent.setType("text/plain");
			context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.ShareStory)));
	  }

	private static String formatShareText(Context context, String title, String url) {
		return String.format(context.getResources().getString(R.string.ShareFormat), title, url);
	}
	  
	  public static void shareSavedSearch(Context context, String query) {
		  shareSavedSearch(context, query, "https://duckduckgo.com/?q=" + query);
	  }
	  
	  public static void shareSavedSearch(Context context, String query, String url) {
		  Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Saved search via DuckDuckGo for Android: " + url);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Saved search: " + query);
			sendIntent.setType("text/plain");
			context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.ShareSearch)));
	  }

}
