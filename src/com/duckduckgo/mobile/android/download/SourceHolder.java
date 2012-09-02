package com.duckduckgo.mobile.android.download;

import android.widget.CheckBox;
import android.widget.TextView;

public class SourceHolder {
	public final TextView textViewTitle;
	public final AsyncImageView imageViewBackground;
	public final CheckBox checkbox;
	
	public String id;
	
	public SourceHolder(final TextView textViewTitle, final AsyncImageView imageViewBackground,
				  final CheckBox checkbox) {
		this.textViewTitle = textViewTitle;
		this.imageViewBackground = imageViewBackground;
		this.checkbox = checkbox;
	}
}