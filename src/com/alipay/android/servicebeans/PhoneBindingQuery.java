package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;


public class PhoneBindingQuery extends BaseServiceBean {
	public static String BIZ_TAG = PhoneBindingQuery.class.getName();
	String mobileNo;
	public PhoneBindingQuery() {
        operationType = ServiceBeanConstants.OPERATION_TYPE_QUERYMOBILEBINDING;
    }
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		mobileNo = taskParams[0];
	}

	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.RQF_FEEDBACK_MOBILENO, mobileNo);
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
}
