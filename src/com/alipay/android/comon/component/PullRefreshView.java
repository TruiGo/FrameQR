package com.alipay.android.comon.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li@alipay.com
 * 下拉刷新View
 *
 */
public class PullRefreshView extends FrameLayout implements OnGestureListener, AnimationListener {
    private static final byte STATE_CLOSE = 0;
    private static final byte STATE_OPEN = STATE_CLOSE + 1;
    private static final byte STATE_OVER = STATE_OPEN + 1;
    private static final byte STATE_OPEN_RELEASE = STATE_OVER + 1;
    private static final byte STATE_OVER_RELEASE = STATE_OPEN_RELEASE + 1;
    private static final byte STATE_REFRESH = STATE_OVER_RELEASE + 1;
    private static final byte STATE_REFRESH_RELEASE = STATE_REFRESH + 1;
    private byte mState;

    private GestureDetector mGestureDetector;
    private Flinger mFlinger;

    private Animation mReverseFlipAnimation;
    private Animation mFlipAnimation;
    private boolean mIndicatorUp;

    private RefreshListener mRefreshListener;

    private View mRefreshView;
    private View mLoadingView;

    private int mLastY;
    private int mMargin;
    private int mMaxMagin;

    private boolean mEnablePull;

    public PullRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(this);
        mFlinger = new Flinger();

