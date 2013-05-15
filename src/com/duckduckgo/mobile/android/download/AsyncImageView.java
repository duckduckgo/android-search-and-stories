package com.duckduckgo.mobile.android.download;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.DDGUtils;

//TODO: Instead of using DownloadDrawable, we can just subclass ImageView with an AsyncImageView or some such...
public class AsyncImageView extends ImageView {
	private boolean hideOnDefault = false;
	public String type = null;
	
	/**
	   * The corner radius of the view (in pixel).
	   */
	private float cornerRadius = 0;
	
	private int layoutWidth = 0, layoutHeight = 0;
	
	public AsyncImageView(Context context, AttributeSet attr) {
		super (context, attr);
	    getXMLAttribute(context, attr);
	}
	
	public AsyncImageView(Context context, AttributeSet attr, int defStyle) {
		super (context, attr, defStyle);
	    getXMLAttribute(context, attr);
	}
	
	public AsyncImageView(Context context) {
		super(context);
	}
	
	  /**
	   * Get parameters in xml layout.
	   * @param context
	   * @param attrs
	   */
	  private void getXMLAttribute(Context context, AttributeSet attrs) {
	    // Get proportion.
	    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AsyncImageView);
	    cornerRadius = a.getDimension(R.styleable.AsyncImageView_cornerRadius, 0);		   
	    a.recycle();
	  }
	  
	  /**
	   * Set corner radius.
	   * @param radius Corder radius in pixel.
	   */
	  public void setCornerRadius(int radius) {
	    this.cornerRadius = radius;
	  }
	
	public void setDefault() {
		setImageBitmap(null);
		if (hideOnDefault) {
			this.setVisibility(View.GONE);
		}
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
		                                               
		if(cornerRadius != 0) {
			ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
			layoutWidth = layoutParams.width;   
			layoutHeight = layoutParams.height;
			
			Bitmap roundedImage = DDGUtils.getRoundedCornerImage(bitmap, cornerRadius, layoutWidth, layoutHeight);
			if(roundedImage != null) {
				setImageBitmap(roundedImage);
			}
			else {
				// TODO take a look at scaleCenterCrop method to fix this failure
				setDefault();
			}
			return;
		}
		
		setImageBitmap(bitmap);         
	}

	
	public boolean shouldHideOnDefault() {
		return this.hideOnDefault;
	}
	
	//NOTE: Setting Hide on default gives visibility control to this ImageView
	//		It may then override other visibility settings given externally
	public void setShouldHideOnDefault(boolean hideOnDefault) {
		this.hideOnDefault = hideOnDefault;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}	

}
