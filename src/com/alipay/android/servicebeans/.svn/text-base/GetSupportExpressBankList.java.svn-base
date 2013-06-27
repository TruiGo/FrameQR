package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class GetSupportExpressBankList extends BaseServiceBean {

	String mInstId; //银行简称，如ICBC表示工商银行。当为空时，返回全部银行列表
	String mCardType; //DC--借记卡，CC--贷记卡。当为空时，返回所有类型
	
	public GetSupportExpressBankList() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_SUPPORTEXPRESSBANKLIST;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.INSTID, mInstId);
			requestJson.put(Constant.CARDTYPE, mCardType);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (2 == taskParams.length) {
			mInstId = taskParams[0];
			mCardType = taskParams[1];
		}
	}

}
