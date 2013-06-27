package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class AlipayClientPubBiz {
	private ServiceBeanFactory serviceFactory = ServiceBeanFactory.getInstance();
	
	public String getTaobaoSid(){
		BaseServiceBean getTaobaoSid = serviceFactory.getBean(ServiceBeanConfig.BEAN_NAME_GETTAOBAOSID);
		getTaobaoSid.initParams();
		return getTaobaoSid.doX();
	}

}
