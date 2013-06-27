package com.alipay.android.longlink;

import com.alipay.android.longlink.IVerifyClientCallback;
import com.alipay.android.longlink.ISocketResultNotifer;

interface IVerifyLinkService {
	boolean initializePushLink(int netType, String clientDealDesc);
	boolean closePushLink();
	void registCallback(IVerifyClientCallback callback);
	void unRegistCallback(IVerifyClientCallback callback);
	void reConnect();
	boolean isSocketConnected();
	void registSocketNotifer(ISocketResultNotifer notifer);//socket连接结果通知
	void unRegistSocketNofiter();
}
