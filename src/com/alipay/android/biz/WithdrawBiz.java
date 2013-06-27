package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

/**
 * 提现业务逻辑
 * @author caidie.wang
 * 与提现接口
 * 提现接口
 * 提现校验接口
 */
public class WithdrawBiz {
	private ServiceBeanFactory serviceBeanFactory = null;
	
	public WithdrawBiz(){
		serviceBeanFactory = ServiceBeanFactory.getInstance();
	}
	/**
	 * 获取提现信息
	 * @param sessionId
	 * @param clientId
	 * @param operationType
	 */
	public String sendPreDrawMoney(){
		BaseServiceBean preDrawMoney = serviceBeanFactory.getBean(ServiceBeanConfig.BEAN_NAME_PRE_DRAW_MONEY);
		preDrawMoney.initParams();
		return preDrawMoney.doX();
	}
	/**
	 * 提交提现信息
	 * @param bankId
	 * @param amount
	 * @param payPassword
	 * @return
	 */
	public String sendDrawMoney(String bankId,String amount,String payPassword){
		BaseServiceBean drawMoney = getDrawMoney(bankId,amount,payPassword,null,null);
		return drawMoney.doX();
	}
	/**
	 * 获取提现短信校验码
	 * @param needResendRandomCode
	 * @return
	 */
	public String resendRandomCode(String needResendRandomCode) {
		BaseServiceBean drawMoney = getDrawMoney(null,null,null,needResendRandomCode,null);
		return drawMoney.doX();
	}
	/**
	 * 提交提现短信校验码
	 * @param randomCode
	 * @return
	 */
	public String submitRandomCode(String randomCode){
		BaseServiceBean drawMoney = getDrawMoney(null,null,null,null,randomCode);
		return drawMoney.doX();
	}
	/**
	 * 生成DrawMoney
	 * @param bankId
	 * @param amount
	 * @param payPassword
	 * @param needResendRandomCode
	 * @param randowCode
	 * @return
	 */
	private BaseServiceBean getDrawMoney(String bankId,String amount,String payPassword,String needResendRandomCode,String randowCode){
		BaseServiceBean drawMoney = serviceBeanFactory.getBean(ServiceBeanConfig.BEAN_NAME_DRAW_MONEY);
		drawMoney.initParams(bankId,amount,payPassword,needResendRandomCode,randowCode);
		return drawMoney;
	}
	
	
	
}
