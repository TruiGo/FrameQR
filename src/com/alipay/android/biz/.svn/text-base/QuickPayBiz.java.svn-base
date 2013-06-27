package com.alipay.android.biz;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class QuickPayBiz {
	 private ServiceBeanFactory serviceFactory  = ServiceBeanFactory.getInstance();
	 
	
	    public String getDynamicId() {
	        BaseServiceBean getDynamicId = serviceFactory
	            .getBean(ServiceBeanConfig.BEAN_NAME_GETDYNAMICID);
	        getDynamicId.initParams();
	        return getDynamicId.doX();
	    }
	    
	    public String sendPayeeInfo(String dynamicId) {
	        BaseServiceBean sendPayeeInfo = serviceFactory
	            .getBean(ServiceBeanConfig.BEAN_NAME_SENDPAYEEINFO);
	        sendPayeeInfo.initParams(dynamicId);
	        return sendPayeeInfo.doX();
	    }  
	    
	    public String checkPhoneBlack(String manufacturer,String phoneModel,String osVersion) {
	        BaseServiceBean checkPhoneBlack = serviceFactory
	            .getBean(ServiceBeanConfig.BEAN_NAME_CHECKPHONEBLACK);
	        checkPhoneBlack.initParams(manufacturer,phoneModel,osVersion);
	        return checkPhoneBlack.doX();
	    }  
	    
	    public String createFastPay(String dynamicId,String money) {
	        BaseServiceBean sendPayeeInfo = serviceFactory
	            .getBean(ServiceBeanConfig.BEAN_NAME_CREATEFASTPAY);
	        sendPayeeInfo.initParams(dynamicId,money);
	        return sendPayeeInfo.doX();
	    }  
	    
	    
	    public String createFastPay(String dynamicId, String money,
                RootActivity activity) {
	    	
				BaseServiceBean createFastPay = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_CREATEFASTPAY);
				
				// 业务联网参数设置
				createFastPay.initParams(dynamicId, money);
				return createFastPay.doX();

			}
	    
}
