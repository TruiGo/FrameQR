package com.alipay.android.ui.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.alipay.android.client.constant.Constant;
import com.eg.android.AlipayGphone.R;

public class WithdrawBankInfo {
	/**
	 * 银行ID
	 */
	private String bankId;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 银行缩写
	 */
	private String bankMark;
	/**
	 * 提现额度
	 * 卡通：xxx.00元/天
	 * 绑定卡：49999.00/次
	 */
	private String cardWithdrawAmount;
	/**
	 * 银行图标
	 */
	private String bankIcon;
	/**
	 * 银行描述
	 */
	private String desc;
	/**
	 * 银行账号类型
	 */
	private String type;
	/**
	 * 到账时间
	 */
	private String arriveDate;
	/**
	 * 单笔或单日限额描述
	 */
	private String newDesc;
	/**
	 * 银行卡类型——卡通
	 */
	public static String TYPE_KATONG = "katong";
	
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankMark() {
		return bankMark;
	}
	public void setBankMark(String bankMark) {
		this.bankMark = bankMark;
	}
	public String getCardWithdrawAmount() {
		return cardWithdrawAmount;
	}
	public void setCardWithdrawAmount(String cardWithdrawAmount) {
		this.cardWithdrawAmount = cardWithdrawAmount;
	}
	public String getBankIcon() {
		return bankIcon;
	}
	public void setBankIcon(String bankIcon) {
		this.bankIcon = bankIcon;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getArriveDate() {
		return arriveDate;
	}
	public void setArriveDate(String arriveDate) {
		this.arriveDate = arriveDate;
	}
	public String getNewDesc() {
		return newDesc;
	}
	public void setNewDesc(String newDesc) {
		this.newDesc = newDesc;
	}
	
	public WithdrawBankInfo parserJsonBankInfo(Context context,JSONObject currentBankItem){
		try {
			if(currentBankItem.has(Constant.BANK_ID)){
				this.setBankId(currentBankItem.getString(Constant.BANK_ID));
			}
			if(currentBankItem.has(Constant.BANK_NAME)){
				this.setBankName(currentBankItem.getString(Constant.BANK_NAME));
			}
			if(currentBankItem.has(Constant.BANK_MARK)){
				this.setBankMark(currentBankItem.getString(Constant.BANK_MARK));
			}
			if(currentBankItem.has(Constant.CARD_WITHDRAW_AMOUNT)){
				this.setCardWithdrawAmount(currentBankItem.getString(Constant.CARD_WITHDRAW_AMOUNT));
			}
			if(currentBankItem.has(Constant.NEW_DESC)){
				this.setNewDesc(currentBankItem.getString(Constant.NEW_DESC));
			}
			if(currentBankItem.has(Constant.TYPE)){
				this.setType(currentBankItem.getString(Constant.TYPE));
			}
			if(currentBankItem.has(Constant.ARRIVE_DATE)){
				this.setArriveDate(currentBankItem.getString(Constant.ARRIVE_DATE));
			}
//			this.setDesc(currentBankItem.getString(Constant.DESC));
			String bankName = "";
			String lastNumber = "";
			if(this.getBankId().length()>4){
				lastNumber = this.getBankId().substring(this.getBankId().length()-4);
			}
			if(this.getType().equalsIgnoreCase(TYPE_KATONG)){
				bankName = this.getBankName()+"("+context.getString(R.string.LastNumber)+lastNumber+")"+"卡通";
				
			}else{
				bankName = this.getBankName()+"("+context.getString(R.string.LastNumber)+lastNumber+")";
			}
			this.setBankName(bankName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}
}
