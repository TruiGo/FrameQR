package com.alipay.android.client.partner;

import java.net.URLDecoder;

import android.util.Base64;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.core.ParamString;

public class PartnerInfo {
	
	/**
	 * 来源ID
	 * 外部sourceId(订单有变更+modify)
	 * 例：MOBILE_CLIENT_51zhangdan  
	 *     MOBILE_CLIENT_51zhangdan_modify
	 */
	private String sourceId;
	
	/**
	 * 外部订单号
	 */
	private String outTradeNo;
	
	/**
	 * 还款完成时返回scheme
	 */
	private String returnUrl;
	
	/**
	 * 外部交易变更
	 * cardNO-卡号变更
	 * bankMark-银行变更
	 * holderName-持卡人姓名变更
	 * amount-还款金额变更
	 * 例：cardNo|bankMark
	 */
	private String outOrderChange;
	
	private String param;
	
	
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getOutOrderChange() {
		return outOrderChange;
	}
	public void setOutOrderChange(String outOrderChange) {
		this.outOrderChange = outOrderChange;
	}
	/**
	 * 反序列化
	 * @param serialization 例：sourceId=51zhangdan&outTradeNo=12523
	 * @return
	 */
	public PartnerInfo deserialization(String serialization){
		ParamString paramString = new ParamString(serialization);
		String currentParam = paramString.getValue(Constant.SOURCE_ID);
		if(currentParam != null){
			setSourceId(URLDecoder.decode(currentParam));
		}
		currentParam = paramString.getValue(Constant.OUT_TRADE_NO);
		if(currentParam != null){
			setOutTradeNo(URLDecoder.decode(currentParam));
		}
		currentParam = paramString.getValue(Constant.RETURN_URL);
		if(currentParam != null){
			setReturnUrl(new String(Base64.decode(URLDecoder.decode(currentParam),Base64.DEFAULT)));
		}
		return this;
	}
	
}
