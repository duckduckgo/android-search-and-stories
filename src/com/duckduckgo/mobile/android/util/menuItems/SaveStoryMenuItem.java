package com.duckduckgo.mobile.android.util.menuItems;

import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class SaveStoryMenuItem extends Item {
	
	private DuckDuckGo context;
	private FeedObject feedObject;

	public SaveStoryMenuItem(DuckDuckGo context, FeedObject feedObject){
		super(context.getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE);
		this.context = context;
		this.feedObject = feedObject;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				context.itemSaveFeed(feedObject, null);
				context.syncAdapters();
				Toast.makeText(context, R.string.ToastSaveStory, Toast.LENGTH_SHORT).show();
			};
		};
	}
}
