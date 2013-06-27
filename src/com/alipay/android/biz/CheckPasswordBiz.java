package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class CheckPasswordBiz {
	
	private ServiceBeanFactory serviceBeanFactory = null;
	
	public CheckPasswordBiz(){
		serviceBeanFactory = ServiceBeanFactory.getInstance();
	}
	
	public String check(String loginAccount, String loginPassword, String loginType, 
			String passwordType){
		BaseServiceBean checkPasswordService = serviceBeanFactory.getBean(ServiceBeanConfig.BEAN_NAME_CHECKPASSWORD);
		checkPasswordService.initParams(loginAccount, loginPassword, loginType, passwordType);
		return checkPasswordService.doX();
	}
	
	public static final String BIZTAG_CHECKPASSWORD = "CheckPassword";
	//检查支付宝支付密码
	public String checkPayPassword(String loginAccount, String payPassword) {
		BaseServiceBean checkPassword = serviceBeanFactory.getBean(BIZTAG_CHECKPASSWORD);
		checkPassword.initParams(loginAccount, payPassword, "alipay", "trade");
		return checkPassword.doX();
	}
	
	//检查登陆密码，包括淘宝登陆密码和支付宝登陆密码
	public String checkLoginPassword(String loginAccount, String loginPassword, String loginType) {
		BaseServiceBean checkPassword = serviceBeanFactory.getBean(BIZTAG_CHECKPASSWORD);
		checkPassword.initParams(loginAccount, loginPassword, loginType, "login");
		return checkPassword.doX();
	}
	
	//检查taobao登陆密码
	public String checkTaobaoLoginPassword(String loginAccount, String loginPassword) {
		BaseServiceBean checkPassword = serviceBeanFactory.getBean(BIZTAG_CHECKPASSWORD);
		checkPassword.initParams(loginAccount, loginPassword, "taobao", "login");
		return checkPassword.doX();
	}
	
	//检查alipay登陆密码
	public String checkAlipayLoginPassword(String loginAccount, String loginPassword) {
		BaseServiceBean checkPassword = serviceBeanFactory.getBean(BIZTAG_CHECKPASSWORD);
		checkPassword.initParams(loginAccount, loginPassword, "alipay", "login");
		return checkPassword.doX();
	}
}
