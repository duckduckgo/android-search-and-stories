package com.duckduckgo.mobile.android.download;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Holder {
	public final Toolbar toolbar;
	public final TextView textViewTitle;
	public final TextView textViewCategory;
	public final AsyncImageView imageViewBackground;
	public final AsyncImageView imageViewFeedIcon; // Bottom Left Icon (Feed Source)
		
	public Holder(final Toolbar toolbar, final TextView textViewTitle,
				  final TextView textViewCategory, final AsyncImageView imageViewBackground,
				  final AsyncImageView imageViewFeedIcon) {
		this.toolbar = toolbar;
		this.textViewTitle = textViewTitle;
		this.textViewCategory = textViewCategory;
		this.imageViewBackground = imageViewBackground;
		this.imageViewFeedIcon = imageViewFeedIcon;
	}
}