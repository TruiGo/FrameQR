//package com.alipay.android.servicebeans;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class QueryBalance extends BaseServiceBean{
//	
//	public static String BIZ_TAG = QueryBalance.class.getName();
//	
//	private String refreshCardList;
//	private String refreshBillList;
//	private String refreshCouponList;
//	
//	
//	public QueryBalance(){
//		operationType = ServiceBeanConstants.OPERATION_TYPE_QUERYBALANCE;
//	}
//	
//	@Override
//	public void initParams(String... taskParams) {
//		super.initParams(taskParams);
//		refreshCardList = taskParams[0];
//		refreshBillList = taskParams[1];
//		refreshCouponList = taskParams[2];
//	}
//	
//
//	@Override
//	protected String buildRequestAsString() {
//		try {
//			JSONObject requestJson = prepareRequest();
//			requestJson.put(ServiceBeanConstants.REFRESHCARDLIST, refreshCardList);
//			requestJson.put(ServiceBeanConstants.REFRESHBILLLIST, refreshBillList);
//			requestJson.put(ServiceBeanConstants.REFRESHCOUPONLIST, refreshCouponList);
//			return requestJson.toString();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//        return null;
//	}
//
//}
