package com.alipay.android.servicebeans;

import org.json.JSONObject;

public class QueryNameCard extends BaseServiceBean{

	public QueryNameCard() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_QUERYNAMECARD;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
	}
}
