package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;


public class GetBankCardList extends BaseServiceBean {

	public GetBankCardList(){
		operationType = ServiceBeanConstants.OPERATION_TYPE_SIGNEXPRESSCARDLIST;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		return (null != requestJson) ? requestJson.toString() : null;
	}

}
