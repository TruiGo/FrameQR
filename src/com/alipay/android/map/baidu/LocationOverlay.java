package com.alipay.android.map.baidu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;

public class LocationOverlay extends Overlay {
    private GeoPoint mGeoPoint;
    private Drawable mDrawable;
    private String mLabel;
    private Paint mPaint;
 
    public LocationOverlay(GeoPoint geoPoint,Drawable drawable,String label) {
        mGeoPoint = geoPoint;
        mDrawable = drawable;
        mLabel = label;
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(20);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        Point point = mapView.getProjection().toPixels(mGeoPoint, null);
        canvas.save();
        canvas.translate(point.x-mDrawable.getIntrinsicWidth()/2, point.y);
        mDrawable.setBounds(0,0,mDrawable.getIntrinsicWidth(),mDrawable.getIntrinsicHeight());
        mDrawable.draw(canvas);
        canvas.restore();
        canvas.drawText(mLabel, point.x, point.y, mPaint);
    }
}
