package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

public class GetTransferList extends BaseServiceBean{
	public static String BIZ_TAG = GetTransferList.class.getName();
	int querySize = 100; //请求交易记录条数
	int resultSize = 20;//返回转账用户记录数    

	
    public GetTransferList() {
        operationType = ServiceBeanConstants.OPERATION_TYPE_QUERYTRANSFERANDPAYLIST;
    }

    @Override
    protected String buildRequestAsString() {
    	try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(ServiceBeanConstants.QUERYSIZE, querySize);
			requestJson.put(ServiceBeanConstants.RESULTSIZE, resultSize);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
    }
}
