package com.alipay.android.comon.component;

import com.eg.android.AlipayGphone.R;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class LockIndicator extends View {

	public static int MAXTOTAL = 32;
	private int mXCount = 3;
	private int mYCount = 3;
	private int mPathBit = 0;
	private int mGridWidth = 40;
	private int mGridHeight = 40;
	private int mGridMarginX = 5;
	private int mGridMarginY = 5;
	private int mStrokeWidth = 3;
	
	private Paint mPaint = null;
	
	private Drawable mGridFocused = null;
	private Drawable mGridNormal = null;
	
	public LockIndicator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
    public LockIndicator(Context context, AttributeSet attrs) {
    	super(context, attrs, 0);
    	
    	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LockIndicator);
    	mGridFocused = a.getDrawable(R.styleable.LockIndicator_gridFocused);
    	mGridNormal = a.getDrawable(R.styleable.LockIndicator_gridNormal);
    	
    	a.recycle();
    	init();
    }
    
    public void clear() {
    	if (0 != mPathBit) {
        	mPathBit = 0;
        	this.invalidate();
    	}
    }
    
    private void init() {
        mPaint = new Paint();   
        mPaint.setAntiAlias(true);   
        mPaint.setStrokeWidth(mStrokeWidth); 
        mPaint.setStyle(Paint.Style.STROKE);
        
        if (null != mGridNormal) {
			mGridWidth = mGridNormal.getIntrinsicHeight();
			mGridHeight = mGridNormal.getIntrinsicHeight();
			mGridMarginX = mGridWidth / 2;
			mGridMarginY = mGridHeight / 4;
			
	        mGridNormal.setBounds(0, 0, mGridWidth, mGridHeight);
	        mGridFocused.setBounds(0, 0, mGridWidth, mGridHeight);
        }

    }
    
    public void setPath(String path) {
    	int length = path.length();
    	mPathBit = 0;
    	for (int i = 0; i < length; i++) {
    		mPathBit |= (1 << (path.charAt(i) - '0'));	
    	}
    	
    	if (mPathBit > 0) {
    		this.invalidate();
    	}
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (null != mGridNormal) {
			int width = mXCount * mGridWidth + mGridMarginX * (mXCount - 1);
			int height = mYCount * mGridHeight +  + mGridMarginY * (mYCount - 1);
			setMeasuredDimension(width, height);
		}
//		else {
//			width = mGridWidth * (mXCount + 1) + mGridBetween * (mXCount - 1) + mStrokeWidth;
//			height = mGridHeight * (mYCount + 1) + mGridBetween * (mYCount - 1) + mStrokeWidth;
//		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		if (mXCount * mYCount > MAXTOTAL) {
//			return ;
//		}
//		
//		if (null == mGridNormal || null == mGridFocused) {
//			return ;
//		}
//		
//		mPaint.setColor(Color.BLACK);
//		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
//		//canvas.drawCircle(getLeft() + 5, getTop() + 5, 5, mPaint);
//		int x = 0, y = 0;
//		for (int i = 0; i < mXCount; i++) {
//			for (int j = 0; j < mYCount; j++) {
//				mPaint.setColor(Color.BLACK);
//				x = (int)(/*getLeft() + */(i + 0.5 ) * mGridWidth + (i + 1) * mGridBetween / 2);
//				y = (int)(/*getTop() + */(j + 0.5 ) * mGridHeight + (j + 1) * mGridBetween);
//				
//				if (0 != (mPathBit & (1 << (i + j * mXCount)))) {
//					mPaint.setColor(Color.YELLOW);
//				}
//				else {
//					mPaint.setColor(Color.BLACK);
//				}
//				
//				canvas.drawRect(x, y, x + mGridWidth, y + mGridHeight, mPaint);
//			}
//		}
		if (mXCount * mYCount > MAXTOTAL) {
			return ;
		}
		
		if (null == mGridNormal || null == mGridFocused) {
			return ;
		}
		
		int x = 0, y = 0;
		for (int i = 0; i < mXCount; i++) {
			for (int j = 0; j < mYCount; j++) {
				mPaint.setColor(Color.BLACK);
				x = (int)(/*getLeft() + */i * mGridWidth + i * mGridMarginX);
				y = (int)(/*getTop() + */j * mGridHeight + j * mGridMarginY);
				
				canvas.save();
				canvas.translate(x, y);
				if (0 != (mPathBit & (1 << (i + j * mXCount)))) {
					mGridFocused.draw(canvas);
				}
				else {
					mGridNormal.draw(canvas);
				}
				
				canvas.restore();
			}
		}
	}
}
