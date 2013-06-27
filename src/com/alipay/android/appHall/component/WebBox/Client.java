/**
 * 
 */
package com.alipay.android.appHall.component.WebBox;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.android.appHall.component.UIWebBox;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class Client extends WebViewClient {
    private UIWebBox mWebBox;
    private AlipayApplication mApplication;
    public Client(UIWebBox webBox) {
        mWebBox = webBox;
        mApplication = (AlipayApplication) mWebBox.getPage().getEngine().getContext().getApplicationContext();
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        mApplication.closeProgress();
        try {
            if(mWebBox.getScheme().processScheme(view,url)){
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

//        mApplication.showProgress();
        return super.shouldOverrideUrlLoading(view, url);
        
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mApplication.closeProgress();
        super.onPageStarted(view, url, favicon);
        mApplication.showProgress();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
        mApplication.closeProgress();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        // TODO Auto-generated method stub
//        super.onReceivedError(view, errorCode, description, failingUrl);
        ExpAppRuntime mEngine = mWebBox.getPage().getEngine();
        mApplication.closeProgress();
        String content = mEngine.getContext().getString(R.string.CheckNetwork);
        mEngine.showAlert(2, content, "$exit()", null, null);
        view.loadData("", "text/html", "utf-8");
    }


    
}
