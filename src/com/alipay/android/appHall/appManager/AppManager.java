package com.alipay.android.appHall.appManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public interface AppManager {
    void install(AppItemInfo appItemInfo);
    boolean installFromInputStream(AppItemInfo appItemInfo, InputStream isSrc);
    void upgrade(AppItemInfo appItemInfo);
    boolean uninstall(AppItemInfo appItemInfo);
    String getAppList(HashMap<String,AppItemInfo> apps);
    ArrayList<AppItemInfo> getAppListInfo(HashMap<String,AppItemInfo> apps);
    boolean isAppExisted(String appId);
    public String getAppVersion(String appId);
    public String getAppIcon(String appId);
    public String getAppName(String appId);
    public int getAppPosition(String appId);
    void resume(AppItemInfo appItemInfo);
    boolean release(AppItemInfo appItemInfo);
    public boolean isAppNeed2Install(AppItemInfo appItemInfo);
}
