package com.alipay.android.ui.voucher;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.ui.bean.WapPageInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.eg.android.AlipayGphone.R;

/**
 * 券详情wap/商户详情wap页
 */
public class DetailWapViewController extends BaseViewController{
	private WebView mWapView;
	String url;
	private AlipayApplication mApplication;
	private TextView mTitleView;
	
	@Override
	protected void onCreate() {
		mApplication = (AlipayApplication) getRootController().getApplicationContext();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.detailinfo_wap, null,false);
		addView(mView, null);
		
		mTitleView = (TextView) mView.findViewById(R.id.title_text);
		mWapView = (WebView) mView.findViewById(R.id.detail_wap);
		
		/*if(params != null)
			url = (String) params;*/
		if(params != null){
			WapPageInfo wapPageInfo = (WapPageInfo) params;
			mTitleView.setText(wapPageInfo.getPageTitle());
			url = wapPageInfo.getPageUrl();
		}
		
		WebSettings settings = mWapView.getSettings();
		settings.setJavaScriptEnabled(true);
		
		mWapView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				mApplication.closeProgress();
			}
		});
		
		mWapView.setWebViewClient(new WebViewClient(){
			@Override
		    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		        mApplication.closeProgress();
		        super.onPageStarted(view, url, favicon);
		        mApplication.showProgress();
		    }

		    @Override
		    public void onPageFinished(WebView view, String url) {
		        super.onPageFinished(view, url);
		        mApplication.closeProgress();
		    }
		});
		mWapView.loadUrl(url);
	}
}
