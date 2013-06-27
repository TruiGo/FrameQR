package com.alipay.android.ui.personlcard;

import android.os.Bundle;

import com.alipay.android.ui.framework.RootController;

public class LargeAvatarActivity extends RootController {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navigateTo("LargeAvatarView", null);
	}

	@Override
	protected String getControllerClassPath() {
		return LargeAvatarActivity.class.getPackage().getName();
	}

}
