package com.alipay.android.client.advert;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.android.appHall.appManager.HallData;
import com.eg.android.AlipayGphone.R;

public class WebClient extends WebViewClient {
    private AdvertScheme mAdvertScheme;
    private Activity mActivity;

    public WebClient(HallData hallData, Activity activity) {
        mAdvertScheme = new AdvertScheme(hallData);
        mActivity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            if (mAdvertScheme.processScheme(view, url)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mActivity != null) {
            Intent intent = new Intent(mActivity, AdvertDetail.class);
            intent.putExtra("url", url);
            intent.putExtra("title", mActivity.getString(R.string.operation_center));
            mActivity.startActivity(intent);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
}
