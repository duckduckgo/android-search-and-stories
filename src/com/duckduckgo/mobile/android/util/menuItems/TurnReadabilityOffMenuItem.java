package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOffEvent;
import com.duckduckgo.mobile.android.util.Item;

public class TurnReadabilityOffMenuItem extends Item {

	public TurnReadabilityOffMenuItem(Context context, String url){
		//super(context.getResources().getString(R.string.ReadabilityOff), R.drawable.icon_readability_off, ItemType.READABILITY_OFF);
        super(context.getResources().getString(R.string.ReadabilityOff), R.drawable.icon_readability_off, ItemType.SAVE);
		this.EventToFire = new TurnReadabilityOffEvent(url);
	}
	
}
