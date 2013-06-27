package com.alipay.android.servicebeans;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.util.LogUtil;

public class SyncVoucher extends BaseServiceBean {
	public static final String BIZTYPE = SyncVoucher.class.getName();
	String fileContent;

	public SyncVoucher(Context context) {
		operationType = ServiceBeanConstants.OPERATION_TYPE_SYNCVOUCHER;
		setContext(context);
	}

	@Override
	protected String buildRequestAsString() {
		return prepareRequest().toString();
	}

	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		fileContent = taskParams[0];
	}

	@Override
	public String doX() {
		String url = Constant.getAlipayServer(mContext);
		url = url + "/mobileFile.htm";
		APHttpClient aPHttpClient = new APHttpClient(url, mContext);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("requestData", buildRequestAsString()));
		pairs.add(new BasicNameValuePair(ServiceBeanConstants.FILECONTENT, fileContent));

		LogUtil.logOnlyDebuggable("HttpClient", "Request" + buildRequestAsString() + ";fileContent:" + fileContent);
		String response = aPHttpClient.sendSynchronousRequestAsHttpResponse(pairs);
		extractBasicResponse(response);
		return response;
	}
}