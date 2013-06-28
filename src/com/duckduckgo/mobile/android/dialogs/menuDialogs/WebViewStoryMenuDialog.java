package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.WebViewStoryMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;
import com.duckduckgo.mobile.android.objects.FeedObject;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class WebViewStoryMenuDialog extends AlertDialog.Builder{
	public WebViewStoryMenuDialog(final DuckDuckGo context, FeedObject feedObject, boolean webViewIsInReadabilityMode) {
		super(context);
		setTitle(R.string.StoryOptionsTitle);
		
		if(feedObject == null)
			return;

        final PageMenuContextAdapter contextAdapter  = new WebViewStoryMenuAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1,
                feedObject, webViewIsInReadabilityMode);        
        setAdapter(contextAdapter, new ExecuteActionOnClickListener(contextAdapter));
	}
}
