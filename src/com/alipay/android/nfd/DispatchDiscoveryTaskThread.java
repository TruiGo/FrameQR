package com.alipay.android.nfd;

import com.alipay.android.util.LogUtil;


/**
 * 发现者分配任务线程类
 * @author daping.gp
 * @version $Id: DispatchDiscoveryTaskThread.java, v 0.1 2012-6-21 下午4:35:48 daping.gp Exp $
 */
public class DispatchDiscoveryTaskThread extends Thread {

    /** 发现者接口 */
    ClientScanner      mClientScanner;
    NFDUtils mNfdUtil;
    String mBizType;
    private DiscoveredResultSet discoveredResultSet;
    long delay;
    public boolean timeout;

    public DispatchDiscoveryTaskThread(ClientScanner clientScanner,DiscoveredResultSet discoveredResultSet,String bizType, long delay) {
        this.mClientScanner = clientScanner;
        mClientScanner.setIsDiscoverFinished(false);
        this.discoveredResultSet = discoveredResultSet;
        mBizType = bizType;
        this.delay = delay;
    }

    @Override
    public void run() {
        super.run();
        if(delay != 0){
	        try {
	            Thread.sleep(delay);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
        
		mClientScanner.discoverClient(discoveredResultSet,mBizType);
    }
}
