package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;

public class CheckPassword extends BaseServiceBean {
	public String mLoginAccount;
	public String mLoginPassword;
	public String mRsaLoginPassword;
	public String mLoginType;
	public String mPasswordType; //密码类型 login/trade，前提loginType=alipay 
	
	public CheckPassword() {
		operationType = Constant.OP_CHECKPASSWORD;
	}
	
	@Override
	public void initParams(String... taskParams) {
		// TODO Auto-generated method stub
		super.initParams(taskParams);

		 if (4 == taskParams.length) {
			 mLoginAccount = taskParams[0];
			 mLoginPassword = taskParams[1];
			 mLoginType = taskParams[2];
			 mPasswordType = taskParams[3];
		 }
	}

	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.RQF_LOGIN_ACCOUNT, mLoginAccount);
			requestJson.put(Constant.RQF_RSALOGIN_PASSWORD, mLoginPassword);
			requestJson.put(Constant.RQF_LOGINTYPE, mLoginType);
			requestJson.put(Constant.RQF_PASSWORDTYPE, mPasswordType);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
    public String doX() {
    	APHttpClient aPHttpClient = new APHttpClient(Constant.getContainerURL(mContext),mContext);
        String response = aPHttpClient.sendSynchronousBizRequest(buildRequestAsString());
    	extractBasicResponse(response);
        return response;
    }
}
