package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class SeekBarHint extends SeekBar {
	Paint p;
	String extraText = null; 
	
	private static int THUMB_TEXT_SIZE = 30;
	
	int viewWidth;
	int barHeight;
	
  public SeekBarHint (Context context) {
      super(context);
      p = new Paint();
      p.setColor(Color.WHITE);
      p.setStyle(Style.FILL);
      p.setTextSize(THUMB_TEXT_SIZE);
  }

  public SeekBarHint (Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      p = new Paint();
      p.setColor(Color.WHITE);
      p.setStyle(Style.FILL);
      p.setTextSize(THUMB_TEXT_SIZE);
  }

  public SeekBarHint (Context context, AttributeSet attrs) {
      super(context, attrs);
      p = new Paint();
      p.setColor(Color.WHITE);
      p.setStyle(Style.FILL);
      p.setTextSize(THUMB_TEXT_SIZE);
  }
  
  public void setExtraText(String text) {
	  extraText = text;
  }
  
  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
   {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);

          setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) (THUMB_TEXT_SIZE*1.5f));
          viewWidth = getMeasuredWidth();
          barHeight = getMeasuredHeight();// returns only the bar height (without the label);

  }
  
  @Override
  protected void onDraw(Canvas c) {
	  if(extraText != null && extraText.length() != 0) {
	      int thumb_x = (int) ( ( ((float) this.getProgress()) / this.getMax() ) * (viewWidth - getPaddingLeft() - getPaddingRight()) );
//	      int middle = (int) ((float) barHeight) / 2;	      
          int progressPosX = thumb_x;
	      
	      c.drawText(extraText, progressPosX, getBottom()-getPaddingRight()/2, p);
	  }
      super.onDraw(c);
  }
}