package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class VerifyModifyExpressUserPhone extends BaseServiceBean {

	String mSignID;
	String mUserPhone;
	String mUpdateType;
	
	public VerifyModifyExpressUserPhone() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_VERIFYMODIFYEXPRESSUSERPHONE;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.SIGNID, mSignID);
			requestJson.put(Constant.USERPHONE, mUserPhone);
			requestJson.put(Constant.UPDATETYPE, mUpdateType);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (3 == taskParams.length) {
			mSignID = taskParams[0];
			mUserPhone = taskParams[1];
			mUpdateType = taskParams[2];
		}
	}
}
