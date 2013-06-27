package com.alipay.android.ui.card;

import android.os.Bundle;

import com.alipay.android.ui.framework.RootController;
/**
 * 我的银行卡管理
 * @author caidie.wang
 *
 */
public class CardManagerRootControllerActivity extends RootController{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navigateTo("CardListView");
	}
	
	@Override
	protected String getControllerClassPath() {
		return CardManagerRootControllerActivity.class.getPackage().getName();
	}

}
