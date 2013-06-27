package com.alipay.android.bizapp;

import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BasePage {
	protected FrameLayout mPageView=null;
	protected BaseBiz mBizObj=null;
	public Object params;
	public void onCreate(BaseBiz bizObj){
		mBizObj = bizObj;
	}
	public void init( Object params) {
        this.params = params;
    }
	public void onStart(){};
	public void onResume(){};
	public void onPause(){};
	public void onStop(){};
	public void onDestory(){};
	public void loadAllVariables(){};
	public boolean onKeyDown(int keyCode, KeyEvent event){return false;}
	public View getPageView(){
		return mPageView;
	}
}
