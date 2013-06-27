package com.alipay.android.map;

import com.baidu.mapapi.GeoPoint;

public class GeoPointUtil {
    public static GeoPoint transToBGeoPoint(String geoPoint){
        if(geoPoint==null||geoPoint.length()<=0)
            return new GeoPoint((int)39.92, (int)116.46);
        String[] strs = geoPoint.split(",");
        if(strs.length<=1)
            return new GeoPoint((int)39.92, (int)116.46);
        double double1 = Double.valueOf(strs[0]);
        double double2 = Double.valueOf(strs[1]);
        return new GeoPoint((int)(double1*1E6), (int)(double2*1E6));
    }
}
