package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;


public class ReloadMenuItem  extends Item {
	private DuckDuckGo context;

	public ReloadMenuItem(DuckDuckGo context){
		super(context.getResources().getString(R.string.Refresh), R.drawable.icon_reload, ItemType.REFRESH);
		this.context = context;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				context.reloadAction();
			};
		};
	}
}