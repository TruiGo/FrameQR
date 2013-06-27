package com.alipay.android.client.advert;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;
import com.eg.android.AlipayGphone.R;

public class AdvertDetail extends RootActivity {
    private WebClient mWebClient;
    private String mUrl;
    private String mTitle;

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private TextView mTitleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webapp_view);

        AlipayApplication alipayApplication = (AlipayApplication) getApplicationContext();
        mWebClient = new WebClient(alipayApplication.getHallData(), null);
        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        loadVariables();
        mWebView.loadUrl(mUrl);
    }

    private void loadVariables() {
        mWebView = (WebView) findViewById(R.id.content);
        mTitleName = (TextView) findViewById(R.id.title_text);
        mTitleName.setText(mTitle);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(mWebClient);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 0&&newProgress<100){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }else
                    mProgressBar.setVisibility(View.GONE);
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        final String mimetype, long contentLength) {
                if (mimetype.equalsIgnoreCase("application/vnd.android.package-archive")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * 按键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN
            && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
