package com.alipay.android.client.safepay;

import android.os.Handler;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;
import com.alipay.android.safepay.MobileSecurePayHelper;

public class SafePayHelper {

	/**
	 * 支付
	 * 
	 * @param activity
	 * @param context
	 * @param handler
	 * @param externToken
	 * @param bizType
	 */
	public static void payDeal(final RootActivity activity, Handler handler,
			final String tradeNO, final String externToken,
			final String partnerID, final String bizType, String bizSubType) {
		// 调起快捷支付需要刷新交易记录列表
		AlipayApplication application = (AlipayApplication) activity
				.getApplicationContext();
		application.setRecordsRefresh(true);

		if (bizSubType != null)
			bizSubType = bizSubType.toLowerCase();
		MobileSecurePayHelper mph = new MobileSecurePayHelper();
		mph.payDeal(activity, handler, null, tradeNO, externToken, partnerID,
				bizType, bizSubType);
	}
	
	public static void callSafepayService(final RootActivity activity,Handler handler,final String strInfo) {
		MobileSecurePayHelper mph = new MobileSecurePayHelper();
		mph.pay(activity,handler,null,strInfo);
	}
}
