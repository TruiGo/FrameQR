/**
 * 
 */
package com.alipay.android.net.http;

import android.content.Context;

import com.alipay.android.client.constant.Constant;

/**
 * @author sanping.li
 * 请求元素
 *
 */
public class Request {
	private String mUrl;
	private String mPostData;
	private boolean mRelAccount;
	public Request(String url) {
		mUrl = url;
	}
	
	public String getUrl(Context context) {
        return Constant.getURL(context,mUrl);
	}
	public String getPostData() {
		return mPostData;
	}
	public boolean getRelAccount() {
		return mRelAccount;
	}

	public void setPostData(String postData) {
		mPostData = postData;
	}

	public void setRelAccount(boolean relAccount) {
		mRelAccount = relAccount;
	}
	
}
