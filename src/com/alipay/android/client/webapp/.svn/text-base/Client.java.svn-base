/**
 * 
 */
package com.alipay.android.client.webapp;

import java.net.URLDecoder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.android.client.Login;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.util.LogUtil;

/**
 * @author sanping.li@alipay.com
 *
 */
public class Client extends WebViewClient {
    private static final String ALIPAY_SCHEME = "alipay";
    private static final String ALIPAY_PAY_HOST = "pay";
    private static final String ALIPAY_LOGIN_HOST = "login";
    private static final String ALIPAY_AUTHO_HOST = "auth";

    private webActivity context;

    public Client(webActivity context) {
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 部分浏览器对http response中的Location:alipay://pay跳转，会自动在前面增加http://www.okooo.com:80/alipay://pay，需要预处理此类现象。
        url = preprocessUrl(url);
        Uri u = Uri.parse(URLDecoder.decode(url));
        if (u != null && u.getScheme().equals(ALIPAY_SCHEME)) {
            if (u.getHost().equals(ALIPAY_PAY_HOST)) {
                LogUtil.logAnyTime(webActivity.TAG, "start alipay msp");
                doPay(u);
            } else if (u.getHost().equals(ALIPAY_LOGIN_HOST)) {
                LogUtil.logAnyTime(webActivity.TAG, "start login");
                doLogin(u);
            } else if (u.getHost().equals(ALIPAY_AUTHO_HOST)) {
                LogUtil.logAnyTime(webActivity.TAG, "start autho");
                doAutho(u);
            }
            return true;
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    private boolean startLoad;

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //for 淘宝
        context.HandleTradePay(url);
        context.HandleSessionInvalid(url);

        if (url.equals(context.getSuccesUrl())) {
            startLoad = true;
        }

        context.ShowProgressBar(true);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (startLoad) {
            startLoad = false;
            context.setSuccesUrl(url);
            context.closeProgress();
        }

        context.ShowProgressBar(false);
    }

    /**
     * 授权
     */
    private void doAutho(Uri u) {
        String callback_url = u.getQueryParameter(Constant.RES_PARAM_CALLBACKURL);
        String fail_url = u.getQueryParameter(Constant.RES_PARAM_FAILURL);
        String partnerId = u.getQueryParameter(Constant.RES_PARAM_PARTNER);
        Intent intent = new Intent(context, AuthoActivity.class);
        intent.putExtra(Constant.RQF_PARTNER_ID, partnerId);
        intent.putExtra(Constant.RES_PARAM_CALLBACKURL, callback_url);
        intent.putExtra(Constant.RES_PARAM_FAILURL, fail_url);
        context.startActivityForResult(intent, webActivity.RES_CODE_AUTHO);
    }

    /**
     * 登录
     */
    private void doLogin(Uri u) {
        String callback_url = u.getQueryParameter(Constant.RES_PARAM_CALLBACKURL);
        Intent intent = new Intent(context, Login.class);
        intent.putExtra(Constant.OP_RETURNURL, webActivity.WEBAPP);
        intent.putExtra(Constant.RES_PARAM_CALLBACKURL, callback_url);//call back url
        context.startActivityForResult(intent, Constant.REQUEST_LOGIN_BACK);
    }

    /**
     * 支付
     */
    private void doPay(Uri u) {
        String orderToken = u.getQueryParameter(Constant.RES_PARAM_TOKEN);
        String callBackUrl = u.getQueryParameter(Constant.RES_PARAM_CALLBACKURL);
        String failUrl = u.getQueryParameter(Constant.RES_PARAM_FAILURL);
        context.setCallBackUrl(callBackUrl);
        context.setFailUrl(failUrl);

        PayHelper payHelper = new PayHelper(context);
        payHelper.dopay(orderToken.replace('"', ' ').trim());
    }

    private String preprocessUrl(String url) {
        if (url != null) {
            int index = url.indexOf(ALIPAY_SCHEME + "://");
            if (index != -1) {
                url = url.substring(index);
            }
        }

        return url;
    }
}
