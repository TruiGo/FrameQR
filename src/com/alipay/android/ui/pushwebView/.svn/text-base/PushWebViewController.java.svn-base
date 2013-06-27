package com.alipay.android.ui.pushwebView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.SchemeHandler;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.core.MsgAction;
import com.alipay.android.ui.framework.BaseViewController;
import com.eg.android.AlipayGphone.R;

public class PushWebViewController extends BaseViewController {
	private static final String ALIPAY_SCHEME = "alipay";
	private static final String ALIPAY_HOST = "platformapi";
	private static final String ALIPAY_ACTION_FUNCTION = "function";
	//private static final String mPkgId = "11000004";
	private TextView mTitleTxt;
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private String mUrl;
	private String sUrl;
	private AlipayDataStore mAlipayDataStore;
	private String strPrefix ="http://fun.alipay.com";
	 AlipayApplication application;
	@Override
	protected void onCreate() {
		mView = (LinearLayout) LayoutInflater.from(this.getRootController())
				.inflate(R.layout.alipay_push_wapview, null, false);
		addView(mView, null);
		mAlipayDataStore = new AlipayDataStore(this.getRootController());
		loadAllVariables();
		application = (AlipayApplication) this.getRootController().getApplicationContext();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if(requestCode ==  Constant.REQUEST_WAPLOGIN && mUrl !=null && mWebView!=null){
			if(mUrl.indexOf("?")!=-1){
				mUrl +="&extern_token="+this.getRootController().getExtToken();
			}else{
				mUrl +="?extern_token="+this.getRootController().getExtToken();
			}
			mWebView.loadUrl(mUrl);
			mUrl = null;
		}
	}

	private void loadAllVariables() {
		mTitleTxt = (TextView) findViewById(R.id.title_text);
		mWebView = (WebView) findViewById(R.id.AlipayPushUrlWebView);
		mProgressBar = (ProgressBar)findViewById(R.id.ProgressBar);
		// TODO Auto-generated method stub
		if (params != null) {
			try {
				Bundle bundle = (Bundle) params;
				mWebView.getSettings().setDefaultTextEncodingName("utf-8");
				mWebView.getSettings().setJavaScriptEnabled(true);
				mWebView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						if(processScheme(view, url)){
							return true;
						}
						return super.shouldOverrideUrlLoading(view, url);
					}
				});

				mWebView.setWebChromeClient(new WebChromeClient() {
					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						if (newProgress >= 0 && newProgress < 100) {
							mProgressBar.setVisibility(View.VISIBLE);
							mProgressBar.setProgress(newProgress);
						} else
							mProgressBar.setVisibility(View.GONE);
					}
				});
				mTitleTxt.setText(bundle.getString(SchemeHandler.PARAM_TITLE));
				//String url = bundle.getString(SchemeHandler.PARAM_URL);
				/*if(url != null && (url.trim().indexOf("http://fun.alipay.net/")!=-1)){//.equalsIgnoreCase("ccrmobile/index.htm?source=app")){
				   sUrl = "http://wap.hzt24.alipay.net/clientTowapLogin.htm?goto="+Uri.encode(url)+"&ct=1&extern_token="+this.getRootController().getExtToken();
				}else{*/
				mUrl = bundle.getString(SchemeHandler.PARAM_URL);//+"&extern_token="+this.getRootController().getExtToken();
				if(!Constant.isDebug(this.getRootController())){
					strPrefix = "http://fun.alipay.com";
				}
				if(mUrl.startsWith(strPrefix)&&mUrl.indexOf("extern_token=")==-1){
					if(mUrl.indexOf("?")!=-1){
						mUrl +="&extern_token="+this.getRootController().getExtToken();
					}else{
						mUrl +="?extern_token="+this.getRootController().getExtToken();
					}
				}
				mWebView.loadUrl(mUrl);
				mUrl = null;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean processScheme(WebView view, String url) {
		url = preprocessUrl(url);
		Uri uri = Uri.parse(url);
		if (url != null && uri.getScheme() != null
				&& uri.getScheme().equals(ALIPAY_SCHEME)
				&& uri.getHost() != null && uri.getHost().equals(ALIPAY_HOST)) {
			String action = uri.getPath();
			if (action.endsWith(ALIPAY_ACTION_FUNCTION)) {
				/* String functionName = uri.getQueryParameter("functionName"); */
				String funName = uri.getQueryParameter("functionName");
				String isSucc = uri.getQueryParameter("isSuccess");
				String AppsId = uri.getQueryParameter("AppsId");
				mUrl  = uri.getQueryParameter("wapurl");
				if(funName.equalsIgnoreCase("reLogin")){
					Intent tIntent = new Intent(this.getRootController(), Login.class);
					getRootController().startActivityForResult(tIntent, Constant.REQUEST_WAPLOGIN);
				}
				else if (funName.equalsIgnoreCase("experienceMoney") && isSucc.equalsIgnoreCase("YES") && mAlipayDataStore != null) {
					mAlipayDataStore.putBoolean(this.getRootController().getUserId(), true);
				}
				else if(funName.equalsIgnoreCase("exitWapPage")){
					try {
						this.getRootController().finish();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(funName.equalsIgnoreCase("goto")&&AppsId != null&&application != null){
					application.getMBus().sendMsg("", AppsId, MsgAction.ACT_LAUNCH,"");
				}
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
			return url;
		} else {
			return "";
		}

	}
}
