package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;

public class DoLogin extends BaseServiceBean {

	public String mLoginAccount;
	public String mLoginPassword;
	public String mLoginType;
	public String mUserAgent;
	public String mProductId;
	public String mProductVersion;
	public String mScreenWidth;
	public String mScreenHeight;
	
	public DoLogin() {
		operationType = Constant.OP_LOGIN;
	}
	
	@Override
	public void initParams(String... taskParams) {
		// TODO Auto-generated method stub
		super.initParams(taskParams);

		 if (8 == taskParams.length) {
			 mLoginAccount = taskParams[0];
			 mLoginPassword = taskParams[1];
			 mLoginType = taskParams[2];
			 mUserAgent = taskParams[3];
			 mProductId = taskParams[4];
			 mProductVersion = taskParams[5];
			 mScreenWidth = taskParams[6];
			 mScreenHeight = taskParams[7];
		 }
	}

	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.RQF_LOGIN_ACCOUNT, mLoginAccount);
			requestJson.put(Constant.RQF_RSALOGIN_PASSWORD, mLoginPassword);
			requestJson.put(Constant.RQF_LOGINTYPE, mLoginType);
			requestJson.put(Constant.RQF_USER_AGENT, mUserAgent);
			requestJson.put(Constant.RQF_PRODUCT_ID, mProductId);
			requestJson.put(Constant.RQF_PRODUCT_VERSION, mProductVersion);
			requestJson.put(Constant.RQF_SCREEN_WIDTH, mScreenWidth);
			requestJson.put(Constant.RQF_SCREEN_HIGH, mScreenHeight);
			
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
