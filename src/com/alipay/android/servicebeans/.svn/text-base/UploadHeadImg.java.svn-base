package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;

public class UploadHeadImg extends BaseServiceBean{
	
	String headImg;
	String fileName;
	
	public UploadHeadImg() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_UPLOADHEADIMG;
	}
	
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		
		try {
			requestJson.put(ServiceBeanConstants.HEAD_IMG, headImg);
			requestJson.put(ServiceBeanConstants.FILE_NAME, fileName);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJson.toString();
	}
	
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		headImg = taskParams[0];
		fileName = taskParams[1];
	}
}
