package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOffEvent;
import com.duckduckgo.mobile.android.util.Item;

public class TurnReadabilityOffMenuItem extends Item {

	public TurnReadabilityOffMenuItem(DuckDuckGo context, String url){
		super(context.getResources().getString(R.string.ReadabilityOff), R.drawable.icon_readability_off, ItemType.READABILITY_OFF);
		this.EventToFire = new TurnReadabilityOffEvent(url);
	}
	
}
