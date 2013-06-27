package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class CancelSavedExpressCard extends BaseServiceBean {

	String mSignID;
	String mCardType;
	String mPayPassword;
	String mCardNo;
	String mDelFromCCR;
	String mDelFromCifWithdraw;
	
	public CancelSavedExpressCard() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_CANCELSAVEDEXPRESSCARD;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.SIGNID, mSignID);
			requestJson.put(Constant.CARDTYPE, mCardType);
			requestJson.put(Constant.PAYPASSWORD, mPayPassword);
			requestJson.put(Constant.CARDNO, mCardNo);
			requestJson.put(Constant.ACTION_DELCCR, mDelFromCCR);
			requestJson.put(Constant.ACTION_DELCIFWITHDRAW, mDelFromCifWithdraw);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (6 == taskParams.length) {
			mSignID = taskParams[0];
			mCardType = taskParams[1];
			mPayPassword = taskParams[2];
			mCardNo = taskParams[3];
			mDelFromCCR = taskParams[4];
			mDelFromCifWithdraw = taskParams[5];
		}
	}

}
