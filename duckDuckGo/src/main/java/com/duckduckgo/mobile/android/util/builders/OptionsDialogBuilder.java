package com.duckduckgo.mobile.android.util.builders;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.util.Item;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class OptionsDialogBuilder extends Builder {
	private PageMenuContextAdapter contextAdapter;

	public OptionsDialogBuilder(DuckDuckGo context, PageMenuContextAdapter contextAdapter, int title){
		super(context);
		setTitle(title);
		setContextAdapter(contextAdapter);
	}

    public OptionsDialogBuilder(DuckDuckGo context, int title){
        super(context);
        setTitle(title);
    }

    public void setContextAdapter(PageMenuContextAdapter contextAdapter){
    	this.contextAdapter = contextAdapter;
        setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Item clickedItem = OptionsDialogBuilder.this.contextAdapter.getItem(item);
                BusProvider.getInstance().post(clickedItem.EventToFire);
            }
        });
    }
}
