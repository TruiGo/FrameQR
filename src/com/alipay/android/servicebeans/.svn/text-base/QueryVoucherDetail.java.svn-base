package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;


public class QueryVoucherDetail extends BaseServiceBean{
	String voucherId;
	String voucherFrom;
	String outBizNo;
	String resolution;
	String userAgent;
	
	public QueryVoucherDetail() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_QUERYVOUCHERDETAIL;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(ServiceBeanConstants.VOUCHERID, voucherId);
			requestJson.put(ServiceBeanConstants.VOUCHERFROM, voucherFrom);
			requestJson.put(ServiceBeanConstants.OUTBIZNO, outBizNo);
			requestJson.put(ServiceBeanConstants.RESOLUTION, resolution);
			requestJson.put(ServiceBeanConstants.USERAGENT, userAgent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return requestJson.toString();
	}
	
	
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		voucherId = taskParams[0];
		voucherFrom = taskParams[1];
		outBizNo = taskParams[2];
		resolution = taskParams[3];
		userAgent = taskParams[4];
	}
	
	/*@Override
	public String doX() {
		APHttpClient aPHttpClient = new APHttpClient("http://www.chiplife.net/mobile/location/index.php",mContext);
        String response = aPHttpClient.sendSynchronousBizRequest(buildRequestAsString());
    	extractBasicResponse(response);
        return response;
	}*/
}
