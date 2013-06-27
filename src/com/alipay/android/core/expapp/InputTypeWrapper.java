package com.alipay.android.core.expapp;

import java.util.HashMap;

import android.text.InputType;

public class InputTypeWrapper {

    final static HashMap<String, Integer> shmforInputType = new HashMap<String, Integer>();
    static {
        shmforInputType.clear();
        shmforInputType.put("digit", InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        shmforInputType.put("char", InputType.TYPE_CLASS_TEXT);
        shmforInputType.put("password", InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }
    
    public static int inputTypeFromString(String key) {
        return shmforInputType.get(key);
    }
}
