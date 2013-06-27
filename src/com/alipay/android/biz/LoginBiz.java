package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class LoginBiz {
	
	private ServiceBeanFactory serviceBeanFactory = null;
	
	public LoginBiz(){
		serviceBeanFactory = ServiceBeanFactory.getInstance();
	}
	
	public static final String BIZTAG_GETRSAKEY = "getRSAKey";
	public String getRSAKey() {
		BaseServiceBean getRSAKey = serviceBeanFactory.getBean(ServiceBeanConfig.BEAN_NAME_GETRSAKEY);
		getRSAKey.initParams();
		return getRSAKey.doX();
	}
	
	public String doSimplelogin(String loginAccount, String loginPassword, String loginType, 
			String userAgent, String productId, String productVersion,
			String screenWidth, String screenHeight){
		BaseServiceBean doLogin = serviceBeanFactory.getBean(ServiceBeanConfig.BEAN_NAME_DOLOGIN);
		doLogin.initParams(loginAccount, loginPassword, loginType,
				userAgent, productId, productVersion, screenWidth, screenHeight);
		return doLogin.doX();
	}
}
