package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class SendPayeeInfo extends BaseServiceBean {
	public static String BIZ_TAG = SendPayeeInfo.class.getName();
	String dynamicId = "";
	
	public SendPayeeInfo() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_SENDPAYEEINFO;
	}


	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(Constant.QUICKPAY_DYNAMICID, dynamicId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		dynamicId = taskParams[0];
		super.initParams(taskParams);
	}
}
