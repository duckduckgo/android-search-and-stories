package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.activity.KeyboardService;

/**
 * Created by fgei on 11/10/14.
 */
public class DDGDrawerLayout extends DrawerLayout implements DrawerLayout.DrawerListener{

	private final DuckDuckGo context;
	private boolean enabled;
	private KeyboardService keyboardService;
	private View mainView;
	private View menuView;
	//private

	public DDGDrawerLayout(Context context) {
		super(context);
		Log.e("aaa", "ddg drawer layout initttt");
		this.context = (DuckDuckGo) context;
		this.keyboardService = new KeyboardService(this.context);
		this.setDrawerListener(this);
		//initViews();
	}

	public DDGDrawerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (DuckDuckGo) context;
		this.keyboardService = new KeyboardService(this.context);
		this.setDrawerListener(this);
		//initViews();
	}

	public DDGDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = (DuckDuckGo) context;
		this.keyboardService = new KeyboardService(this.context);
		this.setDrawerListener(this);
		//initViews();
	}

	public void setViews(View mainView, View menuView) {
		this.mainView = mainView;
		this.menuView = menuView;
		setMenuWidth();
	}

	public void setMenuWidth() {
		int width = getResources().getDisplayMetrics().widthPixels;
		DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) menuView.getLayoutParams();
		float newWidth = width * 0.806f;
		params.width = (int) newWidth;
		//params.width = 10;
		menuView.setLayoutParams(params);
	}

	public void lockDrawer() {
		setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void unlockDrawer() {
		setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}


	public void openDrawer(View drawerView) {
		keyboardService.hideKeyboardDelayed(context.getSearchField());
		super.openDrawer(drawerView);
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		keyboardService.hideKeyboardDelayed(context.getSearchField());
	}

	@Override
	public void onDrawerClosed(View drawerView) {
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
	}

	@Override
	public void onDrawerStateChanged(int newState) {
	}

}
