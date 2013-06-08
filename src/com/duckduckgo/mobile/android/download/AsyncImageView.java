package com.duckduckgo.mobile.android.download;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.duckduckgo.mobile.android.R;
import com.squareup.picasso.Picasso;

//TODO: Instead of using DownloadDrawable, we can just subclass ImageView with an AsyncImageView or some such...
public class AsyncImageView extends ImageView {
	private boolean hideOnDefault = false;
	public String type = null;
	
	/**
	   * The corner radius of the view (in pixel).
	   */
	private float cornerRadius = 0;
	
	private boolean usePicasso = true;
		
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
	    usePicasso = a.getBoolean(R.styleable.AsyncImageView_usePicasso, true);		 
	    a.recycle();
	  }
	  
	  /**
	   * Set corner radius.
	   * @param radius Corder radius in pixel.
	   */
	  public void setCornerRadius(int radius) {
	    this.cornerRadius = radius;
	  }
	  
	  public float getCornerRadius() {
		    return this.cornerRadius;
		  }
	
	
	public void setBitmap(Bitmap bitmap) {
		if(bitmap == null)
			return;
		
		setImageBitmap(bitmap);         
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(usePicasso) {
			Picasso.with(getContext()).cancelRequest(this);
		}
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		
		if(visibility != View.VISIBLE && usePicasso) {
			Picasso.with(getContext()).cancelRequest(this);
		}
	}

}
