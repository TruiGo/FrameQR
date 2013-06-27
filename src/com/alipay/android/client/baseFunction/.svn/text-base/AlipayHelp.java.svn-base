/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client.baseFunction;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Utilz;
import com.eg.android.AlipayGphone.R;

/**
 * The activity to show Help View.
 * </p>
 * @author zhoucheng
 */
public class AlipayHelp extends RootActivity{
	public static String INDEX = "index";
	private String mAnchor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_help_320_480);
        WebView view = (WebView)findViewById(R.id.help_view);
        WebSettings settings = view.getSettings();
		settings.setSupportZoom(false);
		
		mAnchor = getIntent().getStringExtra(INDEX);
        if (!Utilz.isEmpty(mAnchor)) {
        	WebSettings webSettings = view.getSettings();
        	webSettings.setJavaScriptEnabled(true);
        	view.setWebViewClient(new WebViewClient()
        	{
        	    public void onPageFinished(WebView view, String url)
        	    {
        	        if (null != mAnchor)
        	        {
        	            view.loadUrl("javascript:window.location.hash='" + mAnchor + "'");
        	            mAnchor = null;
        	        }
        	    }
        	});
        }
        view.loadUrl(Constant.HELP_URL);
        
        TextView mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.MenuHelp);
    }
}
