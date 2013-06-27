package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class GetAccountInfo extends BaseServiceBean {
	public static String BIZ_TAG = GetAccountInfo.class.getName();
	private String mToPayAccount;

	public GetAccountInfo() {
		// TODO Auto-generated constructor stub
		operationType =ServiceBeanConstants.OPERATION_TYPE_QUERYACCOUNT;
	}
	@Override
	protected String buildRequestAsString() {
		// TODO Auto-generated method stub
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(Constant.RPF_TO_PAY_ACCOUNT, mToPayAccount);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (null != requestJson) ? requestJson.toString() : null;
	}

	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (1 == taskParams.length) {
			mToPayAccount = taskParams[0];
		}
	}
}
