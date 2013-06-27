package com.alipay.android.ui.quickpay;

import android.os.Bundle;
import android.view.KeyEvent;

import com.alipay.android.ui.framework.RootController;

public class QuickPayRootControllerActivity extends RootController {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		needLogin = true;
		super.onCreate(savedInstanceState);
		navigateTo("SendSoundView");
	}

	@Override
	protected String getControllerClassPath() {
		return QuickPayRootControllerActivity.class.getPackage().getName();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 this.pop();
			 return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
}
