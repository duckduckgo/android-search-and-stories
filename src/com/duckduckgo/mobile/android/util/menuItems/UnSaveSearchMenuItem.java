package com.duckduckgo.mobile.android.util.menuItems;

import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class UnSaveSearchMenuItem extends Item {
	private DuckDuckGo context;
	private String query;

	public UnSaveSearchMenuItem(DuckDuckGo context, String query){
		super(context.getResources().getString(R.string.Unsave), android.R.drawable.ic_menu_delete, ItemType.UNSAVE);
		this.context = context;
		this.query = query;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				final long delHistory = DDGApplication.getDB().deleteSavedSearch(query);
				if(delHistory != 0) {							
					context.syncAdapters();
				}	
				Toast.makeText(context, R.string.ToastUnSaveSearch, Toast.LENGTH_SHORT).show();
			};
		};
	}
}
