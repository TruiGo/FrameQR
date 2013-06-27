package com.alipay.android.map;

import com.alipay.android.map.baidu.BMap;
import com.alipay.android.map.google.GMap;
import com.alipay.android.util.PackageUtil;

import android.content.Context;

public class MapManager {
    private Context mContext;

    public MapManager(Context context) {
        mContext = context;
    }

    public void init(){
        
    }
    
    private boolean hasGMap(){
        return PackageUtil.hasInstalledPackage(mContext, "com.google.android.apps.maps");
    }
    
    public IMap getMap(){
        if(hasGMap()){
            return new GMap(mContext);
        }else{
            return new BMap(mContext);
        }
    }
    
}
