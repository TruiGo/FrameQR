package com.alipay.android.client.lifePayment;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 字母索引List定制
 * @author caidie.wang
 *
 */
public class PaymentCityLetterListView extends View {
	
	OnTouchingLetterChangedListener onTouchingLetterChangedListener;//字母触摸监听
	String[] showStrArray = null;//需要显示的字母列表数组
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;

	public PaymentCityLetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PaymentCityLetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PaymentCityLetterListView(Context context) {
		super(context);
	}
	/**
	 * 设置索引字母数组
	 * @param letterStrArray
	 */
	public void setShowStrArray(Vector<String> letterStrArray){
		showStrArray = new String[letterStrArray.size()];
		for(int i=0;i<letterStrArray.size();i++){
			showStrArray[i] = (String)letterStrArray.elementAt(i);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int titleHeight = 41;
		int topMargin = 5;
	    int height = getHeight()-titleHeight-topMargin;
	    int width = getWidth();
	    int singleHeight = height / showStrArray.length;
	    for(int i=0;i<showStrArray.length;i++){
	       paint.setColor(Color.parseColor("#999999"));
	       paint.setTextSize(16);
	       paint.setTypeface(Typeface.DEFAULT_BOLD);
	       paint.setAntiAlias(true);
	       if(i == choose){
	    	   paint.setColor(Color.WHITE);
	    	   paint.setFakeBoldText(true);
	       }
	       float xPos = width/2  - paint.measureText(showStrArray[i])/2;
	       float yPos = singleHeight * i + singleHeight;
	       canvas.drawText(showStrArray[i], xPos, yPos, paint);
	       paint.reset();
	    }
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
	    final float y = event.getY();
	    final int oldChoose = choose;
	    final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
	    final int c = (int) (y/getHeight()*showStrArray.length);
	    
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				showBkg = true;
				if(oldChoose != c && listener != null){
					if(c >= 0 && c< showStrArray.length){
						listener.onTouchingLetterChanged(showStrArray[c]);
						choose = c;
						invalidate();
					}
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				if(oldChoose != c && listener != null){
					if(c >= 0 && c< showStrArray.length){
						listener.onTouchingLetterChanged(showStrArray[c]);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				showBkg = false;
				choose = -1;
				invalidate();
				break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}
	
}
