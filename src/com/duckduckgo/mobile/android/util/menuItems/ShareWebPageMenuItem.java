package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Sharer;

public class ShareWebPageMenuItem extends Item {
	private final DuckDuckGo context;
	private String pageUrl;
	private String pageData;
	
	public ShareWebPageMenuItem(DuckDuckGo context, String pageData, String pageUrl){
		super(context.getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE);
		this.context = context;
		this.pageData = pageData;
		this.pageUrl = pageUrl;
		
		this.ActionToExecute = getActionToExecute();
	}

	/** 
	 * we're actually going to call this directly instead of the public field ActionToExecute, after more refactoring
	 * @return
	 */
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				Sharer.shareWebPage(context, pageUrl, pageUrl);
			};
		};
	}
}
