package com.alipay.android.ui.transfer;

import android.view.LayoutInflater;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alipay.android.ui.framework.BaseViewController;
import com.eg.android.AlipayGphone.R;

public class TransferServiceViewController extends BaseViewController {
	private WebView mTransferServiceWebView;
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.transfer_service, null);
		addView(mView, null);
		mTransferServiceWebView = (WebView) findViewById(R.id.transferServiceWebView);
		WebSettings settings =mTransferServiceWebView.getSettings();
		settings.setSupportZoom(false);
		mTransferServiceWebView.loadUrl("file:///android_asset/transferService.html");
	}
}
