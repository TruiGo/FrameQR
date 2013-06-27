package com.alipay.android.ui.bean;

public class VerifyRecord {
	private String verifyNo;//核销流水号
	private String verifyAddress;//核销网点
	private String verifyCode;//核销码
	private Integer verifyCount;//核销份数
	private String verifyAmount;//核销额
	private String verifyStatus;//核销状态
	private String operator_id;//操作员ID
	private String tradeNo;//交易号
	private String verifyTime;//交易号
	private String[] address;

	public String getVerifyNo() {
		return verifyNo;
	}

	public void setVerifyNo(String verifyNo) {
		this.verifyNo = verifyNo;
	}

	public String getVerifyAddress() {
		return verifyAddress;
	}

	public void setVerifyAddress(String verifyAddress) {
		this.verifyAddress = verifyAddress;
		if(verifyAddress != null)
			address = verifyAddress.split(","); 
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public void setVerifyCount(Integer verifyCount) {
		this.verifyCount = verifyCount;
	}

	public Integer getVerifyCount() {
		return verifyCount;
	}
	public String getVerifyAmount() {
		return verifyAmount;
	}

	public void setVerifyAmount(String verifyAmount) {
		this.verifyAmount = verifyAmount;
	}

	public String getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(String verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(String verifyTime) {
		this.verifyTime = verifyTime;
	}
	
	public String getAddress(){
		if(address != null && address.length >= 3){
			return address[1];
		}
		return "";
	}
	
	public String getPhone(){
		if(address != null && address.length >= 3){
			return address[2];
		}
		return "";
	}
	
	public String getStoreName(){
		if(address != null && address.length >= 3){
			return address[0];
		}
		return "";
	}
}
