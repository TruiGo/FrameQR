package com.alipay.android.client;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;


/**
 * 条码查询RESPONSE
 * @author gang.li
 *
 */
public class BarcodeTradeInfo implements Serializable {

    private int    tradeStatus=-1;

    private String tradeNo;

    private String otherAccount;

    private String otherName;
    
    private String otherUserName;

    private String otherAddress;

    private String otherPhone;

    private String amount;

    private int    needPaymentPassword = 1;

    private String rsaPK;

    private String rsaTS;
    
    public BarcodeTradeInfo()
    {
    	
    }
    
    public BarcodeTradeInfo(JSONObject jo) throws JSONException
    {
    	set(jo);
    }
    
    public void set(JSONObject jo) throws JSONException
   {
    	this.tradeStatus = jo.optInt( Constant.RPF_TRADESTATUS ) ;
    	this.tradeNo = jo.optString( "tradeNo" );
    	this.otherAccount = jo.optString( "otherAccount" );
    	this.otherAddress = jo.optString( "otherAddress" );
    	this.otherName = jo.optString( "otherName" );
    	this.otherUserName = jo.optString( "otherUserName" );
    	this.otherPhone = jo.optString( "otherPhone" );
//    	this.amount = jo.optString( Constant.FIELD_AMOUNT);
//    	this.needPaymentPassword = jo.optInt(Constant.RPF_BARCODE_NEED_PAY_PASSWORD);
    	this.rsaPK = jo.optString(Constant.RPF_RSA_PK);
    	this.rsaTS = jo.optString(Constant.RPF_RSA_TS);
    }

    public int getTradeStatus()
    {
        return tradeStatus;
    }

    public void setTradeStatus(int tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOtherAccount() {
        return otherAccount;
    }

    public void setOtherAccount(String otherAccount) {
        this.otherAccount = otherAccount;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }
    
    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherName) {
        this.otherUserName = otherUserName;
    }

    public String getOtherAddress() {
        return otherAddress;
    }

    public void setOtherAddress(String otherAddress) {
        this.otherAddress = otherAddress;
    }

    public String getOtherPhone() {
        return otherPhone;
    }

    public void setOtherPhone(String otherPhone) {
        this.otherPhone = otherPhone;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getNeedPaymentPassword() {
        return needPaymentPassword;
    }

    public void setNeedPaymentPassword(int needPaymentPassword) {
        this.needPaymentPassword = needPaymentPassword;
    }

    public String getRsaPK() {
        return rsaPK;
    }

    public void setRsaPK(String rsaPK) {
        this.rsaPK = rsaPK;
    }

    public String getRsaTS() {
        return rsaTS;
    }

    public void setRsaTS(String rsaTS) {
        this.rsaTS = rsaTS;
    }
 }