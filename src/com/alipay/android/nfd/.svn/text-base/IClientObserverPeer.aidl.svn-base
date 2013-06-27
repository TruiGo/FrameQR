package com.alipay.android.nfd;

import java.util.List;

import com.alipay.android.nfd.IClientCallback;

/**
 * 参与者和发现者操作接口
 * @author daping.gp
 * @version $Id: ClientObserver.java, v 0.1 2012-6-18 下午3:58:06 daping.gp Exp $
 */
interface IClientObserverPeer {
    /**
     * 解除注册信息
     * @return
     */
    boolean unRegist();
    /**
     * 发现者获取参与者list信息
     * @return
     */
    void discoverClientInfo(String bizType);
    /**
     * 注册回调通知函数
     * @param callback
     */
    void registCallback(IClientCallback callback);
    
    boolean isDiscovering();
    void encryptRegist(String reginfo,String param,String bizType);
    
    boolean modifyBluetoothName();
    
    void businessExit();
    
    boolean hasRegisted();
    
    boolean isDiscoverTimeout();
}
