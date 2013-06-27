package com.alipay.android.ui.personlcard;

import android.os.Bundle;

import com.alipay.android.ui.framework.RootController;

public class GetAvatarActivity extends RootController{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navigateTo("GetAvatarView", null);
	}

	@Override
	protected String getControllerClassPath() {
		return GetAvatarActivity.class.getPackage().getName();
	}
}