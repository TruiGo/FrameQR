package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

/**
 *  银行信息
 */
public class BankItemInfo {
	
	public String mBankName = "";
	public String mBankMark = "";
	public String mArriveDatetime = "";
	public String mPhoneNumber = "";
	public String mAmountLimit = "";
	public String mSMSTemplate ="";
	public String mUserName = "";
	public String mCardIndexNumber = "";
	public String mAmountToDeposit = "";
	public String mCreditCardTailNumber = "";//信用卡卡号后四位
	public String mRemindDate = "";//还款提醒时间
	public String mIsNewCard = "";//是否第一次在客户端还款
	public String mIsNeedAlert = "";//是否已经设置了push提醒
	
	private ArrayList<String> mCardBinList = new ArrayList<String>();
	public void setSupportBankInfo(String bankName, String bankMark, 
			String arriveDatetime, String phoneNumber, String amountLimit,String cardBinList){
		mBankName = bankName;
		mBankMark = bankMark;
		mArriveDatetime = arriveDatetime;
		mPhoneNumber = phoneNumber;
		mAmountLimit = amountLimit;
		try {
			JSONArray binlist = new JSONArray(cardBinList);
			for(int i=0; i<binlist.length(); i++){
				String binItem;				
				binItem = binlist.optString(i);				
				mCardBinList.add(binItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void setExistBankInfo(String bankName, String bankMark, 
			String userName, String cardIndexNumber, String amountLimit, String phoneNumber, String arriveDatetime, String remindDate){
		mBankName = bankName;
		mBankMark = bankMark;
		mUserName = userName;
		mCardIndexNumber = cardIndexNumber;
		mAmountLimit = amountLimit;
		mArriveDatetime = arriveDatetime;
		mPhoneNumber = phoneNumber;
		mRemindDate = remindDate;
		setmCreditCardTailNumber(mCardIndexNumber);
	}

	public String getmBankName() {
		return mBankName;
	}
	public String getRemindDate() {
		return mRemindDate;
	}

	public String getmBankMark() {
		return mBankMark;
	}
	
	public String getmUserName() {
		return mUserName;
	}
	public String getmArriveDatetime() {
		return mArriveDatetime;
	}

	public String getmBankPhoneNumber() {
		return mPhoneNumber;
	}
	public String getmAmountLimit() {
		return mAmountLimit;
	}
	public String getmCardIndexNumber() {
		return mCardIndexNumber;
	}
	public void setmCardIndexNumber(String mCardIndexNumber) {
		this.mCardIndexNumber = mCardIndexNumber;
		setmCreditCardTailNumber(mCardIndexNumber);
	}
	public String getmSMSTemplate() {
		return mSMSTemplate;
	}
	public String getmAmountToDeposit() {
		return mAmountToDeposit;
	}
	public ArrayList<String> getCardBinList(){
		return mCardBinList;
	}
	public String getmIsNewCard() {
		return mIsNewCard;
	}
	public void setmIsNewCard(String mIsNewCard) {
		this.mIsNewCard = mIsNewCard;
	}
	
	public String getmIsNeedAlert() {
		return mIsNeedAlert;
	}
	public void setmIsNeedAlert(String mIsNeedAlert) {
		this.mIsNeedAlert = mIsNeedAlert;
	}
	/**
	 * 获取信用卡尾号
	 * @return
	 */
	public String getmCreditCardTailNumber() {
		return mCreditCardTailNumber;
	}
	/**
	 * 信用卡索引号规则
	 * 8位日期“YYYYMMDD”+”明文卡号前6位”+”明文卡号后4位”+”6位顺序号”。
	 * @return
	 */
	public void setmCreditCardTailNumber(String mCardIndexNumber) {
		if(mCardIndexNumber != null && !mCardIndexNumber.equals("")){
			if(mCardIndexNumber.length()>18){
				mCreditCardTailNumber = mCardIndexNumber.substring(14, 18);
			}else{
				mCreditCardTailNumber = "****";
			}
			
		}
	}
	
	
}
