package com.alipay.android.client.manufacture;

import android.content.Context;

import com.eg.android.AlipayGphone.R;

public class ZtencUpgradeInterceptor implements AppUpgradeInterceptor {

	@Override
	public String execUpgrade(Context context) {
		return (String) context.getText(R.string.ZTEUpdateRemind);
	}
}
