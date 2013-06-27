package com.alipay.android.appHall.component;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIImage extends ImageView implements UIInterface {

    private Page mPage;
    private int heightPixels, widthPixels;

    public UIImage(Page page) {
        super(page.getRawContext());
        mPage = page;
        DisplayMetrics dm = BaseHelper.getDisplayMetrics((Activity) mPage.getRawContext());
        this.heightPixels = dm.heightPixels;
        this.widthPixels = dm.widthPixels;
        setAdjustViewBounds(true);
    }

    public void init(ViewGroup parent) {
        ImageView image = (ImageView) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.image, parent, false);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) image.getLayoutParams();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object id = getTag() == null ? "" : getTag();
                mPage.interpreter("Image::" + id.toString(), expression);
            }
        });
        this.setLayoutParams(Helper.copyLinearLayoutParams(params));
    }

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    //网络上和资源包两种情况
    public void setData(String uri) {
        if (uri == null || uri.length() <= 0)
            return;
        BitmapDownloadListener listener = new BitmapDownloadListener() {
            @Override
            public void onComplete(final Bitmap bm) {
                ((Activity) mPage.getRawContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        UIImage.this.setImageBitmap(bm);
                    }
                });
            }
        };
        String scheme = Uri.parse(uri).getScheme();
        if (scheme != null && scheme.indexOf("http") != -1) {
            StringBuffer sb = new StringBuffer(uri);
            sb.append("?screenWidthPixels=" + String.valueOf(widthPixels) + "&screenHeightPixels="
                      + String.valueOf(heightPixels));
            uri = sb.toString();
            Helper.bitmapFromUriString(mPage.getRawContext(), uri, listener,R.drawable.app_default);
        } else {
            String u = (mPage.getRawContext()).getFilesDir().getAbsolutePath() + Defines.Apps
                       + mPage.getEngine().getPkgId() + "/res/" + uri;
            File file = mPage.getEngine().getFile(u);

            Bitmap bitmap = Helper.bitmapFromUriString(mPage.getRawContext(), file, listener,R.drawable.app_default);
            if(bitmap!=null)
                setImageBitmap(bitmap);
        }
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        // TODO Auto-generated method stub
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return null;
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

    public void set_Height(int height) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        params.height = height;
    }

    public void set_Width(int width) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        params.width = width;
    }

    // ----------------------------设置宽度
    //    private boolean isChange = true;
    //    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;
    //
    //    // getWidth方法应该和设置的宽度是否一样，待确认
    //    public void set_Width(int width) {
    //        isChange = true;
    //        this.width = width;
    //        changedWidth();
    //    }
    //
    //    private void changedWidth() {
    //        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
    //        params.width = this.width;
    //        setLayoutParams(params);
    //    }
    //
    //    @Override
    //    protected void onWindowVisibilityChanged(int visibility) {
    //        super.onWindowVisibilityChanged(visibility);
    //        if (isChange) {
    //            changedWidth();
    //        }
    //        isChange = false;
    //        // 最后测试动态更改宽度
    //        requestLayout();
    //    }

    @Override
    public String getSelectedIndex() {
        return null;
    }

    private boolean isSave;

    @Override
    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    @Override
    public boolean getIsSave() {
        return isSave;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.leftMargin = marginLeft;
        setLayoutParams(params);
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.rightMargin = marginRight;
        setLayoutParams(params);
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.topMargin = marginTop;
        setLayoutParams(params);
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = marginBottom;
        setLayoutParams(params);
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public int get_Width() {
        return getWidth();
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

    @Override
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (android.widget.LinearLayout.LayoutParams) getLayoutParams();
    }

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
