package com.alipay.android.ui.widget;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

import com.alipay.android.appHall.Helper;

public class MyDrawer extends SlidingDrawer{
	DisplayMetrics metrics;
	
	public MyDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MyDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		metrics = Helper.getDisplayMetrics(getContext());	
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#animateClose()
	 */
	@Override
	public void animateClose() {
		// TODO Auto-generated method stub
		super.animateClose();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#animateOpen()
	 */
	@Override
	public void animateOpen() {
		// TODO Auto-generated method stub
		super.animateOpen();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#animateToggle()
	 */
	@Override
	public void animateToggle() {
		// TODO Auto-generated method stub
		super.animateToggle();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#getContent()
	 */
	@Override
	public View getContent() {
		// TODO Auto-generated method stub
		return super.getContent();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#getHandle()
	 */
	@Override
	public View getHandle() {
		// TODO Auto-generated method stub
		return super.getHandle();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#isMoving()
	 */
	@Override
	public boolean isMoving() {
		// TODO Auto-generated method stub
		return super.isMoving();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#isOpened()
	 */
	@Override
	public boolean isOpened() {
		// TODO Auto-generated method stub
		return super.isOpened();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#lock()
	 */
	@Override
	public void lock() {
		// TODO Auto-generated method stub
		super.lock();
	}
	
	private View mHandle;
	
	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mHandle = this.getHandle();
		mHandle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleDrawer();				
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#open()
	 */
	@Override
	public void open() {
		// TODO Auto-generated method stub
		super.open();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#toggle()
	 */
	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		super.toggle();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#unlock()
	 */
	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		super.unlock();
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#setOnDrawerCloseListener(android.widget.SlidingDrawer.OnDrawerCloseListener)
	 */
	@Override
	public void setOnDrawerCloseListener(
			OnDrawerCloseListener onDrawerCloseListener) {
		// TODO Auto-generated method stub
		super.setOnDrawerCloseListener(onDrawerCloseListener);
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#setOnDrawerOpenListener(android.widget.SlidingDrawer.OnDrawerOpenListener)
	 */
	@Override
	public void setOnDrawerOpenListener(
			OnDrawerOpenListener onDrawerOpenListener) {
		// TODO Auto-generated method stub
		super.setOnDrawerOpenListener(onDrawerOpenListener);
	}

	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#setOnDrawerScrollListener(android.widget.SlidingDrawer.OnDrawerScrollListener)
	 */
	@Override
	public void setOnDrawerScrollListener(
			OnDrawerScrollListener onDrawerScrollListener) {
		// TODO Auto-generated method stub
		super.setOnDrawerScrollListener(onDrawerScrollListener);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.SlidingDrawer#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return super.onInterceptTouchEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
			toggleDrawer();
			return false;
		}
		return super.onTouchEvent(event);
//		return false;
	}

	public void toggleDrawer() {
		if (isOpened()){
			close();
		}else{
			open();
		}
		
		try {
			Field trackingField = SlidingDrawer.class.getDeclaredField("mTracking");
			trackingField.setAccessible(true);
			trackingField.set(this,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 是否竖向超过半屏
	 * @param event
	 * @return
	 */
	/*private boolean isHalfLess(MotionEvent event){
		if(event == null)
			return true;
		float y = event.getY();
		return y > 0 && y < metrics.heightPixels / 2 - 60;
	}*/

	private boolean isInRect(MotionEvent event,View view) {
		Rect outRect = new Rect();
		view.getHitRect(outRect);
		float x = event.getX();
        float y = event.getY();
        return outRect.contains((int) x, (int) y);
	}
}
