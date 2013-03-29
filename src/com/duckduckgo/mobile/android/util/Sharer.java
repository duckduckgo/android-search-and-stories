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
			sendIntent.putExtra(Intent.EXTRA_TEXT, url + " via DuckDuckGo for Android");
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
	  
	  public static void shareSearch(Context context, String query) {
		  shareSearch(context, query, "https://duckduckgo.com/?q=" + query);
	  }
	  
	  public static void shareSearch(Context context, String query, String url) {
		  Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s %s via DuckDuckGo for Android", query, url));
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, String.format("DuckDuckGo Search for \"%s\"", query));
			sendIntent.setType("text/plain");
			context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.ShareSearch)));
	  }

}
