package com.alipay.android.core;

import java.io.File;

import android.content.Context;
import android.util.DisplayMetrics;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayApplication;

public class Style {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_METRO = TYPE_NORMAL + 1;

    private static final String RES_DIR_PATH = "res/";

    private int mType;
    private Context mContext;
    private String mAppsPath;

    private String mResDir;
    private String mSizeDir;

    public Style(Context context, int type) {
        mContext = context;
        mType = type;
        init();
    }

    private void init() {
        mAppsPath = (mContext.getFilesDir().getAbsolutePath() + Defines.Apps);
        switch (mType) {
            case TYPE_NORMAL:
                mResDir = "";
                break;
            case TYPE_METRO:
                mResDir = "metro/";
                break;
            default:
                break;
        }
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        switch (application.getConfigData().getDensity()) {
            case DisplayMetrics.DENSITY_HIGH:
                mSizeDir = "high/";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                mSizeDir = "medium/";
                break;
            case DisplayMetrics.DENSITY_LOW:
                mSizeDir = "low/";
                break;
            case 320://DisplayMetrics.DENSITY_XHIGH:
                mSizeDir = "xhigh/";
                break;
            default:
                break;
        }
    }

    public String getIcon(String Id, String icon) {
        if (icon != null && icon.startsWith("http"))
            return icon;
        File f = new File(mAppsPath + Id + "/" + RES_DIR_PATH + mResDir + mSizeDir + icon);
        if (f.exists())//size
            return mAppsPath + Id + "/" + RES_DIR_PATH + mResDir + mSizeDir + icon;
        f = new File(mAppsPath + Id + "/" + RES_DIR_PATH + mResDir + icon);
        if (f.exists())//style
            return mAppsPath + Id + "/" + RES_DIR_PATH + mResDir + icon;
        return mAppsPath + Id + "/" + RES_DIR_PATH + icon;
    }

}
