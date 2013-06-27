package com.alipay.android.ui.bean;

import android.app.Activity;

import com.alipay.android.client.util.Utilz;
import com.eg.android.AlipayGphone.R;

/**
 * 银行卡列表Item数据处理
 * @author caidie.wang
 *
 */
public class BankCardListItemInfo {
	
	private String bankName = null;
	private String bankTialNumber = null;
	private String cardTppeStr = null;
	private String bizTypeString = null;
	private boolean isShowIcon1Res ;
	
	/**
	 * 
	 * @param bankCardInfo
	 */
	public BankCardListItemInfo getBankCardListItemInfo(Activity context,BankCardInfo bankCardInfo) {
		processDifferetType(context,bankCardInfo);
		processBizType(context,bankCardInfo);
		return this;
	}
	/**
	 * 处理不同的银行卡类型数据
	 * @param context
	 * @param bankCardInfo
	 */
	private void processDifferetType(Activity context,BankCardInfo bankCardInfo){
		String cardTypeString = bankCardInfo.getCardType();
	
		cardTppeStr = context.getString(BankCardInfo.CREDIT.equals(cardTypeString) ? 
				R.string.credit : R.string.debit);
		processDifferentCardType(bankCardInfo);
	}
	/**
	 * 处理不同的Ali卡类型数据
	 * @param bankCardInfo
	 */
	private void processDifferentCardType(BankCardInfo bankCardInfo){

		if (Utilz.equalsAtBit(bankCardInfo.getSourceChannel(), BankCardInfo.CARDSOURCE_EXPRESS) ||
				Utilz.equalsAtBit(bankCardInfo.getSourceChannel(), BankCardInfo.CARDSOURCE_KATONG)) {
			isShowIcon1Res = true;
		}
	}
	/**
	 * 处理卡执行的不同的业务类型
	 * @param context
	 * @param bankCardInfo
	 */
	private void processBizType(Activity context,BankCardInfo bankCardInfo){
		switch (bankCardInfo.getBizType()) {
		case BankCardInfo.BIZ_DEFAULT:
			bizTypeString = null;
			break;
		case BankCardInfo.BIZ_CREDITCARDFUND_NEW:
		case BankCardInfo.BIZ_CREDITCARDFUND_OLD:
			bizTypeString = context.getString(R.string.repayment);
			break;
		case BankCardInfo.BIZ_WIDTHDRAW_INSTANT:
		case BankCardInfo.BIZ_WIDTHDRAW_NORMAL:
			bizTypeString = context.getString(R.string.WithDrawal);
			break;
		default:
			break;
		}
	}
	/**
	 * 信用卡索引号规则
	 * 8位日期“YYYYMMDD”+”明文卡号前6位”+”明文卡号后4位”+”6位顺序号”。
	 * @return
	 */
	private String getCreditCardTailNumber(String mCardIndexNumber) {
		String mCreditCardTailNumber = null;
		if(!mCardIndexNumber.equals("")){
			if(mCardIndexNumber.length()>18){
				mCreditCardTailNumber = mCardIndexNumber.substring(14, 18);
			}else{
				mCreditCardTailNumber = "****";
			}
		}
		return mCreditCardTailNumber;
	}
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankTialNumber() {
		return bankTialNumber;
	}
	public void setBankTialNumber(String bankTialNumber) {
		this.bankTialNumber = bankTialNumber;
	}
	public String getCardTppeStr() {
		return cardTppeStr;
	}
	public void setCardTppeStr(String cardTppeStr) {
		this.cardTppeStr = cardTppeStr;
	}
	public String getBizTypeString() {
		return bizTypeString;
	}
	public void setBizTypeString(String bizTypeString) {
		this.bizTypeString = bizTypeString;
	}
	public boolean getIsShowIcon1Res() {
		return isShowIcon1Res;
	}
	public void setBankTypeIcon1ResId(Boolean bankTypeIcon1ResId) {
		this.isShowIcon1Res = bankTypeIcon1ResId;
	}
	
}
