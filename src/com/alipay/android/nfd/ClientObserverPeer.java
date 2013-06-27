package com.alipay.android.nfd;

/**
 * 参与者注册接口
 * @author daping.gp
 * @version $Id: ClientObserver.java, v 0.1 2012-6-19 下午3:34:09 daping.gp Exp $
 */
public interface ClientObserverPeer {
/**
 * 注册函数
 * @param clientInfo 注册信息
 * @return
 */
public boolean regist(String clientInfo,int type,String bizType);

/**
 * 关闭注册信息
 * @return
 */
public boolean unRegist();

}
