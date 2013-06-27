package com.alipay.android.client.baseFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.alipay.android.client.Main;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.eg.android.AlipayGphone.R;


public class AlipayDetailInfo
{
	/**
	 * 账单类型(卡宝新增)
	 * 1、缴费欠费单记录
	 * 2、交易列表  
	 */
	private int billType;
	public static final int BILL_TYPE_PUC = 1;
	public static final int BILL_TYPE_TRADE = 2;
	// 
	public int resultType=0;		// integer of the trade status.
	public int resultIsBuyer=0;
	public String resultStatus="";
	public String resultStatusMemo="";
	public String resultAct="";
	public String resultTradeTime="";
	public String resultGoodsName="";
	public String resultTradeNo="";
	public String resultTradeMoney="";
	 
	
	// 
	public int tPaid=0;
	public String tAct="";
	public String tYourName="";
	public String tTradeNumber="";
	public String tGoodsName="";
	public String tTradeMoney="";
	public String tTradeTime="";
	public String tTradeType="";
	public String tSellerName="";
	public String tSupportedByCoupon="";
	public String tCouponAmount="";
	public String tLogisticsType="";
	public int tLogisticsid=0;
	public String tBuyerAddress;
	public String tLogisticsFee="";
	public String tLogisticsName="";
	public String tLogisticsPhone="";
	public String tLogisticsMemo="";
	public String tLogisticsNo="";

	public String tPartnerName="";
	public String tSellerAccount="";
	public String tPayMoney="";
	public String tBalance="";
	public String tPoint="";
	public String tKatong ="";
	public String tOutTradeNO ="";
	public JSONArray tLogisticsStatus = null;
	public String tChannelState="";
	public boolean tMore=false;

	public String tBizType = "";
	
	public String tPeerPayId  = "";
	public String tPeerPayStatus  = "";
	public String tPeerPayStatusMemo  = "";
	public String tPeerPayAccount  = "";
	public String tPeerPayName  = "";
	public String tTranferStatus  = "";
	public String tSmsPush = "";
	private RootActivity context = null;
	
	public String lifePayCity = "";
	public String lifePayBillKey = "";
	public String lifePayChargeInst = "";
	public String lifePayFineAmount = "";
	public String lifePayGmtCreate = "";
	public String lifePayGmtModified = "";
	public String lifePayOriginalBillDate = "";
	public String lifePayOwnerName = "";
	public String lifePayProvince = "";
	public String lifePaySubBizType = "";
	public String lifePayUserId = "";

	
	public AlipayDetailInfo() {
		//
	}

