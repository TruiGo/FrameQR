package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.partner.PartnerInfo;
import com.alipay.android.ui.bean.BankCardInfo;

/**
 *  银行信息
 */
public class CCRBankCardInfo {
	
	private BankCardInfo bankCardInfo;
	
	private String cardIndexNumber;
	private String arriveDatetime;
	private String phoneNumber;
	private String amountLimit;
	private String smsTemplate;
	private String amountToDeposit;
	private String remindDate;//还款提醒时间
	private String isNewCard;//是否第一次在客户端还款
	private String isNeedAlert;//是否已经设置了push提醒
	
	/**
	 * 还款金额
	 */
	private String amount =  null;
	
	/**
	 * 合作方信息
	 */
	private PartnerInfo partnerInfo = null;
	
	private ArrayList<String> cardBinList = new ArrayList<String>();
	
	public CCRBankCardInfo(){
		if(bankCardInfo == null){
			bankCardInfo = new BankCardInfo();
		}
	}
	
	public CCRBankCardInfo processCCRBankInfoJson(JSONObject ccrBankInfo) {
		if (ccrBankInfo != null) {
			bankCardInfo.processBankCardInfoJson(ccrBankInfo);
			String param;
			param = ccrBankInfo.optString(Constant.RPF_CCR_CARD_INDEX_NUMBER);
			setCardIndexNumber(param);
			param = ccrBankInfo.optString(Constant.RPF_CCR_ARRIVEDATETIME);
			setArriveDatetime(param);
			param = ccrBankInfo.optString(Constant.RPF_CCR_PHONENUMBER);
			setPhoneNumber(param);
			param = ccrBankInfo.optString(Constant.RPF_CCR_AMOUNTLIMIT);
			setAmountLimit(param);
			param = ccrBankInfo.optString(Constant.RPF_CCR_REMINDDATE);
			setRemindDate(param);
			param = ccrBankInfo.optString(Constant.RPF_CCR_CARDBINLIST);
			setCardBinList(param);
		}
		return this;
	}
	
	public void setExistBankInfo(String bankName, String bankMark, 
			String userName, String cardIndexNumber, String amountLimit, String phoneNumber, String arriveDatetime, String remindDate){
		getBankCardInfo().setBankName(bankName);
		getBankCardInfo().setBankMark(bankMark);
		getBankCardInfo().setUserName(userName);
		setCardIndexNumber(cardIndexNumber);
		setAmountLimit(amountLimit);
		setArriveDatetime(amountLimit);
		setPhoneNumber(phoneNumber);
		setRemindDate(remindDate);
		getCreditCardTailNumber(cardIndexNumber);
	}
	
	public BankCardInfo getBankCardInfo() {
		return bankCardInfo;
	}

	public void setBankCardInfo(BankCardInfo bankCardInfo) {
		this.bankCardInfo = bankCardInfo;
	}

	public String getArriveDatetime() {
		return arriveDatetime;
	}

	public void setArriveDatetime(String arriveDatetime) {
		this.arriveDatetime = arriveDatetime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAmountLimit() {
		return amountLimit;
	}

	public void setAmountLimit(String amountLimit) {
		this.amountLimit = amountLimit;
	}

	public String getSmsTemplate() {
		return smsTemplate;
	}

	public void setSmsTemplate(String smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	public String getAmountToDeposit() {
		return amountToDeposit;
	}

	public void setAmountToDeposit(String amountToDeposit) {
		this.amountToDeposit = amountToDeposit;
	}

	public String getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(String remindDate) {
		this.remindDate = remindDate;
	}

	public String getIsNewCard() {
		return isNewCard;
	}

	public void setIsNewCard(String isNewCard) {
		this.isNewCard = isNewCard;
	}

	public String getIsNeedAlert() {
		return isNeedAlert;
	}

	public void setIsNeedAlert(String isNeedAlert) {
		this.isNeedAlert = isNeedAlert;
	}
	
	public void setCardBinList(String cardBinList) {
		processCardBinList(cardBinList);
	}

	public ArrayList<String> getCardBinList() {
		return cardBinList;
	}

	public String getCardIndexNumber() {
		return cardIndexNumber;
	}

	public void setCardIndexNumber(String mCardIndexNumber) {
		this.cardIndexNumber = mCardIndexNumber;
//		getCreditCardTailNumber(mCardIndexNumber);
		bankCardInfo.setCardTailNumber(getCreditCardTailNumber(mCardIndexNumber));
	}

	/**
	 * 获取信用卡尾号
	 * @return
	 */
	public String getCreditCardTailNumber() {
		if(bankCardInfo != null){
			return bankCardInfo.getCardTailNumber();
		}else{
			return "****";
		}
		
	}
	/**
	 * 信用卡索引号规则
	 * 8位日期“YYYYMMDD”+”明文卡号前6位”+”明文卡号后4位”+”6位顺序号”。
	 * @return
	 */
	public String getCreditCardTailNumber(String mCardIndexNumber) {
		String cardTailNumber = null;
		if(mCardIndexNumber != null && !mCardIndexNumber.equals("")){
			if(mCardIndexNumber.length()>18){
//				bankCardInfo.setCardTailNumber(mCardIndexNumber.substring(14, 18));
				cardTailNumber =  mCardIndexNumber.substring(14, 18);
			}
		}
		return cardTailNumber;
	}
	/**
	 * cardbin转化
	 * @param cardBinListStr
	 */
	private void processCardBinList(String cardBinListStr){
		if(cardBinListStr != null && cardBinListStr.length()>0){
			try {
				JSONArray binlist = new JSONArray(cardBinListStr);
				for(int i=0; i<binlist.length(); i++){
					String binItem;				
					binItem = binlist.optString(i);				
					cardBinList.add(binItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public PartnerInfo getPartnerInfo() {
		return partnerInfo;
	}

	public void setPartnerInfo(PartnerInfo partnerInfo) {
		this.partnerInfo = partnerInfo;
	}
	
}
