package com.alipay.android.ui.voucher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.ui.framework.RootController;

public class VoucherIndexActivity extends RootController{
	String couponId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		needLogin = true;
		super.onCreate(savedInstanceState);
		if(getUserData() != null){
			Intent intent = getIntent();
			couponId = intent.getStringExtra("voucherId");
			navigateTo("VoucherListView",couponId);
		}
	}
	
	@Override
	protected String getControllerClassPath() {
		return VoucherIndexActivity.class.getPackage().getName();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constant.REQUEST_LOGIN_BACK && resultCode == Activity.RESULT_OK){
			navigateTo("VoucherListView",couponId);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(mControllerStack.size() > 0){
			mControllerStack.peek().onNewIntent();
		}
	}
}