package com.duckduckgo.mobile.android.download;

import android.support.v7.widget.SwitchCompat;
import android.widget.CheckBox;
import android.widget.TextView;

public class SourceHolder {
	public final TextView textViewTitle;
	public final TextView textViewDescription;
	public final AsyncImageView imageViewBackground;
	public final SwitchCompat switchCompat;
	
	public String id;
	
	public SourceHolder(final TextView textViewTitle, 
			final TextView textViewDescription, 
			final AsyncImageView imageViewBackground,
			final SwitchCompat switchCompat) {
		this.textViewTitle = textViewTitle;
		this.textViewDescription = textViewDescription;
		this.imageViewBackground = imageViewBackground;
		this.switchCompat = switchCompat;
	}
}