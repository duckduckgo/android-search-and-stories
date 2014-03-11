package com.duckduckgo.mobile.android.download;

import android.widget.TextView;

public class Holder {
	public final TextView textViewTitle;
	public final AsyncImageView imageViewBackground;
	public final AsyncImageView imageViewFeedIcon; // Bottom Left Icon (Feed Source)
		
	public Holder(final TextView textViewTitle, final AsyncImageView imageViewBackground,
				  final AsyncImageView imageViewFeedIcon) {
		this.textViewTitle = textViewTitle;
		this.imageViewBackground = imageViewBackground;
		this.imageViewFeedIcon = imageViewFeedIcon;
	}
}