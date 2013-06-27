package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

/**
 * 卡管理业务逻辑类
 * @author caidie.wang
 * 获取卡列表接口
 */
/**
 * @author dong.liu
 *
 */
public class CardManagerBiz {
	
	private ServiceBeanFactory serviceBeanFactory = null;
	
	public CardManagerBiz(){
		serviceBeanFactory = ServiceBeanFactory.getInstance();
	}
	
	public String getCardList(){
		BaseServiceBean getCardList = serviceBeanFactory.getBean(ServiceBeanConfig.GET_CARD_LIST);
		getCardList.initParams();
		return getCardList.doX();
	}
	
	//检查卡号合法性，获得对应的银行、卡类型。如果是信用卡，返回的UUID就是卡号cachekey
	public static final String BIZTAG_CHECKCARDBIN = "CheckCardBin";
	public String checkCardBin(String cardNo) {
		BaseServiceBean checkCarBin = serviceBeanFactory.getBean(BIZTAG_CHECKCARDBIN);
		checkCarBin.initParams(cardNo);
		return checkCarBin.doX();
	}

	//签约验证
	public static final String BIZTAG_VERIFYEXPRESSCARD = "VerifyExpressCard";
	public String verifyExpressCard(String bankShortName, String cardType, String cardNo, String cardNoType,
			String expireDate, String cvv2, String holderName, String mobile, String certNo, String certType) {
		BaseServiceBean verifyExpressCard = serviceBeanFactory.getBean(BIZTAG_VERIFYEXPRESSCARD);
		verifyExpressCard.initParams(bankShortName, cardType, cardNo, cardNoType, expireDate, 
        		cvv2, holderName, mobile, certNo, certType);
		return verifyExpressCard.doX();
	}
	
	//签约请求
	public static final String BIZTAG_SIGNEXPRESSCARD = "SignExpressCard";
	public String signExpressCard(String signId, String verifyCode, String resendVerifyCode) {
		BaseServiceBean signExpressCard = serviceBeanFactory.getBean(BIZTAG_SIGNEXPRESSCARD);
		signExpressCard.initParams(signId, verifyCode, resendVerifyCode);
		return signExpressCard.doX();
	}
	
	//修改/添加手机号校验接口
	public static final String BIZTAG_VERIFYMODIFYEXPRESSUSERPHONE = "VerifyModifyExpressUserPhone";
	public String verifyModifyExpressUserPhone(String signId, String userPhone, String updateType) {
		BaseServiceBean verifyModifyExpressUserPhone = serviceBeanFactory.getBean(BIZTAG_VERIFYMODIFYEXPRESSUSERPHONE);
		verifyModifyExpressUserPhone.initParams(signId, userPhone, updateType);
		return verifyModifyExpressUserPhone.doX();
	}
	
	//修改/添加签约手机号
	public static final String BIZTAG_MODIFYEXPRESSUSERPHONE = "ModifyExpressUserPhone";
	public String modifyExpressUserPhone(String verifyCode, String resendVerifyCode) {
		BaseServiceBean modifyExpressUserPhone = serviceBeanFactory.getBean(BIZTAG_MODIFYEXPRESSUSERPHONE);
		modifyExpressUserPhone.initParams(verifyCode, resendVerifyCode);
		return modifyExpressUserPhone.doX();
	}
	
	//关闭快捷
	public static final String BIZTAG_CANCELSAVEDEXPRESSCARD = "CancelSavedExpressCard";
	public String CancelSavedExpressCard(String signId, String cardType, 
			String payPassword, String cardNo, String delFromCCR, String delFromCifWithdraw) {
		BaseServiceBean cancelSavedExpressCard = serviceBeanFactory.getBean(BIZTAG_CANCELSAVEDEXPRESSCARD);
		cancelSavedExpressCard.initParams(signId, cardType, payPassword, cardNo, delFromCCR, delFromCifWithdraw);
		return cancelSavedExpressCard.doX();
	}
	
	//银行卡限额查询
	public static final String BIZTAG_GETEXPRESSCARDLIMIT = "GetExpressCardLimitList";
	public String getExpressCardLimit(String bankShortName, String cardType) {
		BaseServiceBean getExpressCardLimit = serviceBeanFactory.getBean(BIZTAG_GETEXPRESSCARDLIMIT);
		getExpressCardLimit.initParams(bankShortName, cardType);
		return getExpressCardLimit.doX();
	}
	
	//银行卡签约表单查询
	public static final String BIZTAG_GETSUPPORTEXPRESSBANKLIST = "GetSupportExpressBankList";
	public String GetExpressBankParamsForSign(String bankShortName, String cardType) {
		BaseServiceBean getExpressBankParamsForSign = serviceBeanFactory.getBean(BIZTAG_GETSUPPORTEXPRESSBANKLIST);
		getExpressBankParamsForSign.initParams(bankShortName, cardType);
		return getExpressBankParamsForSign.doX();
	}
	
	//删除单张卡记录接口（无需输入密码）
	public static final String BIZTAG_DELETECARDWITHOUTPWD = "DeleteCardWithoutPWD";
	public String deleteCardWithoutPWD(String cardNo, String cardType, String bankShortName, String actionType) {
		BaseServiceBean deleteCardWithoutPWD = serviceBeanFactory.getBean(BIZTAG_DELETECARDWITHOUTPWD);
		deleteCardWithoutPWD.initParams(cardNo, cardType, bankShortName, actionType);
		return deleteCardWithoutPWD.doX();
	}
}
