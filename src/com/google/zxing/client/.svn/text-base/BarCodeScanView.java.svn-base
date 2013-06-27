package com.google.zxing.client;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.eg.android.AlipayGphone.R;

public class BarCodeScanView extends FrameLayout {
	private Animation scanAnimationBegin;
	private ImageView mScanBox;
	private ImageView mScanBar;

	public BarCodeScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		mScanBox = new ImageView(getContext());
//		mScanBox.setImageResource(R.drawable.scan_box);
//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//		this.addView(mScanBox, lp);
//
//		 lp =new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		// lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		mScanBar = new ImageView(getContext());
//		mScanBar.setImageResource(R.drawable.scan_08);
//		this.addView(mScanBar, lp);
		 LayoutInflater flater = LayoutInflater.from(context);
		 View view = flater.inflate(R.layout.barscanview, this);
		 mScanBox = (ImageView) view.findViewById(R.id.bar_scan_box);
		 mScanBar = (ImageView) view.findViewById(R.id.bar_scan_bar);

	}

	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		startScanAnimation();
	}

	public void startScanAnimation() {

		if (scanAnimationBegin == null) {
			scanAnimationBegin = new TranslateAnimation(0, 0, mScanBox.getTop(), mScanBox.getBottom() - mScanBar.getMeasuredHeight());
			scanAnimationBegin.setDuration(1500);
			scanAnimationBegin.setFillAfter(true);
			scanAnimationBegin.setRepeatMode(TranslateAnimation.REVERSE);
			scanAnimationBegin.setRepeatCount(TranslateAnimation.INFINITE);
			scanAnimationBegin.setInterpolator(new AccelerateDecelerateInterpolator());
			mScanBar.startAnimation(scanAnimationBegin);
		}

	}

	public void endScanAnimation() {
		mScanBar.clearAnimation();
		scanAnimationBegin = null;
	}
}
