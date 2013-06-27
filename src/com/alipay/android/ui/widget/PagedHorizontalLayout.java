package com.alipay.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.alipay.android.appHall.Helper;

public class PagedHorizontalLayout extends ViewGroup{
	private DisplayMetrics mDisplayMetrics; 
	private Scroller mScroller;
	private int mCurrentPage;
	
	public PagedHorizontalLayout(Context context) {
		super(context);
		init(context);
	}
	
	public PagedHorizontalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context) {
		mDisplayMetrics = Helper.getDisplayMetrics(context);
		mScroller = new Scroller(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
		}
		
		super.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	@Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LinearLayout.LayoutParams(getContext(), attrs);
    }
	
	@Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LinearLayout.LayoutParams(p);
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				child.layout(i * mDisplayMetrics.widthPixels + getPaddingLeft(), 0, (i + 1) * mDisplayMetrics .widthPixels - getPaddingRight(), mDisplayMetrics.heightPixels);
			}
		}
	}
	
	public void snapToNextPage(int duration,final PageScrollListener listener){
		mScroller.startScroll((mCurrentPage++) * mDisplayMetrics.widthPixels, 0, mDisplayMetrics.widthPixels, 0,duration);
		invalidate();
		
		callListener(duration, listener);
	}

	public void snapToPreviousPage(int duration,PageScrollListener listener){
		mScroller.startScroll((mCurrentPage--) * mDisplayMetrics.widthPixels, 0, -mDisplayMetrics.widthPixels, 0,duration);
		invalidate();
		
		callListener(duration, listener);
	}
	
	private void callListener(int duration, final PageScrollListener listener) {
		postDelayed(new Runnable() {
			@Override
			public void run() {
				listener.onScrollFinish();
			}
		}, duration / 2);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}
	}
	
	public int getPageIndex(){
		return mCurrentPage;
	}
	
	public interface PageScrollListener{
		public void onScrollFinish();
	}
}
