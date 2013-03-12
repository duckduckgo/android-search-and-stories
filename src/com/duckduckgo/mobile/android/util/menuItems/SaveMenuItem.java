package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class SaveMenuItem extends Item {
	
	private DuckDuckGo context;
	private FeedObject feedObject;

	public SaveMenuItem(DuckDuckGo context, FeedObject feedObject){
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
			};
		};
	}
}
