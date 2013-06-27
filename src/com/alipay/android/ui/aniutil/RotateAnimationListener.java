package com.alipay.android.ui.aniutil;

public interface RotateAnimationListener {
	//开始从正面到反面
	void onFrontToBack();
	//开始从反面到正面
	void onBackToFront();
	//正面到反面翻转完成
	void onRotateFinished();
	
	//....其它扩展
}
