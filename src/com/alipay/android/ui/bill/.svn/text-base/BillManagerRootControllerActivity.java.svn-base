package com.alipay.android.ui.bill;

import android.content.Intent;
import android.os.Bundle;

import com.alipay.android.ui.framework.RootController;

public class BillManagerRootControllerActivity extends RootController{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int lifePayCount = 0;
		Intent intent = getIntent();
		if(intent != null && intent.hasExtra("LifePayCount"))
		{
			lifePayCount = intent.getExtras().getInt("LifePayCount");
		}
		
		navigateTo("BillListView", lifePayCount);
	}

	@Override
	protected String getControllerClassPath() {
		return BillManagerRootControllerActivity.class.getPackage().getName();
	}

}
