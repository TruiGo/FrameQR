package com.alipay.android.nfd;

import java.util.List;

import android.os.Bundle;

public interface NFDBaseCallback {
	void binderStartActivity(String packageName, String className, int iCallingPid,Bundle bundle,IClientObserverPeer nfdService);

	void binderRefresh(List<String> clientInfos,IClientObserverPeer nfdService);

	void binderRegistReslut(int resultCode,IClientObserverPeer nfdService);
}
