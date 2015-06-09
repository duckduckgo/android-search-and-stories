package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.util.Item;

public class DeleteStoryInHistoryMenuItem extends Item {

	public DeleteStoryInHistoryMenuItem(Context context, String feedObjectId){
		//super(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.DELETE);
        super(context.getResources().getString(R.string.remove), android.R.drawable.ic_menu_close_clear_cancel, ItemType.SAVE);
		this.EventToFire = new DeleteStoryInHistoryEvent(feedObjectId);
	}

}
