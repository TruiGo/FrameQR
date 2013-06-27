package com.alipay.android.comon.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.eg.android.AlipayGphone.R;

public class LockView extends View {
	public static int MINSELECTED = 4;
	
    public interface OnLockInputListener {

        void onLockDone(String path);
        void onLockInput(int indexUnlocked);
    }
    
	private Paint mPaint;
	
	private int iPointSmallRadius = 20;
	private int iPointBigRadius = 20;
	private int iPointMarginBetween = 60;
	private int iPointMarginStartX = 50;
	private int iPointMarginStartY = 50;
	private int iCount = 9;
	private int iCountPerLine = 3;
	private int iPointBit = 0;
	private int[] iPointArray;
	private int iCurCount;
	private boolean iDrawLineNeeded = false;
	private boolean iDone = false;
	private Point iCurPointerPoint = new Point();
	private boolean iQuitOnDone = false;
	private OnLockInputListener mOnLockInputListener = null;

	private Drawable mGridFocused = null;
	private Drawable mGridNormal = null;
	int mGridMargin = 0;
	int mGridWidth = 0;
	int mGridHeight = 0;
	int mGridBetweenX = 0;
	int mGridBetweenY = 0;
	private int mGridRadius = 0;
	private boolean mDensityLow = false;
	
	public LockView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
    public LockView(Context context, AttributeSet attrs) {
    	super(context, attrs, 0);

    	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LockView);
    	mGridFocused = a.getDrawable(R.styleable.LockView_gridFocused);
    	mGridNormal = a.getDrawable(R.styleable.LockView_gridNormal);
    	
    	a.recycle();
    	
