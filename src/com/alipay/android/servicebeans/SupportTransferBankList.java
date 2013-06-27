package com.alipay.android.servicebeans;

import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class SupportTransferBankList extends BaseServiceBean{
	public static String BIZ_TAG = SupportTransferBankList.class.getName();
	String bankShortName;
	
	public SupportTransferBankList() {
		operationType = Constant.OP_SUPPORTTRANSFERBANKLIST;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		return requestJson.toString();
	}
}
