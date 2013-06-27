package com.alipay.android.client.advert;

import android.app.Activity;
import android.net.Uri;
import android.webkit.WebView;

import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.core.MsgAction;

public class AdvertScheme {
    /**
     * alipay://platformapi/startapp?appId=10000001
     * 兼容id=xxxxx
     */
    private static final String ALIPAY_SCHEME = "alipay";
    private static final String ALIPAY_HOST = "platformapi";
    private static final String ALIPAY_ACTION_GOBUNDLE = "startapp";
    private static final String ALIPAY_ACTION_GOHOME = "gohome";

    public AdvertScheme(HallData hallData) {
    }

    public boolean processScheme(WebView view, String url) throws Exception {
        // 部分浏览器对http
        // response中的Location:alipay://pay跳转，会自动在前面增加http://www.okooo.com:80/alipay://pay，需要预处理此类现象。
        url = preprocessUrl(url);
        Activity activity = (Activity) view.getContext();
        AlipayApplication application = (AlipayApplication) activity.getApplicationContext();
        Uri uri = Uri.parse(url.toLowerCase());
        if (uri.getScheme().equals(ALIPAY_SCHEME) && uri.getHost().equals(ALIPAY_HOST)) {
            String action = uri.getPath();
            if (action.endsWith(ALIPAY_ACTION_GOBUNDLE)) {
                String param = uri.getQuery();
                String id = uri.getQueryParameter("id");
                if (id == null) {
                    id = uri.getQueryParameter("appid");
                }
                application.getMBus().sendMsg("", id, MsgAction.ACT_LAUNCH, param);
                activity.finish();
            } else if (action.endsWith(ALIPAY_ACTION_GOHOME)) {
                activity.finish();
            }
            return true;
        }
        return false;
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
