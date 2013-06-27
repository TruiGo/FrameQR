package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class CheckPhoneBlack extends BaseServiceBean {
	public static String BIZ_TAG = CheckPhoneBlack.class.getName();
	String manufacturer  = "";
	String phoneModel   = "";
	String osVersion  = "";
	
	public CheckPhoneBlack() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_CHECKPHONEBLACK;
	}


	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(Constant.QUICKPAY_MANUFACTURER, manufacturer);
			requestJson.put(Constant.QUICKPAY_PHONEMODEL, phoneModel);
			requestJson.put(Constant.QUICKPAY_OSVERSION, osVersion);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		manufacturer = taskParams[0];
		phoneModel = taskParams[1];
		osVersion = taskParams[2];
		super.initParams(taskParams);
	}
}
