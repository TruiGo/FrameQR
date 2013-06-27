package com.alipay.android.appHall.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.expapp.Page;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class UIShowBox extends LinearLayout implements UIInterface, OnTouchListener,
                                           OnGestureListener, OnDoubleTapListener {

    static final String TAG = "UIShowBox";

    public static final int UISHOWBOX_H = 1;
    public static final int UISHOWBOX_V = 2;

    private int heightPixels, widthPixels;

    private int showBox_Type = UISHOWBOX_H;

    private GestureDetector mGestureDetector;
    private ViewFlipper flipper;

    private ImageView[] imageViews;

    private Page mPage;

    public UIShowBox(Page page) {
        super(page.getRawContext());
        mPage = page;
        DisplayMetrics dm = BaseHelper.getDisplayMetrics((Activity) mPage.getRawContext());
        this.heightPixels = dm.heightPixels;
        this.widthPixels = dm.widthPixels;

    }

    public void init(ViewGroup parent) {
        init(parent, showBox_Type);
        mGestureDetector = new GestureDetector(this);
        flipper.setFocusableInTouchMode(true);
        flipper.setLongClickable(true);
        flipper.setOnTouchListener(this);
    }

    private void init(ViewGroup parent, int orientation) {
        showBox_Type = orientation;

        flipper = (ViewFlipper) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.uishowbox, this, false);
        this.addView(flipper);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        setLayoutParams(params);

        if (showBox_Type == UISHOWBOX_H) {
            flipper.setInAnimation(mPage.getRawContext(), R.anim.push_left_in);
            flipper.setOutAnimation(mPage.getRawContext(), R.anim.push_left_out);
        } else {
            flipper.setInAnimation(mPage.getRawContext(), R.anim.push_top_in);
            flipper.setOutAnimation(mPage.getRawContext(), R.anim.push_top_out);
        }
    }

    private int imgCount = 0;

    private void getImage(ArrayList<Object> uris) {
        final BitmapDrawable[] drawables = new BitmapDrawable[uris.size()];

        for (int i = 0; i < uris.size(); i++) {
            final int j = i;
            BitmapDownloadListener listener = new BitmapDownloadListener() {
                @Override
                public void onComplete(Bitmap bm) {
                    drawables[j] = new BitmapDrawable(bm);
                    imgCount++;
                    if (imgCount == drawables.length) {
                        ((Activity) mPage.getRawContext()).runOnUiThread(new Runnable() {
                            public void run() {
                                getImage(drawables);
                                imgCount = 0;
                            }
                        });
                    }
                }
            };

            String uri = (String) uris.get(i);
            String scheme = Uri.parse(uri).getScheme();
            if (scheme != null && scheme.indexOf("http") != -1) {
                StringBuffer sb = new StringBuffer(uri);
                sb.append("?screenWidthPixels=" + String.valueOf(widthPixels)
                          + "&screenHeightPixels=" + String.valueOf(heightPixels));
                uri = sb.toString();
                Helper.bitmapFromUriString(mPage.getRawContext(), uri, listener,R.drawable.app_default);
            } else {
                File file = mPage.getEngine().getFile(uri);

                Bitmap bitmap =Helper.bitmapFromUriString(mPage.getRawContext(), file, listener,R.drawable.app_default);
                if(bitmap!=null)
                    listener.onComplete(bitmap);
            }
        }
    }
    
    private void getImage(BitmapDrawable[] drawables) {
        imageViews = new ImageView[drawables.length];
        for (int i = 0; i < drawables.length; i++) {
            imageViews[i] = new ImageView(mPage.getRawContext());
            imageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(
                imgWidth, imgHeight));
            imageViews[i].setLayoutParams(params);
            imageViews[i].setImageDrawable(drawables[i]);
            flipper.addView(imageViews[i]);
        }
    }

    private int imgWidth;
    private int imgHeight;

    // ----------------------------设置宽度
    private boolean isChangeHeight = true;
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;

    public void set_Height(int height) {
        isChangeHeight = true;
        this.height = height;
        imgHeight = height;
        changedHeight();
    }

    private void changedHeight() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        params.height = this.height;
    }

    private boolean isChangeWidth = true;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;

    public void set_Width(int width) {
        isChangeWidth = true;
        this.width = width;
        imgWidth = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)getLayoutParams();
        params.width = this.width;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (isChangeWidth) {
            changedWidth();
            isChangeWidth = false;
        }
        if (isChangeHeight) {
            changedHeight();
            isChangeHeight = false;
        }
        // 最后测试动态更改宽度
        requestLayout();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        uris = (ArrayList<Object>) value;
        getImage(uris);
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return uris;
    }

    @Override
    public void setEnable(boolean enabled) {
        this.setEnabled(enabled);
    }

    @Override
    public boolean getEnable() {
        // TODO Auto-generated method stub
        return isEnabled();
    }

    public void setIsSave(boolean isSave) {
    }

    public boolean getIsSave() {
        return false;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {

        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public int get_Width() {
        return getWidth();
    }

    public int get_Height() {
        return getHeight();
    }

    @Override
    public void set_Id(int id) {
        setId(id);
    }

    @Override
    public int get_Id() {
        return getId();
    }

    @Override
    public void set_Text(String text) {
    }

    @Override
    public String get_Text() {
        return null;
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public String getSelectedIndex() {
        return null;
    }

    @Override
    public String get_Tag() {
        return null;
    }

    @Override
    public void set_Tag(String tag) {
    }

    //OnDoubleTapListener实现的接口
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onDoubleTap");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onDoubleTapEvent");
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onSingleTapConfirmed");
        Object id = getTag()==null?"":getTag();
        mPage.interpreter("ShowBox::"+id.toString(),(itemExps.get(currentIndex)).toString());
        return true;
    }

    //OnGestureListener实现的接口
    @Override
    public boolean onDown(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onDown");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        LogUtil.logAnyTime(TAG, "onFling");
        if (showBox_Type == UISHOWBOX_H) {
            if (e1.getX() > e2.getX()) {//move to left
                flipper.showNext();
                currentIndex += 1;
                if (currentIndex == uris.size()) {
                    currentIndex = 0;
                }
            } else if (e1.getX() < e2.getX()) {
                flipper.setInAnimation(mPage.getRawContext(), R.anim.push_right_in);
                flipper.setOutAnimation(mPage.getRawContext(), R.anim.push_right_out);
                flipper.showPrevious();
                flipper.setInAnimation(mPage.getRawContext(), R.anim.push_left_in);
                flipper.setOutAnimation(mPage.getRawContext(), R.anim.push_left_out);
                currentIndex -= 1;
                if (currentIndex == -1) {
                    currentIndex = uris.size() - 1;
                }
            } else {
                return false;
            }
        } else {
            if (e1.getY() < e2.getY()) {//move to bottom
                System.out.println("onFling" + "1");
                flipper.showNext();
            } else if (e1.getY() > e2.getY()) {
                flipper.setInAnimation(mPage.getRawContext(), R.anim.push_bottom_in);
                flipper.setOutAnimation(mPage.getRawContext(), R.anim.push_bottom_out);
                flipper.showPrevious();
                flipper.setInAnimation(mPage.getRawContext(), R.anim.push_top_in);
                flipper.setOutAnimation(mPage.getRawContext(), R.anim.push_top_out);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onLongPress");
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LogUtil.logAnyTime(TAG, "onScroll");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onShowPress");
    }

    //实现单击onSingleTapUp->onSingleTapConfirmed

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LogUtil.logAnyTime(TAG, "onSingleTapUp");
        return false;
    }

    //OnTouchListener实现的接口
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.logAnyTime(TAG, "onTouch");
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (LinearLayout.LayoutParams) findViewById(R.id.mViewFlipper).getLayoutParams();
    }

    //-------------------
    private ArrayList<Object> itemExps = null;
    private ArrayList<Object> uris = null;
    private int currentIndex = 0;

    @SuppressWarnings("unchecked")
    public void setItemExps(Object object) {
        if (object != null) {
            itemExps = (ArrayList<Object>) object;
        }
    }

    //-------------------

    //未开放接口
    //    private void setShowBoxType(int showBoxType){
    //        this.showBox_Type = showBoxType;
    //    }
    //    
    //    private float mDuration;
    //    
    //    private void setAnimDuration(float duration){
    //        this.mDuration = duration;
    //    }

    //tag for android 1.5
    HashMap<Integer, Object> mTags = new HashMap<Integer, Object>();

    @Override
    public Object get_tag(int type) {
        return mTags.get(type);
    }

    @Override
    public void set_Tag(int type, Object tag) {
        mTags.put(type, tag);
    }

}
