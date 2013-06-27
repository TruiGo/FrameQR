package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

public class GetVerifyRecordList extends BaseServiceBean{
	public static String BIZ_TAG = GetStoreList.class.getName();
	String voucherId;
	String voucherFrom;
	String outBizNo;
	int currentPage;
	int pageCount;
	
	public GetVerifyRecordList() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_GETVERIFYRECORDLIST;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(ServiceBeanConstants.VOUCHERID, voucherId);
			requestJson.put(ServiceBeanConstants.VOUCHERFROM, voucherFrom);
			requestJson.put(ServiceBeanConstants.OUTBIZNO, outBizNo);
			requestJson.put(ServiceBeanConstants.CURRENTPAGE, currentPage);
			requestJson.put(ServiceBeanConstants.PAGECOUNT, pageCount);
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
		currentPage = Integer.parseInt(taskParams[3]);
		pageCount = Integer.parseInt(taskParams[4]);
	}
}
