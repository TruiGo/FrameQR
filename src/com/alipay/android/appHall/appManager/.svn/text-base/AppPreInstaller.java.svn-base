package com.alipay.android.appHall.appManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;

public class AppPreInstaller {
    private final static String apps_preInstall = "apps_preInstall";

    private Context mContext;
    public AppPreInstaller(Context context) {
        mContext = context;
    }

    public void doPreInstall(HallData hallData) {
        InputStream isConfig = null;
        HashMap<String, AppItemInfo> appConfigInfos = null;
        String[] appArray = null;
        try {
            appArray = mContext.getAssets().list(apps_preInstall);
            isConfig = mContext.getAssets().open(apps_preInstall + "/" + Defines.CONFIG_NAME);
            appConfigInfos = Helper.getConfigInfo(hallData, isConfig);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                isConfig.close();
                isConfig = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
        if (appArray == null || appConfigInfos == null)
            return;

        AppItemInfo appConfigInfo = null;
        boolean hasChanged = false;
        for (String file : appArray) {
            if (null == file || !file.endsWith(".amr"))
                continue;
            String appId = appIdFromFileName(file);
            appConfigInfo = appConfigInfos.get(appId);

            AppManager appManager = hallData.getAppManager();
            if (!appManager.isAppNeed2Install(appConfigInfo))//比较assert/apps_preInstall/configs.json中的应用程序与data/data/.../config.json中的版本信息
                continue;

            InputStream isSrc = null;
            try {
                isSrc = mContext.getAssets().open(apps_preInstall + "/" + file);
                appManager.installFromInputStream(appConfigInfo, isSrc);
                hasChanged = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    isSrc.close();
                    isSrc = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //store apps info
        if (hasChanged) {
            try {
                hallData.storeApps();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String appIdFromFileName(String file) {
        int index = file.lastIndexOf(".");
        if (index != -1)
            file = file.substring(0, index);

        return file;
    }

}
