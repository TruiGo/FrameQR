package com.alipay.android.bizapp.CCR;
public class BankSMSInfo {
	
	public static final String[] supportSMSBank = {"ICBC","CMB","COMM","GDB","CITIC","CIB","BOC"};//7大需要监听短信的银行列表
	String mBankNumber="";
	String mBankSMS="";
	String mBankMark="";
	public BankSMSInfo(String num, String sms, String mark){
		mBankNumber=num;
		mBankSMS=sms;
		mBankMark=mark;
	}
	/**
	 * 某银行是否需要短信监听服务
	 * @param currentBankMark
	 * @return
	 */
	public static boolean currentBankIsNeedSmsServer(String currentBankMark){
		boolean isNeed = false;
		for(String bankMark : supportSMSBank){
			if(currentBankMark.equals(bankMark)){
				isNeed = true;
			}
		}
		return isNeed;
	}
}