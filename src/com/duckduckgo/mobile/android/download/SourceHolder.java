package com.duckduckgo.mobile.android.download;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Utility class whose instance is used to memorize state in SourcesAdapter.
 */
public class SourceHolder {
	public final TextView textViewTitle;
	public final TextView textViewDescription;
	public final AsyncImageView imageViewBackground;
	public final View checkBox;
	
	public String id;
	
	public SourceHolder(final TextView textViewTitle, 
			final TextView textViewDescription, 
			final AsyncImageView imageViewBackground,
			final View checkBox) {
		this.textViewTitle = textViewTitle;
		this.textViewDescription = textViewDescription;
		this.imageViewBackground = imageViewBackground;
		this.checkBox = checkBox;
	}
}