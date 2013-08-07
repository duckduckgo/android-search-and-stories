package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOnEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Item;

public class TurnReadabilityOnMenuItem extends Item {
	
	public TurnReadabilityOnMenuItem(DuckDuckGo context, FeedObject feedObject){
		super(context.getResources().getString(R.string.ReadabilityOn), R.drawable.icon_readability_on, ItemType.READABILITY_ON);	
		this.EventToFire = new TurnReadabilityOnEvent(feedObject);
	}
	
}
