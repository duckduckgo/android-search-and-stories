package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.util.Item;

public class DeleteStoryInHistoryMenuItem extends Item {

	public DeleteStoryInHistoryMenuItem(DuckDuckGo context, String feedObjectId){
		super(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.DELETE);
		this.EventToFire = new DeleteStoryInHistoryEvent(feedObjectId);
	}

}
