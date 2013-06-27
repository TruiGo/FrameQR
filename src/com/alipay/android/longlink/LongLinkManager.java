package com.alipay.android.longlink;

import com.alipay.android.client.AlipayPushMsgData;

public interface LongLinkManager {
	void bindService();
	void unBindService();
	void sendPacket(AlipayPushMsgData msgData);
	void readPacket();
}
