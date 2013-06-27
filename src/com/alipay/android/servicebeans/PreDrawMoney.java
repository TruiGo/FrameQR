package com.alipay.android.servicebeans;

import org.json.JSONObject;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConstants;
/**
 * 获取用户账户提现信息
 * @author caidie.wang
 *
 */
public class PreDrawMoney extends BaseServiceBean{
	
	public static String  BIZ_TAG = PreDrawMoney.class.getName();
	
	public PreDrawMoney(){
		operationType = ServiceBeanConstants.PRE_DRAW_MONEY;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
	}

	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		return requestJson.toString();
	}

}
