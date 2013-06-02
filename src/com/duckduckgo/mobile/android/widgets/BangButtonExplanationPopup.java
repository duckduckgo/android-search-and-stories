package com.duckduckgo.mobile.android.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class BangButtonExplanationPopup extends PopupWindow {
	protected BangButtonExplanationPopup(DuckDuckGo context, View explanationLayout) {
		super(explanationLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ImageView closeButton = (ImageView)explanationLayout.findViewById(R.id.closeButton);
        
        closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BangButtonExplanationPopup.this.dismiss();
			}
		});
	}
	
	public static BangButtonExplanationPopup showPopup(DuckDuckGo context, ImageButton bangButton){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.bangbutton_explanation_popup,
                (ViewGroup) context.findViewById(R.id.bangbuttonexplanation));
		BangButtonExplanationPopup popup = new BangButtonExplanationPopup(context, layout);
		popup.showAsDropDown(bangButton);
		return popup;
	}
}
