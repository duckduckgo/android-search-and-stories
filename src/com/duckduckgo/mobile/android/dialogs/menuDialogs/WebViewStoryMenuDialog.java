package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedSearchMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.WebViewStoryMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;
import com.duckduckgo.mobile.android.objects.FeedObject;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class WebViewStoryMenuDialog extends AlertDialog.Builder{
	public WebViewStoryMenuDialog(final DuckDuckGo context, FeedObject feedObject, boolean webViewIsInReadabilityMode) {
		super(context);

        final boolean isPageSaved = DDGApplication.getDB().isSaved(feedObject.getId());
        final PageMenuContextAdapter contextAdapter  = new WebViewStoryMenuAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1,
                feedObject, isPageSaved, webViewIsInReadabilityMode);

        setTitle(R.string.StoryOptionsTitle);
        setAdapter(contextAdapter, new ExecuteActionOnClickListener(contextAdapter));
	}
}
