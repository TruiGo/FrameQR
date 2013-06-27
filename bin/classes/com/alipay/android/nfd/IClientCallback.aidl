package com.alipay.android.nfd;

import java.util.List;
/**
 * 客户端远程回调通知函数
 * @author daping.gp
 * @version $Id: RemoteServiceCallback.java, v 0.1 2012-6-18 下午4:04:12 daping.gp Exp $
 */
interface IClientCallback {
    /**
     * 通知远程调用者已经近场发现完成并返回结果
     */
    void refresh(out List<String> clientInfos);
    
    void startActivity(String packageName, String className, int iCallingPid, in Bundle bundle);
    
    void registReslut(int resultCode);
}
