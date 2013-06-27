package com.alipay.android.client;

import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;


public class AlipayAgentPayDetail
{
	// 
	public int resultType=0;
	
	public String tYourName="";

	public String tBuyerName="";
	public String tBuyerAccount="";
	public String tBuyerApplyMemo="";
	
	public String tSellerName="";
	public String tSellerAccount="";

	public String tGoodsName="";
	public String tPayMoney="";
	public String tTradeNo="";
	
	public String tTradeTime="";
	
	public String tTradeType="";
	public String tTradeStatus="";
	public String tTradeStatusMemo="";
	
	public String tPeerPayId="";
	public String tPeerPayStatus="";
	public String teerPayStatusMemo="";
	
	public AlipayAgentPayDetail() {
		//
	}

	public void setDetailInfo(RootActivity activity,JSONObject itemObj) {
		// TODO Auto-generated method stub
		this.tYourName=activity.getRealName();
		
		this.tBuyerName = itemObj.optString(Constant.RPF_BUYER_NAME);
		this.tBuyerAccount = itemObj.optString(Constant.RPF_BUYER_ACCOUNT);
		this.tBuyerApplyMemo = itemObj.optString(Constant.RPF_APPLY_MESSAGE);
		
		this.tSellerName = itemObj.optString(Constant.RPF_SELLER_NAME);
		this.tSellerAccount = itemObj.optString(Constant.RPF_SELLER_ACCOUNT);
		
		this.tGoodsName = itemObj.optString(Constant.RPF_GOODSNAME);
		this.tPayMoney = itemObj.optString(Constant.RPF_PAYMONEY);
		
		this.tTradeNo = itemObj.optString(Constant.RPF_TRADENO);
		this.tTradeTime = itemObj.optString(Constant.RPF_TRADETIME);
		this.tTradeType = itemObj.optString(Constant.RPF_TRADETYPE);
		
		this.tTradeStatus = itemObj.optString(Constant.RPF_TRADESTATUS);
		this.tTradeStatusMemo = itemObj.optString(Constant.RPF_TRADESTATUSMEMO);
		
		this.tPeerPayId = itemObj.optString(Constant.RPF_PEERPAY_ID);
		this.tPeerPayStatus = itemObj.optString(Constant.RPF_PEERPAY_STATUS);
		this.teerPayStatusMemo = itemObj.optString(Constant.RPF_PEERPAY_STATUSMEMO);
	}
	
	public void setResultType(int type) {
		this.resultType = type;
	}
}  