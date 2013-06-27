package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class GetStoreList extends BaseServiceBean{
	public static String BIZ_TAG = GetStoreList.class.getName();
	
	String goodsId;
	int currentPage;
	int pageCount;
	String cityId;
	String voucherFrom;
	String queryType;
	String locationParams;
	
	public GetStoreList() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_GETSTORELIST;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = null;
		try {
			if(locationParams != null){
				requestJson = new JSONObject(locationParams);;
				requestJson.put(Constant.RQF_OPERATION_TYPE, operationType);
			}else{
				requestJson = prepareRequest();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			if(requestJson == null){
				requestJson = prepareRequest();
			}
		}
		
		try {
			requestJson.put(ServiceBeanConstants.GOODSID, goodsId);
			requestJson.put(ServiceBeanConstants.CITYID, cityId);
			requestJson.put(ServiceBeanConstants.VOUCHERFROM, voucherFrom);
			requestJson.put(ServiceBeanConstants.CURRENTPAGE, currentPage);
			requestJson.put(ServiceBeanConstants.QUERYTYPE,queryType);
			requestJson.put(ServiceBeanConstants.PAGECOUNT, pageCount);
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		goodsId = taskParams[0];
		cityId = taskParams[1];
		voucherFrom = taskParams[2];
		currentPage = Integer.parseInt(taskParams[3]);
		pageCount = Integer.parseInt(taskParams[4]);
		queryType = taskParams[5];
		locationParams = taskParams[6];
	}
	
	/*@Override
	public String doX() {
		APHttpClient aPHttpClient = new APHttpClient("http://www.chiplife.net/mobile/location/index.php",mContext);
        String response = aPHttpClient.sendSynchronousBizRequest(buildRequestAsString());
    	extractBasicResponse(response);
        return response;
	}*/
}
