package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class ModifyExpressUserPhone extends BaseServiceBean {

	String mVerifyCode;
	String mResendVerifyCode;
	
	public ModifyExpressUserPhone() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_MODIFYEXPRESSUSERPHONE;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.VERIFYCODE, mVerifyCode);
			requestJson.put(Constant.RESENDVERIFYCODE, mResendVerifyCode);
			
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
			mVerifyCode = taskParams[0];
			mResendVerifyCode = taskParams[1];
		}
	}
}