    	init();
    }
    
    public LockView(Context context, AttributeSet attrs, int defStyle) {
    	super(context);
    	init();
    }
    
    public void setQuitOnDone(boolean quitOnDone) {
    	iQuitOnDone = quitOnDone;
    }
    
    public void setOnLockInputListener(OnLockInputListener l) {
    	mOnLockInputListener = l;
    }
    
    public void clear() {
		iPointBit = 0;
		iDone = true;
		iDone = false;
		if (0 != iCurCount) {
			iCurCount = 0;
			this.invalidate();
		}
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			iDone = true;
			if (null != mOnLockInputListener)
			{
				Log.v("Main", "LockActivity:mOnLockListener.onLock");
				String path = "";
				for (int i = 0; i < iCurCount; i++) {
					path += iPointArray[i];
				}
				if (path.length() > 0) {
					mOnLockInputListener.onLockDone(path);
				}
			}
			//iPointBit = 0;
			//iPointArray = null;
			//iCurCount = 0;
			iDrawLineNeeded = false;
			this.invalidate();
			result = true;
			break;
		case MotionEvent.ACTION_DOWN:
			if (iDone) {
				iPointBit = 0;
				//iPointArray = null;
				iCurCount = 0;
				iDone = false;
			}
			result = checkDrawNeeded(event.getX(), event.getY());
			if (result){
				this.invalidate();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			result = true;
			break;
		case MotionEvent.ACTION_MOVE:
			iDrawLineNeeded = false;
			final int historySize = event.getHistorySize();
	        for (int i = 0; i < historySize + 1; i++) {
	            final float x = i < historySize ? event.getHistoricalX(i) : event.getX();
	            final float y = i < historySize ? event.getHistoricalY(i) : event.getY();
	            
	            iDrawLineNeeded |= checkDrawNeeded(x, y);
	        }
	        
			//iDrawLineNeeded = checkDrawNeeded(event.getX(), event.getY());
			if (iDrawLineNeeded){
				iCurPointerPoint.x = (int) event.getX();
				iCurPointerPoint.y = (int) event.getY();
				this.invalidate();
			}

			break;
		default:
			result = true;
			break;
		}

		return true;//super.onTouchEvent(event);
	}
	
    private void init() {   

        mPaint = new Paint();   
        mPaint.setAntiAlias(true);   
  
        //mPaint.setTextSize(16);   
        //mPaint.setTextAlign(Paint.Align.RIGHT);  
        
        iPointArray = new int[iCount];
        for (int i = 0; i < iCount; i++) {
        	iPointArray[i] = -1;
        }
        iCurCount = 0;
        
        DisplayMetrics dmDisplayMetrics = getResources().getDisplayMetrics();
        if (DisplayMetrics.DENSITY_HIGH > dmDisplayMetrics.densityDpi || 728 == dmDisplayMetrics.heightPixels) {
        	mDensityLow = true;
            mPaint.setStrokeWidth(9); 
        } 
        //meizu
        else if (960 == dmDisplayMetrics.heightPixels && 640 == dmDisplayMetrics.widthPixels) {
        	mDensityLow = true;
            mPaint.setStrokeWidth(18); 
        }
        else {
            mPaint.setStrokeWidth(18); 
        }
    }  

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//		int height = 2 * iPointMarginStartY + iPointMarginBetween * (iCount / iCountPerLine - 1);
//		iPointMarginBetween = width / iCountPerLine;
//		iPointMarginStartX = iPointMarginBetween / 2;
//		iPointMarginStartY = iPointMarginStartX;
//		iPointBigRadius = (iPointMarginStartX + iPointSmallRadius) / 2;
//
//		setMeasuredDimension(width, height);
		
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		mGridWidth = width / (2 * iCountPerLine - 1);
		mGridHeight = mGridWidth;
		mGridMargin = (width - mGridWidth * iCountPerLine) / (2 * (iCountPerLine - 1) + 2);
		mGridBetweenX = mGridWidth + 2 * mGridMargin;

		mGridBetweenY = mGridHeight + (mDensityLow ? 1 : 2) *mGridMargin;
		mGridRadius = mGridWidth / 2;
		int height = mGridBetweenY * (iCountPerLine - 1) + mGridHeight + (mDensityLow ? 0 : 2) *mGridMargin;;
		setMeasuredDimension(width, height);
		mGridFocused.setBounds(0, 0, mGridWidth, mGridHeight);
		mGridNormal.setBounds(0, 0, mGridWidth, mGridHeight);
	}
	
	private boolean checkDrawNeeded(float x, float y) {
		for (int i = 0; i < iCount; i++) {
			if (isAvailable(i)) {
//				if (CheckInCircle(x, y, i)) {
				if (CheckInGrid(x, y, i)) {
					if (iCurCount > 0) {
						int indexMissing = detectCircleMissing(iPointArray[iCurCount - 1], i);
						if (-1 != indexMissing) {
							hit(indexMissing);
							Log.v("indexMissing", "="+indexMissing);
						}
					}
					
					hit(i);
					Log.v("indexMissing", "i="+i);
					break;
				}
			}
		}
		
		return iCurCount > 0;
	}
	
	private void hit(int index) {
		iPointArray[iCurCount++] = index;
		iPointBit |= (1 << (index + 1));
	}
	
	private boolean isAvailable(int index) {
		return 0 == (iPointBit & (1 << (index + 1)));
	}
	
	private int detectCircleMissing(int indexBefore, int curIndex) {
		final int xBefore = indexBefore % 3;
		final int yBefore = indexBefore / 3;
		final int xCur = curIndex % 3;
		final int yCur = curIndex / 3;
		
		if(0 == (xBefore - xCur) % 2 &&
				0 == (yBefore - yCur) % 2) {
			int tempIndex = (indexBefore + curIndex) /2;
			if (isAvailable(tempIndex)) {
				return tempIndex;
			}
		}
		
		return -1;
	}
	
	private boolean CheckInCircle(float x, float y, int index) {
		int xPoint = iPointMarginStartX + iPointMarginBetween * (index % iCountPerLine);
		int yPoint = iPointMarginStartY + iPointMarginBetween * (index / iCountPerLine);
		
		int deltaX = (int) (xPoint - x);
		int deltaY = (int) (yPoint - y);

		double deltaR = Math.sqrt(deltaX*deltaX+deltaY*deltaY);

		return deltaR <= iPointBigRadius;
	}
	
	private boolean CheckInGrid(float x, float y, int index) {
		int xPoint = mGridMargin + mGridBetweenX * (index % iCountPerLine) + mGridWidth / 2;
		int yPoint = (mDensityLow ? 0 : mGridMargin) + mGridBetweenY * (index / iCountPerLine) + mGridWidth / 2;
		
		int deltaX = (int) (xPoint - x);
		int deltaY = (int) (yPoint - y);

		double deltaR = Math.sqrt(deltaX*deltaX+deltaY*deltaY);

		return deltaR <= mGridRadius;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.save();
		//canvas.translate(getLeft(), getTop());
		//canvas.drawRect(20, 0, 50, 80, mPaint);
		//canvas.drawCircle(10, 0, 5, mPaint);

//		for (int i = 0; i < iCount; i++) {
//			drawPoint(canvas, i);
//		}
//		
//		for (int i = 0; i < iCurCount - 1; i++) {
//			canvas.drawLine(GetPointX(iPointArray[i]), GetPointY(iPointArray[i]), 
//					GetPointX(iPointArray[i + 1]), GetPointY(iPointArray[i + 1]), mPaint);
//		}
//
//		if (iDrawLineNeeded && iCurCount > 0) {
//			canvas.drawLine(GetPointX(iPointArray[iCurCount - 1]), GetPointY(iPointArray[iCurCount - 1]), 
//					iCurPointerPoint.x, iCurPointerPoint.y, mPaint);
//		}
//		
//		super.onDraw(canvas);
		
		for (int i = 0; i < iCount; i++) {
			drawGrid(canvas, i);
		}
		
		for (int i = 0; i < iCurCount - 1; i++) {
			canvas.drawLine(GetGridX(iPointArray[i]), GetGridY(iPointArray[i]), 
					GetGridX(iPointArray[i + 1]), GetGridY(iPointArray[i + 1]), mPaint);
		}

		if (iDrawLineNeeded && iCurCount > 0) {
			mPaint.setColor(Color.argb(51, 255, 142, 9));
			canvas.drawLine(GetGridX(iPointArray[iCurCount - 1]), GetGridY(iPointArray[iCurCount - 1]), 
					iCurPointerPoint.x, iCurPointerPoint.y, mPaint);
		}
		super.onDraw(canvas);
	}
	
	private int GetPointX(int index) {
		return iPointMarginStartX + iPointMarginBetween * (index % iCountPerLine);
	}
	
	private int GetPointY(int index) {
		return iPointMarginStartY + iPointMarginBetween * (index / iCountPerLine);
	}
	
	private int GetGridX(int index) {
		return mGridMargin + mGridBetweenX * (index % iCountPerLine) + mGridRadius;
	}
	
	private int GetGridY(int index) {
		return (mDensityLow ? 0 : mGridMargin) + mGridBetweenY * (index / iCountPerLine) + mGridRadius;
	}
	
	private void drawPoint(Canvas canvas, int index) {
		float x = iPointMarginStartX + iPointMarginBetween * (index % iCountPerLine);
		float y = iPointMarginStartY + iPointMarginBetween * (index / iCountPerLine);
		
		int occupied = iPointBit & (1 << (index + 1));
		if (occupied > 0)
		{
			mPaint.setColor(Color.RED);
			//canvas.drawArc(new RectF(0, 0, 100, 100), 0, 360, false, mPaint);
			canvas.drawCircle(x, y, iPointBigRadius, mPaint);
			mGridFocused.draw(canvas);
		}
		else
		{
			mPaint.setColor(Color.BLACK);
			canvas.drawCircle(x, y, iPointSmallRadius, mPaint);
		}
		//canvas.drawCircle(x, y, iPointSmallRadius, mPaint);
	}
	
	private void drawGrid(Canvas canvas, int index) {
		float x = mGridMargin + mGridBetweenX * (index % iCountPerLine);
		float y = (mDensityLow ? 0 : mGridMargin) + mGridBetweenY * (index / iCountPerLine);
		
		int occupied = iPointBit & (1 << (index + 1));
		
		canvas.save();
		canvas.translate(x, y);
		if (occupied > 0)
		{
			mGridFocused.draw(canvas);
		}
		else
		{
			mGridNormal.draw(canvas);
		}
		
		canvas.restore();
		//canvas.drawCircle(x, y, iPointSmallRadius, mPaint);
	}
    
}
