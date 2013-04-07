package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class TurnReadabilityOffMenuItem extends Item {
	private DuckDuckGo context;
	private String url;

	public TurnReadabilityOffMenuItem(DuckDuckGo context, String url){
		super(context.getResources().getString(R.string.ReadabilityOff), R.drawable.icon_readability_off, ItemType.READABILITY_OFF);
		this.context = context;
		this.url = url;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				context.mainWebView.forceOriginal();
				context.showWebUrl(url);
			};
		};
	}
}
