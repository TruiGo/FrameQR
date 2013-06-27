package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConstants;
/**
 * 提现接口
 * @author caidie.wang
 *
 */
public class DrawMoney extends BaseServiceBean{
	
	public static String BIZ_TAG = DrawMoney.class.getName();
	public static String BIZ_NEED_RESEND_RANDOM_CODE = "BIZ_NEED_RESEND_RANDOM_CODE";
	public static String BIZ_RANDOM_CODE = "BIZ_RANDOM_CODE";
	
	private String amount;
	private String bankId;
	private String payPassword;
	private String needResendRandomCode;
	private String randomCode;
	
	
	public DrawMoney(){
		operationType = ServiceBeanConstants.DRAW_MONEY;
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		bankId = taskParams[0];
		amount = taskParams[1];
		payPassword = taskParams[2];
		needResendRandomCode = taskParams[3];
		randomCode = taskParams[4];
	}

	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(ServiceBeanConstants.BANK_ID, bankId);
			requestJson.put(ServiceBeanConstants.AMOUNT, amount);
			requestJson.put(ServiceBeanConstants.PAY_PASSWORD, payPassword);
			requestJson.put(ServiceBeanConstants.NEED_RESEND_RANDOM_CODE, needResendRandomCode);
			requestJson.put(ServiceBeanConstants.RANDOM_CODE, randomCode);
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
