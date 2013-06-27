package com.alipay.android.servicebeans;

import org.json.JSONObject;

public class GetCardList extends BaseServiceBean{
	
	public static String BIZ_TAG = GetCardList.class.getName();
	/**
	 * 业务类型
	 * 1：表示信用卡还款业务
	 * 2：表示提现业务
	 * 若为空服务端将返回全部业务列表
	 */
	private int cardBizType ;
	
	
	public GetCardList(){
		operationType = ServiceBeanConstants.GET_CARD_LIST;
	}

	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		return requestJson.toString();
	}

}
