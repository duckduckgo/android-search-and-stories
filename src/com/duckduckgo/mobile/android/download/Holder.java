package com.duckduckgo.mobile.android.download;

import android.widget.TextView;

public class Holder {
	public final TextView textViewTitle;
	public final AsyncImageView imageViewBackground;
	public final AsyncImageView imageViewFeedIcon; //Top Right Icon (Feed Source)
	public final AsyncImageView imageViewUrlIcon; //Bottom Left Icon (Icon for linked page)
	
	public Holder(final TextView textViewTitle, final AsyncImageView imageViewBackground,
				  final AsyncImageView imageViewFeedIcon, final AsyncImageView imageViewUrlIcon) {
		this.textViewTitle = textViewTitle;
		this.imageViewBackground = imageViewBackground;
		this.imageViewFeedIcon = imageViewFeedIcon;
		this.imageViewUrlIcon = imageViewUrlIcon;
	}
}