package com.alipay.android.appHall.uiengine;

import java.util.ArrayList;

public interface UIList {
    
    public static final String VALUE = "value";
    public static final String TEXT = "text";
    public static final String EXPINDEX = "expIndex";
    public static final String STATE = "state";

    public static final String ENABLE = "enable";
    public static final String DISABLE = "disable";
    
    void addData(ArrayList<Object> value);
    void setData(ArrayList<Object> value);
    ArrayList<?> getData();
    
    String getItemId();
    
    void setTotal(int total);
    
    int getCurrentPage();
    
    int getPageSize();
    
    void setDeletable(String deletable);
    
    void setDelExp(String exp);
    
    void clearData();
    
    void setItemExps(ArrayList<Object> itemExps);
    
    
}