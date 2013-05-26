package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;

public class WelcomeScreenView extends LinearLayout {
	
	Context context;
	
	Button getStartedButton;
	
	OnClickListener closeListener;
	
	public WelcomeScreenView(Context context) {
		super(context);
		this.context = context;
		initView(context);
	}

	public WelcomeScreenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView(context);	
	}
	
	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.welcome, this, true);		
    	getStartedButton = (Button) findViewById(R.id.getStartedBut); 
	}
	
	public void setOnCloseListener(OnClickListener closeListener) {
		this.closeListener = closeListener;
		if(closeListener != null) {
	    	getStartedButton.setOnClickListener(closeListener);
		}
	}

}
