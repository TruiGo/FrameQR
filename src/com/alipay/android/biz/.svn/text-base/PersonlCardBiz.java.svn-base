package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class PersonlCardBiz {
	private ServiceBeanFactory serviceFactory = ServiceBeanFactory
			.getInstance();

	public String queryNameCard() {
		BaseServiceBean queryNameCard = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_QUERYNAMECARD);
		queryNameCard.initParams();
		return queryNameCard.doX();
	}
	
	public String uploadHeadImg(String headImg_base64,String fileName) {
		BaseServiceBean uploadHeadImg = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_UPLOADHEADIMG);
		uploadHeadImg.initParams(headImg_base64,fileName);
		return uploadHeadImg.doX();
	}

}
