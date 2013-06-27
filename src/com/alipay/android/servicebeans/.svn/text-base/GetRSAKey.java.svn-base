package com.alipay.android.servicebeans;

import org.json.JSONObject;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;

public class GetRSAKey extends BaseServiceBean {

	public GetRSAKey() {
		operationType = Constant.OP_GET_RSA_KEY;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		return (null != requestJson) ? requestJson.toString() : null;
	}

	@Override
    public String doX() {
    	APHttpClient aPHttpClient = new APHttpClient(Constant.getContainerURL(mContext),mContext);
        String response = aPHttpClient.sendSynchronousBizRequest(buildRequestAsString());
    	extractBasicResponse(response);
        return response;
    }
}
