package com.duckduckgo.mobile.android.download;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.duckduckgo.mobile.android.tasks.DownloadBitmapTask;

//TODO: Instead of using DownloadDrawable, we can just subclass ImageView with an AsyncImageView or some such...
public class AsyncImageView extends ImageView implements DownloadableImage {
	private WeakReference<DownloadBitmapTask> downloadTaskReference;
	private boolean hideOnDefault = false;
	
	public AsyncImageView(Context context, AttributeSet attr) {
		super (context, attr);
	}
	
	public AsyncImageView(Context context) {
		super(context);
	}
	
	public void setDownloadBitmapTask(DownloadBitmapTask task) {
		downloadTaskReference = new WeakReference<DownloadBitmapTask>(task);
	}
	
	public DownloadBitmapTask getDownloadBitmapTask() {
		if (downloadTaskReference != null) {
			return downloadTaskReference.get();
		}
		return null;
	}
	
	public void setBitmap(Bitmap bitmap) {
		//Don't show a null bitmap
		if (bitmap == null) {
			setDefault();
			return;
		}
		if (this.getVisibility() == View.GONE && this.hideOnDefault) {
			this.setVisibility(View.VISIBLE);
		}
		
		setImageBitmap(bitmap);
	}
	
	public void setDefault() {
		setImageBitmap(null);
		if (hideOnDefault) {
			this.setVisibility(View.GONE);
		}
	}
	
	public boolean shouldHideOnDefault() {
		return this.hideOnDefault;
	}
	
	//NOTE: Setting Hide on default gives visibility control to this ImageView
	//		It may then override other visibility settings given externally
	public void setShouldHideOnDefault(boolean hideOnDefault) {
		this.hideOnDefault = hideOnDefault;
	}
}