	public void setDetailInfo(RootActivity activity,JSONObject itemObj) {
		context  = activity;
		this.tYourName=activity.getRealName();
		
		this.billType = itemObj.optInt(Constant.BILL_TYPE);
		
		this.resultIsBuyer = itemObj.optInt(Constant.RPF_BUYER);
		
		if(billType == 1)
		{
			this.resultStatus = itemObj.optString(Constant.STATUS);
			this.resultStatusMemo = itemObj.optString(Constant.RPF_STATUSMEMO);
			this.resultGoodsName = itemObj.optString(Constant.RPF_SHOWTITLE);
			this.resultTradeNo = itemObj.optString(Constant.RPF_ID);
			this.resultTradeMoney = itemObj.optString(Constant.RPF_BILLAMOUNT);
			
			this.tTradeNumber = itemObj.optString(Constant.RPF_ID);
			this.tGoodsName = itemObj.optString(Constant.RPF_SHOWTITLE);
			this.tTradeMoney = itemObj.optString(Constant.RPF_BILLAMOUNT);
			this.tTradeTime = itemObj.optString(Constant.RPF_BILLDATE);
			this.tSellerName = itemObj.optString(Constant.RPF_CHARGEINSTNAME);
			
			this.lifePayBillKey = itemObj.optString(Constant.BILLKEY);
			this.lifePayChargeInst = itemObj.optString(Constant.CHARGEINST);
			this.lifePayCity = itemObj.optString(Constant.CITY);
			this.lifePayFineAmount = itemObj.optString(Constant.FINEAMOUNT);
			this.lifePayGmtCreate = itemObj.optString(Constant.GMTCREATE);
			this.lifePayGmtModified = itemObj.optString(Constant.GMTMODIFIED);
			this.lifePayOriginalBillDate = itemObj.optString(Constant.ORIGINALBILLDATE);
			this.lifePayOwnerName = itemObj.optString(Constant.OWNERNAME);
			this.lifePayProvince = itemObj.optString(Constant.PROVINCE);
			this.lifePaySubBizType = itemObj.optString(Constant.SUBBIZTYPE);
			this.lifePayUserId = itemObj.optString(Constant.USERID);		
			this.resultTradeTime = itemObj.optString(Constant.RPF_BILLDATE);
		}
		else
		{
			this.resultStatus = itemObj.optString(Constant.RPF_TRADESTATUS);
			this.resultStatusMemo = itemObj.optString(Constant.RPF_TRADESTATUSMEMO);
			this.resultTradeTime = itemObj.optString(Constant.RPF_TRADETIME);
			this.resultGoodsName = itemObj.optString(Constant.RPF_GOODSNAME);
			this.resultTradeNo = itemObj.optString(Constant.RPF_TRADENO);
			this.resultTradeMoney = itemObj.optString(Constant.RPF_TRADEMONEY);
			
			this.tTradeNumber = itemObj.optString(Constant.RPF_TRADENO);
			this.tGoodsName = itemObj.optString(Constant.RPF_GOODSNAME);
			this.tTradeMoney = itemObj.optString(Constant.RPF_TRADEMONEY);
			this.tTradeTime = itemObj.optString(Constant.RPF_TRADETIME);
			this.tSellerName = itemObj.optString(Constant.RPF_SELLERNAME);
		}
		
		this.resultStatusMemo = TradeStatuMemoMaping.getMappedStatus(activity, resultStatus,resultStatusMemo);
		
		this.tTranferStatus = itemObj.optString(Constant.RPF_TRANFERSTATUS);
		
		this.resultAct = itemObj.optString(Constant.RPF_ACT);
		
		this.tBizType = itemObj.optString(Constant.RPF_BIZTYPE);
    	
		this.tPaid = itemObj.optInt(Constant.RPF_PAID);
		this.tAct = itemObj.optString(Constant.RPF_ACT);
		
		this.tTradeType = itemObj.optString(Constant.RPF_TRADETYPE);
		
		this.tLogisticsType = itemObj.optString(Constant.RPF_LOGISTICSTYPE);
		this.tLogisticsid = itemObj.optInt(Constant.RPF_LOGISTICSID);
		this.tBuyerAddress = itemObj.optString(Constant.RPF_BUYERADDRESS);
		this.tLogisticsFee = itemObj.optString(Constant.RPF_LOGISTICSFEE);
		this.tLogisticsName = itemObj.optString(Constant.RPF_LOGISTICSNAME);
		this.tLogisticsPhone = itemObj.optString(Constant.RPF_LOGISTICSPHONE);
		this.tLogisticsMemo = itemObj.optString(Constant.RPF_LOGISTICSMEMO);
		this.tLogisticsNo = itemObj.optString(Constant.RPF_LOGISTICSNO);
		
		this.tSupportedByCoupon = 	itemObj.optString(Constant.RPF_SUPPORTED_COUPON);
		this.tCouponAmount = 	itemObj.optString(Constant.RPF_COUPON_AMOUNT);
		this.tPartnerName = itemObj.optString(Constant.RPF_PARTNERNAME);
		this.tSellerAccount = itemObj.optString(Constant.RPF_SELLER_ACCOUNT);
		this.tPayMoney = itemObj.optString(Constant.RPF_PAYMONEY);
		this.tBalance = itemObj.optString(Constant.RPF_BALANCE);
		this.tPoint = itemObj.optString(Constant.RPF_POINT);
		this.tChannelState = itemObj.optString(Constant.RPF_CHANNELSTATE);
		this.tKatong = itemObj.optString(Constant.RPF_KATONGLIST);
		this.tOutTradeNO = itemObj.optString(Constant.RPF_OUTTRADENO);
		this.tLogisticsStatus = itemObj.optJSONArray(Constant.RPF_LOGISTICSSTATUS);
		
		this.tPeerPayId = itemObj.optString(Constant.RPF_PEERPAY_ID);
		this.tPeerPayStatus = itemObj.optString(Constant.RPF_PEERPAY_STATUS);
		this.tPeerPayStatusMemo = itemObj.optString(Constant.RPF_PEERPAY_STATUSMEMO);
		this.tPeerPayAccount = itemObj.optString(Constant.RPF_PEERPAY_ACCOUNT);
		this.tPeerPayName = itemObj.optString(Constant.RPF_PEERPAY_NAME);
		this.tSmsPush = itemObj.optString(Constant.RPF_SMSPUSH);
	}
	
	public int getBillType() {
		return billType;
	}
	public void setBillType(int billType) {
		this.billType = billType;
	}
	public void setResultType(int type) {
		this.resultType = type;
	}
	/**
	 * 设置状态颜色
	 * @param statusText
	 */
	public void setStatusColor(TextView statusText){
		String memo = resultStatusMemo;
		 if (memo != null) {
	            if ("等待卖家发货".equals(memo) || "等待对方付款".equals(memo) || "还款中".equals(memo) 
	            		|| context.getText(R.string.wait_to_receive).equals(memo) || context.getText(R.string.wait_bank_process).equals(memo)
	            		|| "等待确认收货".equals(memo) || "等待卖家收款".equals(memo) || "缴费中".equals(memo) || "等待买家确认收货".equals(memo)
	            		|| "等待卖家发货".equals(memo)) {
	            	statusText.setTextColor(context.getResources().getColor(R.color.TextColorBlue));
	            } else if ("等待付款".equals(memo) || "等待缴费".equals(memo)) {
	            	statusText.setTextColor(context.getResources().getColor(R.color.text_orange));
	            } else
	            	statusText.setTextColor(context.getResources().getColor(R.color.text_light_gray));
	        }
	        if (resultIsBuyer == 2) {
	            //代付人相关
	            if (resultStatus != null
	                && resultStatus.equals(context.getResources().getString(
	                    R.string.AgentDetailPeerPayInitResult))) {
	                //代付相关
	                memo = "等待代付" + "(" + resultStatusMemo + ")";
	                statusText.setTextColor(context.getResources().getColor(R.color.text_orange));
	            }
	        } else if (resultIsBuyer == 0) {
	            if (tPeerPayStatus != null && ! tPeerPayStatus.equals("")) {
	                //买家有代付相关状态
	                memo = resultStatusMemo + "(" + tPeerPayStatusMemo + ")";
	            }
	        }
	        if (memo != null) {
	            SpannableStringBuilder strStyle = BaseHelper.changeStringStyle(memo, "(", ")",
	                Color.GRAY, 22);
	            if (strStyle != null) {
	            	statusText.setText(strStyle);
	            } else {
	            	statusText.setText(memo);
	            }
	        }
	}
}  