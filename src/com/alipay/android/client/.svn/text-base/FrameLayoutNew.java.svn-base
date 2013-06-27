package com.alipay.android.client;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.eg.android.AlipayGphone.R;

public class FrameLayoutNew extends FrameLayout
{
	public FrameLayoutNew(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}

	@Override public void draw(Canvas canvas)
	{
		super.draw(canvas);
		
		Drawable foreground = this.getContext().getResources()
								.getDrawable(R.drawable.framelayout_foreground);
		foreground.setBounds(0, 0, 320, 1);
		foreground.draw(canvas);
	}
}
