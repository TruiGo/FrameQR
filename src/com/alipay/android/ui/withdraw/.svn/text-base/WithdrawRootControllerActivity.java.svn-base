package com.alipay.android.ui.withdraw;

import android.content.Intent;
import android.os.Bundle;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.ui.framework.RootController;

public class WithdrawRootControllerActivity extends RootController{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent != null && intent.hasExtra(Constant.BANK_ID)){
			navigateTo("ApplyView",intent.getExtras().get(Constant.BANK_ID));
		}else{
			navigateTo("ApplyView");
		}
	}

	@Override
	protected String getControllerClassPath() {
		return WithdrawRootControllerActivity.class.getPackage().getName();
	}
}
