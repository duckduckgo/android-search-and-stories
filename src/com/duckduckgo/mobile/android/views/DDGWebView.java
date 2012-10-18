package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class DDGWebView extends WebView {
	
	OnTouchListener extraTouchListener;

	public DDGWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		attrSet = attrs;
	}

	public boolean is_gone=true;
	public AttributeSet attrSet = null;
	
	public void onWindowVisibilityChanged(int visibility)
	       {super.onWindowVisibilityChanged(visibility);
	        if (visibility==View.GONE)
	           {try
	                {WebView.class.getMethod("onPause").invoke(this);//stop flash
	                }
	            catch (Exception e) {}
	            this.pauseTimers();
	            this.is_gone=true;
	           }
	        else if (visibility==View.VISIBLE)
	             {try
	                  {WebView.class.getMethod("onResume").invoke(this);//resume flash
	                  }
	              catch (Exception e) {}
	              this.resumeTimers();
	              this.is_gone=false;
	             }
	       }
	
	public AttributeSet getAttributes() {
		return attrSet;
	}
	
	public void setExtraTouchListener(OnTouchListener listener) {
		extraTouchListener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {            
	    boolean consumed = super.onTouchEvent(event);
	    if(extraTouchListener != null) {
	    	extraTouchListener.onTouch(this, event);
	    }
	    return consumed;
	}
	
//	public void onDetachedFromWindow()
//	       {//this will be trigger when back key pressed, not when home key pressed
//	        if (this.is_gone)
//	           {try
//	               {this.destroy();
//	               }
//	            catch (Exception e) {}
//	           }
//	       }

}
