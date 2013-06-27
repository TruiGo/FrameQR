package com.alipay.android.nfd;


/**
 * 发现者接口
 * @author daping.gp
 * @version $Id: ClientScanner.java, v 0.1 2012-6-19 下午3:32:29 daping.gp Exp $
 */
public interface ClientScanner {
    /**
     * 发现参与者
     * @param clientCallback 
     * @return
     */
    public boolean discoverClient(DiscoveredResultSet discoveredResultSet,String bizType);
    
    /**
     * 判断是否已经发现
     * @return
     */
    public boolean isDiscoverFinished();

    public void setIsDiscoverFinished(boolean finished);
    
    public void relaseResources();
}
