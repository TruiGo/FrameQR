package com.alipay.android.nfd;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.alipay.android.util.LogUtil;

/**
 * wLan注册类
 * @author daping.gp
 * @version $Id: LANWifiOberverImpl.java, v 0.1 2012-6-20 下午3:35:13 daping.gp Exp $
 */
public class WlanOberverPeerImpl implements ClientObserverPeer {
    private static final String TAG = "LANWifiOberverPeerImpl";
    Context                     mContext;
    UdpSendIpMsg                msg;

    public WlanOberverPeerImpl(Context context) {
        mContext = context;
    }

    @Override
    public boolean regist(String clientInfo,int type,String bizType) {
        if( (type & RegistType.LANWifi.getCode())==0){
            return false;
        }
        //如果不是wifi 返回为false
        if(!NFDUtils.getInstance(mContext).isUseWifi()){
            return false;
        }
        try {
            unRegist();
            msg = new UdpSendIpMsg(clientInfo,mContext);
            msg.start();
        } catch (Exception e) {
            LogUtil.logOnlyDebuggable(TAG, e.toString());
            return false;
        }
        return true;
    }

    @Override
    public boolean unRegist() {
        try {
            if (msg != null) {
                msg.setThreadExit();
                msg = null;
            }
        } catch (Exception e) {
            LogUtil.logOnlyDebuggable(TAG, e.toString());
            return false;
        }
        return true;
    }
}
