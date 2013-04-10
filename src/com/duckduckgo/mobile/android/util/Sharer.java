package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.content.Intent;

import com.duckduckgo.mobile.android.R;

/*
 * A class that, well, shares stuff :)
 */
public class Sharer {

	public static void shareWebPage(Context context, String title, String url) {
		Intent shareIntent = createBasicShareIntent(url + " via DuckDuckGo for Android", title);
		context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.SharePage)));
	}

	public static void shareStory(Context context, String title, String url) {
		Intent shareIntent = createBasicShareIntent(formatShareText(context, title, url), title);
		context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.ShareStory)));
	}

	public static void shareSearch(Context context, String query) {
		String url = "https://duckduckgo.com/?q=" + query;
		Intent shareIntent = createBasicShareIntent(String.format("%s %s via DuckDuckGo for Android", query, url),
				String.format("DuckDuckGo Search for \"%s\"", query));
		context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.ShareSearch)));
	}
	
	private static Intent createBasicShareIntent(String text, String subject) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		return shareIntent;
	}

	private static String formatShareText(Context context, String title, String url) {
		return String.format(context.getResources().getString(R.string.ShareFormat), title, url);
	}

}
