package com.alipay.android.nfd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.alipay.android.util.LogUtil;

/**
 * 支付宝近场发现服务类
 * @author daping.gp
 * @version $Id: NFDOperationService.java, v 0.1 2012-6-25 下午2:14:35 daping.gp Exp $
 */
public class NFDService extends Service {
    static String TAG = "NFDService";
    private NfdBinder mNfdBinder;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mNfdBinder = new NfdBinder(this);
        LogUtil.logOnlyDebuggable(TAG, "nfd service on create");
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.logOnlyDebuggable(TAG, "nfd service on Bind");
        return mNfdBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.logOnlyDebuggable(TAG, "nfd service on Destroy");
    }

}
