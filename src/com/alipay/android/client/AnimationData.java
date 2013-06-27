package com.alipay.android.client;

import android.graphics.Bitmap;

public class AnimationData {
    private Bitmap mBitmap;//图片
    private int mInitY;
    private int mInitH;

    public AnimationData(Bitmap bitmap, int initY, int initH) {
        mBitmap = bitmap;
        mInitY = initY;
        mInitH = initH;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getInitY() {
        return mInitY;
    }

    public int getInitH() {
        return mInitH;
    }

}
