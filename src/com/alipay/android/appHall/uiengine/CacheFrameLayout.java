package com.alipay.android.appHall.uiengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CacheFrameLayout extends FrameLayout {
    private Bitmap mCacheBitmap;

    public CacheFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CacheFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CacheFrameLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        Bitmap bitmap = mCacheBitmap;
        if(bitmap!=null)bitmap.recycle();

        try {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
            bitmap.setHasAlpha(false);
            mCacheBitmap = bitmap;
        } catch (OutOfMemoryError e) {
            // If there is not enough memory to create the bitmap cache, just
            // ignore the issue as bitmap caches are not required to draw the
            // view hierarchy
            mCacheBitmap = null;
            return;
        }
    }


    public Bitmap getCacheBitmap() {
        Canvas canvas = new Canvas(mCacheBitmap);
        draw(canvas);
        return mCacheBitmap;
    }
}
