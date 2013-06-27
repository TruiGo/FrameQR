/**
 * 
 */
package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.app.Application;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.android.appHall.component.WebBox.Client;
import com.alipay.android.appHall.component.WebBox.Scheme;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.core.expapp.Page;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class UIWebBox extends WebView implements UIInterface{
    private String mUrl;
    private Page mPage;
    
    private WebViewClient mClient;
    private Scheme mScheme;

    public UIWebBox(Page page) {
        super(page.getRawContext());
        mPage = page;
        mClient = new Client(this);
        mScheme = new Scheme(this);
    }
    
    public void init(ViewGroup parent,String url) {
        mUrl = url;
        
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        setWebViewClient(mClient);
        setWebChromeClient(new WebChromeClient(){
        	@Override
            public void onReceivedTitle(WebView view, String title) {
                ((AlipayApplication)mPage.getEngine().getContext().getApplicationContext()).closeProgress();//标题出现，可以隐藏进度条了
            }
        });
        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        setBackgroundColor(mPage.getEngine().getContext().getResources().getColor(R.color.AppBackgroundColor));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        this.setLayoutParams(layoutParams);
        this.requestFocusFromTouch();
        
        try{
            if(mUrl!=null&&!mScheme.processScheme(this,mUrl)){
                LogUtil.logOnlyDebuggable("WebBox", "loadWebUrl: "+mUrl);
                loadUrl(mUrl);
            }
        }catch (Exception e) {
            // TODO: handle exception
        }
    }
    

    public Page getPage() {
        return mPage;
    }

    public Scheme getScheme() {
        return mScheme;
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        if(value==null||value.toString().length()<=0)return;
        LogUtil.logOnlyDebuggable("WebBox", "setValue: "+value.toString());
        loadUrl(value.toString());
//        loadData(value.toString(), "text/html", "utf-8");
    }
    
    /**
     * 加载url
     */
    public void loadWebUrl(String url){
        if(url==null||url.length()<=0)return;
        LogUtil.logOnlyDebuggable("WebBox", "loadWebUrl: "+url);
        loadUrl(url);
    }
    
    /**
     * 加载HTML内容
     */
    public void loadWebData(String data){
        if(data==null||data.length()<=0)return;
        LogUtil.logOnlyDebuggable("WebBox", "loadWebData: "+data);
        loadData(data, "text/html", "utf-8");
    }

    @Override
    public Object getValue() {
        return mUrl;
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


    // ----------------------------设置宽度
    private boolean isChange = true;
    private int     width    = ViewGroup.LayoutParams.FILL_PARENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        params.width = this.width;
        setLayoutParams(params);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (isChange) {
            changedWidth();
        }
        isChange = false;
        // 最后测试动态更改宽度
        requestLayout();
    }

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
