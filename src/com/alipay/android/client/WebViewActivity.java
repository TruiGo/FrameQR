package com.alipay.android.client;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.webapp.Client;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.eg.android.AlipayGphone.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author dong.liu
 * 内嵌在应用中打开普通网页
 */
public class WebViewActivity extends RootActivity {
	public static final String WEBAPP_URL = "url";
	public static final String WEBAPP_NAME = "name";
	
	private WebView webView;
	private TextView mTitleName;
	private Client client;
	private String url;
	private String title;
	private String callBackUrl;
	private String failUrl;
	private String succesUrl;//交易成功
	private ProgressDiv mProgress;
    private ProgressBar mProgressBar;
	private StorageStateInfo storageStateInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.webapp_view);
		webView = (WebView) findViewById(R.id.content);
		mTitleName = (TextView) findViewById(R.id.title_text);
        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				View toastView = LayoutInflater.from(
						getApplicationContext()).inflate(
						R.layout.alipay_toast_view, null);
				TextView toastTextView = (TextView) toastView
						.findViewById(R.id.toastText);
				toastTextView.setText(R.string.networkfailed);
				Toast toast = new Toast(getApplicationContext());
				toast.setView(toastView);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}

		});
		
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		//清空记录和历史
		webView.clearCache(true);
		webView.clearHistory();
		
		this.loadUrl(getIntent().getStringExtra(WEBAPP_URL));
		setTitles(getIntent().getStringExtra(WEBAPP_NAME));
        
	}
	
	/**
	 * @param string
	 */
	private void setTitles(String string) {
		if (null != mTitleName) {
			mTitleName.setText(string);
		}
	}
	
	private void loadUrl(String url){
	    try{
    		webView.loadUrl(url);
	    }catch (Exception e) {
        }
	}
	
	/**
	 * 按键处理
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				String nextActivity = getIntent().getStringExtra(Defines.NextActivity);
				if (null != nextActivity) {
					try {
						Class<?> clazz = Class.forName(nextActivity);
						Intent intent = new Intent(this, clazz);
						this.startActivity(intent);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				this.finish();
			}
			
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
    final class MyWebChromeClient extends WebChromeClient 
    {
		@Override
        public void onReceivedTitle(WebView view, String title){
        	setTitles(title);
        }
		
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress >= 0&&newProgress<100){
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }else
                mProgressBar.setVisibility(View.GONE);
        }
    }
}
