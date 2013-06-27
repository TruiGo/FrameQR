package com.alipay.android.client.lifePayment;

import android.view.View;

/**
 * 字母索引触摸监听
 * 
 * @author caidie.wang
 * 
 */
class LetterListViewListener implements
		OnTouchingLetterChangedListener {

	/**
     * 
     */
    private final PaymentCity LetterListViewListener;

    /**
     * @param paymentCity
     */
    LetterListViewListener(PaymentCity paymentCity) {
        LetterListViewListener = paymentCity;
    }

    @Override
	public void onTouchingLetterChanged(final String s) {
		if (LetterListViewListener.alphaIndexer.get(s) != null) {
			int position = LetterListViewListener.alphaIndexer.get(s);
			LetterListViewListener.cityList.setSelection(position);
			LetterListViewListener.overlay.setText(LetterListViewListener.sections[position]);
			LetterListViewListener.overlay.setVisibility(View.VISIBLE);
			LetterListViewListener.handler.removeCallbacks(LetterListViewListener.overlayThread);
			LetterListViewListener.handler.postDelayed(LetterListViewListener.overlayThread, 1500);
		}
	}

}