package com.duckduckgo.mobile.android.adapters;

import java.util.ArrayList;

import com.duckduckgo.mobile.android.util.DDGControlVar;

import android.content.Context;
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
			ArrayList<T> arrayList) {
		super(context, listLayout, textViewResourceId, arrayList);
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cv = convertView;
		if(convertView == null) {
		  cv = super.getView(position, convertView, parent);
		}
		
		((TextView) cv.findViewById(textViewResourceId)).setTextSize(DDGControlVar.recentTextSize);
		
		return cv;
	}

}
