package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class CreateP2PhoneOrder extends BaseServiceBean{
	String receiverMobile;
	String receiverName;
	String transferAmount;
	String bankShortName;
	String transferSpeedName;
	String reason;
	String toPayMemo;
	
	public CreateP2PhoneOrder(){
		operationType = ServiceBeanConstants.OPERATION_TYPE_TRANSFERTOMOBILECREATE;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.RECEIVERMOBILE, receiverMobile);
			requestJson.put(Constant.RECEIVERNAME, receiverName);
			requestJson.put(Constant.TRANSFERAMOUNT, transferAmount);
			requestJson.put(Constant.BANKSHORTNAME, bankShortName);
			requestJson.put(Constant.TRANSFERSPEEDNAME, transferSpeedName);
			requestJson.put(Constant.RPF_MEMO, reason);
			requestJson.put(Constant.RQF_TO_PAY_MEMO, toPayMemo);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		receiverMobile = taskParams[0];
		receiverName = taskParams[1];
		transferAmount = taskParams[2];
		bankShortName = taskParams[3];
		transferSpeedName = taskParams[4];
		reason = taskParams[5];
		toPayMemo = taskParams[6];
	}
}
