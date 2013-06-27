package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class GetExpressCardLimitList extends BaseServiceBean {

	String mInstId; //银行短名称,如ICBC
	String mCardType; //DC—借记卡，CC—贷记卡
	
	public GetExpressCardLimitList() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_EXPRESSCARDLIMITLIST;
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
