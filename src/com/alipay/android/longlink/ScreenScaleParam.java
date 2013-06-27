package com.alipay.android.longlink;

public class ScreenScaleParam {
	private int scaleHeight = 320;
	private int scaleWeight = 240;
	private float barcodeSizeScale = 0.65f;
	private float qrodeSizeScale = 0.9f;

	public int getScaleHeight() {
		return scaleHeight;
	}

	public void setScaleHeight(int scaleHeight) {
		this.scaleHeight = scaleHeight;
	}

	public int getScaleWeight() {
		return scaleWeight;
	}

	public void setScaleWeight(int scaleWeight) {
		this.scaleWeight = scaleWeight;
	}

	public float getCurScale() {
		return (float)scaleHeight/(float)scaleWeight;
	}

	public float getBarcodeSizeScale() {
		return barcodeSizeScale;
	}

	public void setBarcodeSizeScale(float barcodeSizeScale) {
		this.barcodeSizeScale = barcodeSizeScale;
	}

	public float getQrodeSizeScale() {
		return qrodeSizeScale;
	}

	public void setQrodeSizeScale(float qrodeSizeScale) {
		this.qrodeSizeScale = qrodeSizeScale;
	}
}
