package com.duckduckgo.mobile.android.actionmode;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
