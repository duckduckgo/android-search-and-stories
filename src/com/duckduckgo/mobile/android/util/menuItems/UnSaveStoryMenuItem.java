package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.util.Item;

public class UnSaveStoryMenuItem extends Item {
	
	public UnSaveStoryMenuItem(DuckDuckGo context, String feedObjectId){
		super(context.getResources().getString(R.string.Unsave), android.R.drawable.ic_menu_delete, ItemType.UNSAVE);
		this.EventToFire = new UnSaveStoryEvent(feedObjectId);
	}
	
}
