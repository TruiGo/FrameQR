package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class CreateP2POrder extends BaseServiceBean{
	String toPayAmount;
	String toPayReason;
	String toPayAccount;
	String smsMobileNo;
	String isAddContact;
	String toPayMemo;
	
	public CreateP2POrder() {
		operationType = Constant.OP_CREATE_P2P;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.RQF_TO_PAY_AMOUNT, toPayAmount);
			requestJson.put(Constant.RQF_TO_PAY_REASON, toPayReason);
			requestJson.put(Constant.RQF_TO_PAY_ACCOUNT, toPayAccount);
			requestJson.put(Constant.RQF_SMS_PHONENO, smsMobileNo);
			requestJson.put(Constant.RQF_ISADDTOCONTACTS, isAddContact);
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
		toPayAmount = taskParams[0];
		toPayReason = taskParams[1];
		toPayAccount = taskParams[2];
		smsMobileNo = taskParams[3];
		isAddContact = taskParams[4];
		toPayMemo = taskParams[5];
	}
}
