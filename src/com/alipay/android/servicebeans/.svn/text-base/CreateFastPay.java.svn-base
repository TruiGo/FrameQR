package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class CreateFastPay extends BaseServiceBean {

	
	public static String BIZ_TAG = CreateFastPay.class.getName();
	//定义请求需要的变量
	String dynamicId = "";
	String payMoney = "";

	public CreateFastPay() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_CREATEFASTPAY;
	}
	
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(Constant.QUICKPAY_DYNAMICID, dynamicId);
			requestJson.put(Constant.QUICKPAY_PAYMONEY, payMoney);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		dynamicId = taskParams[0];
		payMoney = taskParams[1];
		super.initParams(taskParams);
	}

}
