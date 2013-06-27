package com.alipay.android.appHall.uiengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.alipay.android.client.AnimationData;

public class AnimRelativeLayout extends RelativeLayout {
    private Bitmap mBitmap;
    /**
     * 运动块初始点的Y坐标
     */
    private int mInitY;
    /**
     * 运动块的高度
     */
    private int mInitH;
    private Matrix mCacheMatrix;
    private Matrix mTargetMatrix;

    /**
     * 运动块对应的控件id，R.Id.xxx
     */
    private int mTargetWidget;
    private View mMovedWidget;
    /**
     * 运动块所对应的目标块的坐标
     */
    private int[] mTargetPosition = new int[2];
    /**
     * 当前视图的坐标
     */
    private int[] mCurrentPosition = new int[2];

    private RectF mTop;
    private RectF mBottom;

    public AnimRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AnimRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimRelativeLayout(Context context) {
        super(context);
    }

    public Animation playInAnim(Animation.AnimationListener animationListener) {
        mCacheMatrix = new Matrix();
        mTargetMatrix = new Matrix();
        InAnimation animation = new InAnimation();
        configAnim(animationListener, animation);
        return animation;
    }

    public Animation playOutAnim(Animation.AnimationListener animationListener) {
        mCacheMatrix = new Matrix();
        mTargetMatrix = new Matrix();
        OutAnimation animation = new OutAnimation();
        configAnim(animationListener, animation);
        return animation;
    }

    private void configAnim(Animation.AnimationListener animationListener, Animation animation) {
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        if (animationListener != null)
            animation.setAnimationListener(animationListener);
        animation.setDuration(600);
        startAnimation(animation);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mTargetWidget != 0)
            findViewById(mTargetWidget).getLocationOnScreen(mTargetPosition);
        getLocationOnScreen(mCurrentPosition);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child != mMovedWidget) {
            return  super.drawChild(canvas, child, drawingTime);
        } else {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                canvas.save();
                canvas.clipRect(mTop);
                canvas.drawBitmap(mBitmap, mCacheMatrix, null);
                canvas.restore();
                canvas.save();
                canvas.setMatrix(mTargetMatrix);
            }
            boolean ret = super.drawChild(canvas, child, drawingTime);
            if (mBitmap != null && !mBitmap.isRecycled()) {
                canvas.restore();
                canvas.save();
                canvas.clipRect(mBottom);
                canvas.drawBitmap(mBitmap, mCacheMatrix, null);
                canvas.restore();
            }
            return ret;
        }
    }

    private void computeCache() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mTop = new RectF(0, 0, mBitmap.getWidth(), mInitY + mInitH - mCurrentPosition[1]);
            mCacheMatrix.mapRect(mTop);
            mBottom = new RectF(0, mInitY + mInitH - mCurrentPosition[1], mBitmap.getWidth(),
                mBitmap.getHeight());
            mCacheMatrix.mapRect(mBottom);
        }

    }

    public void setAnimData(AnimationData animationData) {
        mBitmap = animationData.getBitmap();
        mInitY = animationData.getInitY();
        mInitH = animationData.getInitH();
    }

    public void setTargetWidget(int widget) {
        mTargetWidget = widget;
    }

    public void setMovedWidget(int widget) {
        mMovedWidget = findViewById(widget);
    }

    private class InAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                int bitmapHeight = mBitmap.getHeight();
                //从下到上,mInitY-mTargetPosition[1]+mCurrentPosition[1]->mCurrentPosition[1]
                mTargetMatrix.setTranslate(0, mInitY - mTargetPosition[1]+mCurrentPosition[1]
                        + (mTargetPosition[1] - mInitY) * interpolatedTime);
                //从上到下,0>bitmapHeight
                mCacheMatrix.setTranslate(0,  bitmapHeight * interpolatedTime);
                computeCache();
            }
        }

    }

    private class OutAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                int bitmapHeight = mBitmap.getHeight();
                //从上到下,mCurrentPosition[1]->mInitY-mTargetPosition[1]+mCurrentPosition[1]
                mTargetMatrix.setTranslate(0,mCurrentPosition[1]+(mInitY - mTargetPosition[1]) * interpolatedTime);
                //从下到上,bitmapHeight>0
                mCacheMatrix.setTranslate(0, bitmapHeight - bitmapHeight
                                             * interpolatedTime);
                computeCache();
            }
        }

    }

}
