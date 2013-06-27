package com.alipay.android.map.baidu;

import android.content.Context;
import android.content.Intent;

import com.alipay.android.map.IMap;

public class BMap implements IMap {
	
    private Context mContext;//Activity
    

    public BMap(Context context) {
        mContext = context;
    }
    

    @Override
    public void location(String geoPoint,String label) {
        Intent intent = new Intent(mContext, BMapActivity.class);
        intent.putExtra("geo", geoPoint);
        intent.putExtra("label", label);
        
        mContext.startActivity(intent);
    }

    @Override
    public void route(String srcGeoPoint, String destGeoPoint, String mode) {
        Intent intent = new Intent(mContext, BMapActivity.class);
        intent.putExtra("srcGeo", srcGeoPoint);
        intent.putExtra("destGeo", destGeoPoint);
        intent.putExtra("mode", mode);
        
        mContext.startActivity(intent);
    }

}
