package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOnEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Item;

public class TurnReadabilityOnMenuItem extends Item {
	
	public TurnReadabilityOnMenuItem(Context context, FeedObject feedObject){
		//super(context.getResources().getString(R.string.ReadabilityOn), R.drawable.icon_readability_on, ItemType.READABILITY_ON);
        super(context.getResources().getString(R.string.ReadabilityOn), R.drawable.icon_readability_on, ItemType.SAVE);
		this.EventToFire = new TurnReadabilityOnEvent(feedObject);
	}
	
}
