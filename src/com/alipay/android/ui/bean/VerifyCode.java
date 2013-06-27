package com.alipay.android.ui.bean;


public class VerifyCode{
	private String verifyCode;
	private String barcodeInfo;
	private String verifyStatus;
	private String dynamicId;
	private String qrCodeStr;
    private String messageEncoding;
    private String format;
    private String message;

	
	public VerifyCode() {
	}
	
	/*public VerifyCode(String verifyCode, String verifyStatus, String dynamicId) {
		this.verifyCode = verifyCode;
		this.verifyStatus = verifyStatus;
		this.dynamicId = dynamicId;
	}*/
	
	public String getBarcodeInfo() {
		return barcodeInfo;
	}

	public String getMessageEncoding() {
		return messageEncoding;
	}

	public void setMessageEncoding(String messageEncoding) {
		this.messageEncoding = messageEncoding;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public String getVerifyStatus() {
		return verifyStatus;
	}
	public void setVerifyStatus(String verifyStatus) {
		this.verifyStatus = verifyStatus;
	}
	public String getDynamicId() {
		return dynamicId;
	}
	public void setDynamicId(String dynamicId) {
		this.dynamicId = dynamicId;
	}

	public String getQrCodeStr() {
		return qrCodeStr;
	}

	public void setQrCodeStr(String qrCodeStr) {
		this.qrCodeStr = qrCodeStr;
	}
}