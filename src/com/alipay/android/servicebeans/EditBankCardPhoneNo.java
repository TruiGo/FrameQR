package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;


public class EditBankCardPhoneNo extends BaseServiceBean {

	String mSignID;
	String mCardNo;
	String mCardType;
	String mBankShortName;
	String mUserPhone;
	
	public EditBankCardPhoneNo() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_MODIFYEXPRESSUSERPHONE;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.SIGNID, mSignID);
			requestJson.put(Constant.BANKSHORTNAME, mBankShortName);
			requestJson.put(Constant.CARDTYPE, mCardType);
			requestJson.put(Constant.CARDNO, mCardNo);
			requestJson.put(Constant.USERPHONE, mUserPhone);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (5 == taskParams.length) {
			mSignID = taskParams[0];
			mBankShortName = taskParams[1];
			mCardType = taskParams[2];
			mCardNo = taskParams[3];
			mUserPhone = taskParams[4];
		}
	}

}
