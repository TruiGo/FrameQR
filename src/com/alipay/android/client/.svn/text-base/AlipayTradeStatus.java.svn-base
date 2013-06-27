package com.alipay.android.client;

import android.content.Context;

import com.alipay.android.client.constant.Constant;
import com.eg.android.AlipayGphone.R;

public class AlipayTradeStatus
{
	//交易状态
	public static final int WAIT_BUYER_PAY=0;
	public static final int WAIT_SELLER_CONFIRM_TRADE=1;
	public static final int WAIT_SYS_CONFIRM_PAY=2;
	public static final int WAIT_SELLER_SEND_GOODS=3;
	public static final int WAIT_BUYER_CONFIRM_GOODS=4;
	public static final int WAIT_SYS_PAY_SELLER=5;
	public static final int TRADE_FINISHED=6;
	public static final int TRADE_CLOSED=7;
	public static final int TRADE_REFUSE=8;
	public static final int TRADE_REFUSE_DEALING=9;
	public static final int TRADE_CANCEL=10;
	public static final int TRADE_PENDING=11;
	public static final int TRADE_SUCCESS=12;
	public static final int BUYER_PRE_AUTH=13;
	public static final int COD_WAIT_SELLER_SEND_GOODS=14;
	public static final int COD_WAIT_BUYER_PAY=15;
	public static final int COD_WAIT_SYS_PAY_SELLER=16;
	//代付相关状态
	public static final int PEERPAY_INIT=17;
	public static final int PEERPAY_CANCEL=18;
	public static final int PEERPAY_REFUSE=19;
	public static final int PEERPAY_ABORT=20;
	public static final int PEERPAY_FAIL=21;
	public static final int PEERPAY_SUCCESS=22;
	
	private Context mContext;
	
	public AlipayTradeStatus(Context context) 
	{
		mContext = context;
	}

	public int getMapStatus(String tradeStatus) {
		int resultType = -1;
		if (tradeStatus == null || tradeStatus.equals(""))
			return resultType;
		
		if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryWaitPayingResult)) 
				|| Constant.STATUS_INIT.equals(tradeStatus)
				|| tradeStatus.equals("I")){
    		resultType = WAIT_BUYER_PAY;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryWaitSellerConfirmTradResult))){
    		resultType = WAIT_SELLER_CONFIRM_TRADE;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryWaitSysConfirmPayResult))){
    		resultType = WAIT_SYS_CONFIRM_PAY;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryWaitSellerSendGoodsResult))){
    		resultType = WAIT_SELLER_SEND_GOODS;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryWaitBuyerConfirmGoodsResult))){
    		resultType = WAIT_BUYER_CONFIRM_GOODS;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryWaitSysPaySellerResult))){
    		resultType = WAIT_SYS_PAY_SELLER;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradeFinishedResult))){
    		resultType = TRADE_FINISHED;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradeClosedResult))){
    		resultType = TRADE_CLOSED;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradeRefuseResult))){
    		resultType = TRADE_REFUSE;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradeRefuseDealingResult))){
    		resultType = TRADE_REFUSE_DEALING;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradeCancelResult))){
    		resultType = TRADE_CANCEL;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradePendingResult))){
    		resultType = TRADE_PENDING;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryTradeSuccessResult))){
    		resultType = TRADE_SUCCESS;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryBuyerPreAuthResult))){
    		resultType = BUYER_PRE_AUTH;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryCODWaitSellerSendGoodsResult))){
    		resultType = COD_WAIT_SELLER_SEND_GOODS;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryCODWaitBuyerPayResult))){
    		resultType = COD_WAIT_BUYER_PAY;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.DealQueryCODWaitSysPaySellerResult))){
    		resultType = COD_WAIT_SYS_PAY_SELLER;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.AgentDetailPeerPayInitResult))){
    		resultType = PEERPAY_INIT;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.AgentDetailPeerPayCancelResult))){
    		resultType = PEERPAY_CANCEL;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.AgentDetailPeerPayRefuseResult))){
    		resultType = PEERPAY_REFUSE;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.AgentDetailPeerPayAbortResult))){
    		resultType = PEERPAY_ABORT;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.AgentDetailPeerPayFailResult))){
    		resultType = PEERPAY_FAIL;
    	}else if (tradeStatus.equals(mContext.getResources().getString(R.string.AgentDetailPeerPaySuccessResult))){
    		resultType = PEERPAY_SUCCESS;
    	}
		
		return resultType;
	}
	
}