package com.alipay.android.longlink;

/**
 * 长连接建立成功与否回调
 */
interface ISocketResultNotifer {
	void onSocketCreateSuccess();
	
	void onSocketCreateFail();
}
