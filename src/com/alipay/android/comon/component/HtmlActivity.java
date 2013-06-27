package com.alipay.android.comon.component;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.Utilz;
import com.eg.android.AlipayGphone.R;

public class HtmlActivity extends RootActivity {
	public static String URL = "url";
	public static String TITLE = "title";
	private String mUrl;
	private TextView mTitleName;
	private ProgressBar mProgressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlactivity);
        
		mTitleName = (TextView) findViewById(R.id.title_text);
        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        
        WebView view = (WebView)findViewById(R.id.htmlView);
        WebSettings settings = view.getSettings();
		settings.setSupportZoom(false);
        
		String title = getIntent().getStringExtra(TITLE);
		if (!Utilz.isEmpty(title)) {
			setTitles(title);
		} else {
	        view.setWebChromeClient(new WebChromeClient() {
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
	        });
		}

        mUrl = getIntent().getStringExtra(URL);
        view.loadUrl(mUrl);
    }
    
	
	/**
	 * @param string
	 */
	private void setTitles(String string) {
		mTitleName.setText(string);
	}
}
