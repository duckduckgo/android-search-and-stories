package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class TurnReadabilityOnMenuItem extends Item {
	private DuckDuckGo context;
	private FeedObject feedObject;

	public TurnReadabilityOnMenuItem(DuckDuckGo context, FeedObject feedObject){
		super(context.getResources().getString(R.string.ReadabilityOn), R.drawable.icon_readability_on, ItemType.READABILITY_ON);
		this.context = context;
		this.feedObject = feedObject;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				context.launchReadableFeedTask(feedObject);
			};
		};
	}
}
