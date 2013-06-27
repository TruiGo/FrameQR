package com.alipay.android.ui.pushwebView;

import android.content.Intent;
import android.os.Bundle;

import com.alipay.android.ui.framework.RootController;

public class PushWebViewRootControllerActivity extends RootController{

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle params = getIntent().getExtras();
		navigateTo("PushWebView",params);
	}

	@Override
	protected String getControllerClassPath() {
		// TODO Auto-generated method stub
		return PushWebViewRootControllerActivity.class.getPackage().getName();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(mControllerStack.size() == 0){
			navigateTo("PushWebView");
		}
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(isNotEmptyStack()){
			mControllerStack.peek().onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private boolean isNotEmptyStack() {
		return !mControllerStack.isEmpty() && mControllerStack.peek() != null;
	}
}
