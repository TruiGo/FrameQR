package com.alipay.android.servicebeans;

import org.json.JSONObject;

import android.content.Context;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.ConfigData;
import com.alipay.android.net.alipayclient2.APHttpClient;

public abstract class BaseServiceBean {
    // The request filed. 
    protected String operationType;
    protected String clientID;
    protected Context mContext;
    private AlipayApplication mApplication;
    
    // The response filed.
    public String resultStatus;
    public String sessionId;
    public String memo;
    private ConfigData configData;
    
    public void setContext(Context context){
    	this.mContext = context;
    	mApplication = (AlipayApplication)mContext.getApplicationContext();
    	configData = mApplication.getConfigData();
    }
    
    public void initParams(String... taskParams){
		clientID = configData.getClientId();
		sessionId = configData.getSessionId();
    }
    
    protected JSONObject prepareRequest() {
        try {
            JSONObject request = new JSONObject();
            request.put(Constant.RQF_OPERATION_TYPE, operationType);
            request.put(Constant.RQF_SMSTRANS_SESSIONID, sessionId);
            request.put(Constant.RQF_CLIENT_ID, clientID);
            return request;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    protected void extractBasicResponse(String response) {
        try {
            JSONObject responseJson = new JSONObject(response);
            resultStatus = responseJson.optString("resultStatus");
            sessionId = responseJson.optString("sessionId");
            memo = responseJson.optString("memo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected abstract String buildRequestAsString();
    
    public String doX() {
    	APHttpClient aPHttpClient = new APHttpClient(Constant.getAlipayURL(mContext),mContext);
        String response = aPHttpClient.sendSynchronousBizRequest(buildRequestAsString());
    	extractBasicResponse(response);
        return response;
    }
}
