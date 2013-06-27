package com.alipay.android.nfd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import android.os.RemoteException;

public class DiscoveredResultSet {
	private IClientCallback clientCallback;
	
	public DiscoveredResultSet(IClientCallback clientCallback) {
		this.clientCallback = clientCallback;
	}
	
	/** 参与者标示信息 */
    private Set<String> discoveredClients = new LinkedHashSet<String>();
    
    public void resetClientInfo() {
        discoveredClients.clear();
    }

    /**
     * 去重参与者客户端信息
     * @param lbsClientinfos lbs传递过来的客户端信息集合
     * @return
     */
    public synchronized void refreshClient(Set<String> clientInfos) {
        ArrayList<String> resultSet = new ArrayList<String>();
        if (clientInfos != null && clientInfos.size() > 0) {
            clientInfos.removeAll(discoveredClients);
            resultSet.addAll(clientInfos);
            discoveredClients.addAll(clientInfos);
        }
        
        try {
			clientCallback.refresh(resultSet);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    public synchronized void refreshClient(String clientInfo) {
        ArrayList<String> resultSet = new ArrayList<String>();
        if (clientInfo != null && !discoveredClients.contains(clientInfo)) {
            discoveredClients.add(clientInfo);
            resultSet.add(clientInfo);
        }
        
        try {
			clientCallback.refresh(resultSet);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
}
