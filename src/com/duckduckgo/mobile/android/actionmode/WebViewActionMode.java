package com.duckduckgo.mobile.android.actionmode;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.duckduckgo.mobile.android.R;

public class WebViewActionMode implements ActionMode.Callback {

	@Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Create the menu from the xml file
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Here, you can checked selected items to adapt available actions
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        mode.finish();
        return true;
    }

	@Override
	public void onDestroyActionMode(ActionMode mode) {		
	}
	
}
