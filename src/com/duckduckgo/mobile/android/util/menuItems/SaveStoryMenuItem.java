package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Item;

public class SaveStoryMenuItem extends Item {

	public SaveStoryMenuItem(Context context, FeedObject feedObject){
		super(context.getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE);
		EventToFire = new SaveStoryEvent(feedObject);
	}

}
