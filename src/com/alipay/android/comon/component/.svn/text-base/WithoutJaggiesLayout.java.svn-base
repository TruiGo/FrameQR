package com.alipay.android.comon.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class WithoutJaggiesLayout extends RelativeLayout {
	private PaintFlagsDrawFilter drawFilter;
	private Paint mPaint;
	
	public WithoutJaggiesLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);		
		mPaint = new Paint();
		mPaint.setDither(false);
		mPaint.setAlpha(1);
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
	}

	public WithoutJaggiesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WithoutJaggiesLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.setDrawFilter(drawFilter);
//		canvas.drawPaint(mPaint);
		super.draw(canvas);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		// TODO Auto-generated method stub
		return super.drawChild(canvas, child, drawingTime);
	}
}
