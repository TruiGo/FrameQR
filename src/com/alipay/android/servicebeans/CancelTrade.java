package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

public class CancelTrade extends BaseServiceBean{
	public static final String BIZTYPE = BaseServiceBean.class.getName();
	String tradeNo;
	
	public CancelTrade() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_CANCELTRADE;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(ServiceBeanConstants.TRADENO, tradeNo);
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		tradeNo = taskParams[0];
	}
}
