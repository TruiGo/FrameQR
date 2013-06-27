package com.alipay.android.servicebeans;

import org.json.JSONObject;

public class GetDynamicId extends BaseServiceBean {
	
	public static String BIZ_TAG = GetDynamicId.class.getName();
	//定义请求需要的变量

	public GetDynamicId() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_GETDYNAMICID;
	}
	
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
//		requestJson.put(name, value)
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
	}
}
