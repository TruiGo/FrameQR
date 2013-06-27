package com.alipay.android.appHall.component.incrementitem;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alipay.android.core.expapp.Page;

public abstract class AbstractIncreamUtil {
    protected Page mPage;
    protected LinearLayout                    itemsLayout        = null;
    protected LinearLayout                    controlLayout      = null;
    protected ArrayList<HashMap<Object, Object>>              inputContentArrayList;     //每一项的数据存放位置
    protected int                             mAddCount          = 0;    //数量
    protected HashMap<String, RelativeLayout> cacheLayoutHashMap = null; //填写完之后的数据保存

    protected abstract void loadIncreamItemsData();

    protected abstract void loadIncreamControlData();

    protected abstract void removeIncreamItem(String tag);
    
    public abstract Object getValue(); 
    public abstract boolean validateCheck();
    public abstract boolean inputed();
}
