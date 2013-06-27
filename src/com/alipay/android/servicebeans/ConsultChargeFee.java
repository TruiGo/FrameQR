package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

public class ConsultChargeFee extends BaseServiceBean {
	public static String BIZ_TAG = ConsultChargeFee.class.getName();
	String transferSpeedName;
	String bankShortName;
	String transferAmount;
	
	public ConsultChargeFee() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_CONSULTCHARGEFEE; 
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		transferSpeedName = taskParams[0];
		bankShortName = taskParams[1];
		transferAmount = taskParams[2];
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(ServiceBeanConstants.TRANSFERSPEEDNAME, transferSpeedName);
			requestJson.put(ServiceBeanConstants.BANKSHORTNAME, bankShortName);
			requestJson.put(ServiceBeanConstants.TRANSFERAMOUNT, transferAmount);
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
}
