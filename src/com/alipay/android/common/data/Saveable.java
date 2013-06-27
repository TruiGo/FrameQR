package com.alipay.android.common.data;

import android.content.SharedPreferences;

public interface Saveable {
    public void save(SharedPreferences preferences,String key);
    public void restore(SharedPreferences preferences,String key);
}
