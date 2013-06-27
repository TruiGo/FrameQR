package com.alipay.android.servicebeans;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.net.http.RespData;

public class CheckCardBin extends BaseServiceBean {

	public String mCardNo;
	
	public CheckCardBin() {
		operationType = Constant.OP_GET_RSA_KEY;
	}
	
	@Override
	protected String buildRequestAsString() {
		return null;
	}

	@Override
	public void initParams(String... taskParams) {
		 if (1 == taskParams.length) {
			 mCardNo = taskParams[0];
		 }
	}
	
	private String getUrl() {
		return Constant.getCcdcURL(mContext) + "checkCardBin.json?" + "cardNo=" + mCardNo + "&doUpdate=false";
		//return /*Constant.getCcdcURL(mContext)*/ "http://ccdc.stable.alipay.net/"+ "checkCardBin.json?" + "cardNo=" + mCardNo + "&doUpdate=false";
	}
	
	@Override
    public String doX() {
    	APHttpClient aPHttpClient = new APHttpClient(getUrl(), mContext);
    	RespData resp = aPHttpClient.sendSynchronousGetRequest(null);
        return null != resp ? resp.strResponse : null;
    }
}
