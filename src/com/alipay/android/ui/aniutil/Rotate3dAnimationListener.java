package com.alipay.android.ui.aniutil;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public final class Rotate3dAnimationListener implements Animation.AnimationListener {
	private boolean front;
	private View mContainer;
	private RotateAnimationListener mRotateAnimationListener;

	public Rotate3dAnimationListener(boolean front, View container) {
		this.front = front;
		this.mContainer = container;
	}

	public RotateAnimationListener getmRotateAnimationListener() {
		return mRotateAnimationListener;
	}

	public void setmRotateAnimationListener(RotateAnimationListener mRotateAnimationListener) {
		this.mRotateAnimationListener = mRotateAnimationListener;
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mContainer.post(new SwapView(front));
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	
	
	class SwapView implements Runnable {
		private boolean front;

		public SwapView(boolean front) {
			this.front = front;
		}

		public void run() {
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			Rotate3dAnimation rotation;

			if (front) {
				if(mRotateAnimationListener != null){
					mRotateAnimationListener.onFrontToBack();
				}
				rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 310.0f, false);
			} else {
				if(mRotateAnimationListener != null){
					mRotateAnimationListener.onBackToFront();
				}
				rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
			}

			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			if (front) {
				rotation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if(mRotateAnimationListener != null){
							mRotateAnimationListener.onRotateFinished();
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
			}

			mContainer.startAnimation(rotation);
		}
	}
}

