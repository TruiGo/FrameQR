package com.alipay.android.ui.voucher;

import org.json.JSONObject;

public interface VerifyResultNotifier {
	//核销成功回调
	void onSuccess(JSONObject response);
	//核销失败回调
	void onFail(JSONObject response);
}
