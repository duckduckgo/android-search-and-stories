package com.duckduckgo.mobile.android.adapters;

import java.util.List;

import com.duckduckgo.mobile.android.util.DDGControlVar;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {
	
	int textViewResourceId;

	public CustomArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.textViewResourceId = textViewResourceId;
	}
	
	public CustomArrayAdapter(Context context,
			int listLayout, int textViewResourceId,
			List<T> list) {
		super(context, listLayout, textViewResourceId, list);
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  View cv = super.getView(position, convertView, parent);
		
		((TextView) cv.findViewById(textViewResourceId)).setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.recentTextSize);
		
		return cv;
	}

}
