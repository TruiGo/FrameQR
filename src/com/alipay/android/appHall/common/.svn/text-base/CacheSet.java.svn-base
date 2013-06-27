package com.alipay.android.appHall.common;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheSet {
    static final String TAG = "AlixSet";
    public static final String FILE_NAME = "AppHall.cache";

    private Context mContext;

    private static CacheSet sRef = null;

    public static synchronized CacheSet getInstance(Context context) {
        if (sRef == null)
            sRef = new CacheSet();

        sRef.mContext = context;
        return sRef;
    }

    private CacheSet() {
    }

    public String getString(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FILE_NAME, 0);
        return null == sharedPref ? "" : sharedPref.getString(key, "");
    }

    public void putString(String key, String value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FILE_NAME, 0);
        sharedPref.edit().putString(key, value).commit();
    }

    public void remove(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(FILE_NAME, 0);
        sharedPref.edit().remove(key).commit();
    }
}