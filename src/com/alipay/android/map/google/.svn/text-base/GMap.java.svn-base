package com.alipay.android.map.google;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alipay.android.map.IMap;

public class GMap implements IMap {
    private Context mContext;//Activity

    public GMap(Context context) {
        mContext = context;
    }

    @Override
    public void location(String geoPoint,String label) {
        Uri uri = Uri.parse("http://maps.google.com/maps?q="+geoPoint+"("+label+")&z=21&cbp=1");
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        mContext.startActivity(intent);
    }

    @Override
    public void route(String srcGeoPoint, String destGeoPoint, String mode) {
        Uri uri = Uri.parse("http://maps.google.com/maps?saddr="+srcGeoPoint+"&daddr="+destGeoPoint+"&f=d&source=s_d&dirflg="+mode);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        mContext.startActivity(intent);
    }

}
