package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

//验证签约
public class VerifySMSCode extends BaseServiceBean {

	String mSignID;
	String mVerifyCode;
	
	public VerifySMSCode() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_VERIFYEXPRESSCARD;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.SIGNID, mSignID);
			requestJson.put(Constant.VERIFYCODE, mVerifyCode);
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (2 == taskParams.length) {
			mSignID = taskParams[0];
			mVerifyCode = taskParams[1];
		}
	}

}
