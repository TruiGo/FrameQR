/**
 * 
 */
package com.alipay.android.appHall.appManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author sanping.li
 *
 */
public class AppHandler extends Handler {
    public static final int ACTION_INSTALL = 0;
    public static final int ACTION_UPGRADE = ACTION_INSTALL + 1;
    public static final int ACTION_DELETE = ACTION_UPGRADE + 1;
    public static final int ACTION_RESUME = ACTION_DELETE + 1;

    /**
     * 业务列表数据
     */
    private HallData mHallData;

    public AppHandler(HallData hallData, Looper looper) {
        super(looper);
        mHallData = hallData;
    }

    public void doAction(String funcId, int action) {
        Message message = obtainMessage(action, funcId);
        sendMessage(message);
    }

    @Override
    public void handleMessage(Message msg) {
        String id = (String) msg.obj;
        int action = msg.what;
        AppItemInfo itemInfo = mHallData.getAppConfig(id);
        if (itemInfo == null)
            return;
        mHallData.refresh();
        AppManager appManager = mHallData.getAppManager();
        switch (action) {
            case ACTION_INSTALL:
                if (itemInfo.getState() != AppItemInfo.STATE_PRE_INSTALL)
                    break;
                itemInfo.setState(AppItemInfo.STATE_INSTALLING);
                appManager.install(itemInfo);
                break;
            case ACTION_UPGRADE:
                if (itemInfo.getState() != AppItemInfo.STATE_PRE_UPGRADE)
                    break;
                itemInfo.setState(AppItemInfo.STATE_UPGRADEING);
                appManager.upgrade(itemInfo);
                break;
            case ACTION_DELETE:
                if (itemInfo.getState() != AppItemInfo.STATE_PRE_DELETE)
                    break;
                if (appManager.uninstall(itemInfo)) {
                    itemInfo.setState(AppItemInfo.STATE_DELETEED);
                    mHallData.removeApp(itemInfo);
                    mHallData.removeAppFromCache(id);
                    mHallData.refresh();
                }
                break;
            case ACTION_RESUME:
                if (itemInfo.getState() != AppItemInfo.STATE_INSTALL_PAUSE
                    && itemInfo.getState() != AppItemInfo.STATE_UPGRADE_PAUSE)
                    break;
                if (itemInfo.getState() == AppItemInfo.STATE_INSTALL_PAUSE) {
                    itemInfo.setState(AppItemInfo.STATE_INSTALLING);
                } else if (itemInfo.getState() == AppItemInfo.STATE_UPGRADE_PAUSE) {
                    itemInfo.setState(AppItemInfo.STATE_UPGRADEING);
                }
                appManager.resume(itemInfo);
                break;
            default:
                break;
        }
    }

}