        init();
        loadVariables();
    }
    
    private void loadVariables() {
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setAnimationListener(this);
        mReverseFlipAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setAnimationListener(this);
        mIndicatorUp = false;
    }

    private void init() {
        View mOverView = inflate(getContext(), R.layout.pull_refresh_header, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        addView(mOverView, params);
        mOverView.measure(0, 0);
        mMargin = mMaxMagin = mOverView.getMeasuredHeight();

        mRefreshView = mOverView.findViewById(R.id.refresh);
        mLoadingView = mOverView.findViewById(R.id.loading);
    }

    @Override
    public void onAnimationEnd(Animation anim) {
        ImageView indicator = (ImageView) getChildAt(0).findViewById(R.id.refresh_indicator);
        if (anim == mFlipAnimation) {
            indicator.setImageResource(R.drawable.pull_arrow_up);
        } else {
            indicator.setImageResource(R.drawable.pull_arrow_down);
        }
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {

    }

    @Override
    public boolean onDown(MotionEvent evn) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float disX, float disY) {
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (child instanceof AdapterView<?>) {
            if (((AdapterView<?>) child).getFirstVisiblePosition() != 0
                || (((AdapterView<?>) child).getFirstVisiblePosition() == 0
                    && ((AdapterView<?>) child).getChildAt(0) != null && ((AdapterView<?>) child)
                    .getChildAt(0).getTop() < 0))
                return false;
        }
        if ((mState == STATE_REFRESH && head.getTop() > 0 && disY > 0)
            || (child.getTop() <= 0 && disY > 0)) {
            return false;
        }
        if (mState == STATE_OPEN_RELEASE || mState == STATE_OVER_RELEASE
            || mState == STATE_REFRESH_RELEASE)
            return false;
        int speed = mLastY;
        if (head.getTop() >= 0)
            speed = mLastY / 2;
        boolean bool = moveDown(speed, true);
        mLastY = (int) -disY;
        return bool;
    }

    private void release(int dis) {
        if (mRefreshListener != null && dis > mMaxMagin) {
            mState = STATE_OVER_RELEASE;
            mFlinger.recover(dis - mMaxMagin);
        } else {
            mState = STATE_OPEN_RELEASE;
            mFlinger.recover(dis);
        }
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mEnablePull)
            return super.dispatchTouchEvent(ev);
        if (!mFlinger.isFinished())
            return false;
        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (head.getBottom() > 0) {
                if (mState == STATE_REFRESH && head.getBottom() > mMaxMagin) {
                    release(head.getBottom() - mMaxMagin);
                    return false;
                } else if (mState != STATE_REFRESH) {
                    release(head.getBottom());
                    return false;
                }
            }
        }
        boolean bool = mGestureDetector.onTouchEvent(ev);

        if ((bool || (mState != STATE_CLOSE && mState != STATE_REFRESH)) && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.dispatchTouchEvent(ev);
        }

        if (bool)
            return true;
        else
            return super.dispatchTouchEvent(ev);
    }

    /**
     * 自动滚动
     * 
     */
    private class Flinger implements Runnable {
        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        public Flinger() {
            mScroller = new Scroller(getContext());
            mIsFinished = true;
        }

        @Override
        public void run() {
            boolean b = mScroller.computeScrollOffset();
            if (b) {
                moveDown(mLastY - mScroller.getCurrY(), false);
                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                mIsFinished = true;
                removeCallbacks(this);
            }
        }

        public void recover(int dis) {
            if (dis <= 0)
                return;
            removeCallbacks(this);
            mLastY = 0;
            mIsFinished = false;
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        public boolean isFinished() {
            return mIsFinished;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View head = getChildAt(0);
        View child = getChildAt(1);
        int y = child.getTop();
        
        if (mState == STATE_REFRESH) {
            head.layout(0, 0, right, mMaxMagin);
            child.layout(0, mMaxMagin, right, mMaxMagin+child.getMeasuredHeight());
        } else {
            head.layout(0, y - mMargin, right, y);
            child.layout(0, y, right, y+child.getMeasuredHeight());
        }

        View other = null;
        for (int i = 2; i < getChildCount(); ++i) {
            other = getChildAt(i);
            other.layout(0, top, right, bottom);
        }
    }

    public boolean moveDown(int dis, boolean changeState) {
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + dis;
        if (childTop <= 0) {
            if (childTop < 0)
                dis = -child.getTop();
            head.offsetTopAndBottom(dis);
            child.offsetTopAndBottom(dis);
            if (mState != STATE_REFRESH)
                mState = STATE_CLOSE;
        } else if (childTop <= mMaxMagin) {
            if (mIndicatorUp) {
                playAnim(false);
            }
            head.offsetTopAndBottom(dis);
            child.offsetTopAndBottom(dis);
            if (changeState && mState != STATE_REFRESH)
                mState = STATE_OPEN;
            else if (childTop <= mMaxMagin && mState == STATE_OVER_RELEASE) {
                refresh();
            }
        } else if (mState != STATE_REFRESH) {
            if (!mIndicatorUp) {
                playAnim(true);
            }
            head.offsetTopAndBottom(dis);
            child.offsetTopAndBottom(dis);
            if (changeState)
                mState = STATE_OVER;
        }
        mMargin = child.getTop() - head.getTop();
        invalidate();
        return true;
    }

    /**
     * 刷新
     */
    private void refresh() {
        if (mRefreshListener != null) {
            mState = STATE_REFRESH;
            mRefreshView.setVisibility(GONE);
            mLoadingView.setVisibility(VISIBLE);
            mRefreshListener.onRefresh();
        }
    }

    /**
     * 刷新完成
     */
    public void refreshFinished() {
        View head = getChildAt(0);
        mRefreshView.setVisibility(VISIBLE);
        mLoadingView.setVisibility(GONE);
        if (head.getBottom() > 0) {
            mState = STATE_REFRESH_RELEASE;
            release(head.getBottom());
        } else
            mState = STATE_CLOSE;
    }
    
    public void setLoadingViewVisiable(boolean visiable){
    	mLoadingView.setVisibility(visiable ? VISIBLE : GONE);
    }

    private void playAnim(boolean bool) {
        ImageView indicator = (ImageView) getChildAt(0).findViewById(R.id.refresh_indicator);
        TextView textView = (TextView) getChildAt(0).findViewById(R.id.refresh_text);
        indicator.clearAnimation();
        if (bool) {
            mIndicatorUp = true;
            textView.setText(R.string.release_refresh);
            indicator.startAnimation(mFlipAnimation);
        } else {
            mIndicatorUp = false;
            textView.setText(R.string.pull_refresh);
            indicator.startAnimation(mReverseFlipAnimation);
        }

    }

    public void setEnablePull(boolean enablePull) {
        mEnablePull = enablePull;
    }

    /**
     * 设置刷新接口
     */
    public void setRefreshListener(RefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    /**
     * 刷新接口
     */
    public interface RefreshListener {
        /**
         * 刷新
         */
        public void onRefresh();
    }
}
