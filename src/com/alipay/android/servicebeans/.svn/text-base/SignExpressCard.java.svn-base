package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class SignExpressCard extends BaseServiceBean {

	String mSignID; //快捷协议ID 由签约验证请求获得。重发验证码时可以传空
	String mVerifyCode; //重发验证码时可以传空
	String mResendVerifyCode; //rue-重发，此时服务器忽略其他业务参数，false-不重发，默认值。
	
	public SignExpressCard() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_SIGNEXPRESSCARD;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.SIGNID, mSignID);
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
		
		if (3 == taskParams.length) {
			mSignID = taskParams[0];
			mVerifyCode = taskParams[1];
			mResendVerifyCode = taskParams[2];
		}
	}

}
