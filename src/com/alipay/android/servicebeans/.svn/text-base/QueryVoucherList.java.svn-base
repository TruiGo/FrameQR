package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;


public class QueryVoucherList extends BaseServiceBean{
	String status;
	int currentPage;
	int pageCount;
	String beginDate;
	String endDate;
	
	public QueryVoucherList() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_QUERYVOUCHERLIST;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(ServiceBeanConstants.STATUS, status);
			requestJson.put(ServiceBeanConstants.CURRENTPAGE, currentPage);
			requestJson.put(ServiceBeanConstants.PAGECOUNT, pageCount);
			requestJson.put(ServiceBeanConstants.BEGINDATE, beginDate);
			requestJson.put(ServiceBeanConstants.ENDDATE, endDate);
			requestJson.put(ServiceBeanConstants.APIVERSION, "2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		status = taskParams[0];
		currentPage = isNullParam(taskParams[1]) ? 1 :Integer.parseInt(taskParams[1]);
		pageCount = isNullParam(taskParams[2]) ? 10 :Integer.parseInt(taskParams[2]);
		beginDate = taskParams[3];
		endDate = taskParams[4];
	}

	private boolean isNullParam(String param){
		return param == null || "".equals(param);
	}
	
	/*@Override
	public String doX() {
		APHttpClient aPHttpClient = new APHttpClient("http://www.chiplife.net/mobile/location/index.php",mContext);
        String response = aPHttpClient.sendSynchronousBizRequest(buildRequestAsString());
    	extractBasicResponse(response);
        return response;
	}*/
}
