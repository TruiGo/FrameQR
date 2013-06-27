package com.alipay.android.client;

import android.os.Handler;

public class PayData {
	RootActivity mActivity;
	Handler mHandler;
	String mTradeNO;
	String mExternToken;
	String mPartnerID;
	String mBizType;
	String mBizSubType;
	public void setData(RootActivity activity,Handler handler,final String tradeNO, final String externToken, final String partnerID,final String bizType,final String bizSubType)
	{
		mActivity = activity;
		mHandler = handler;
		mTradeNO = tradeNO;
		mExternToken = externToken;
		mPartnerID = partnerID;
		mBizType= bizType;
		mBizSubType = bizSubType;
	}
	public RootActivity getAc(){
		return mActivity;
	}
	public Handler getHd(){
		return mHandler;
	}
	public String getTn(){
		return mTradeNO;
	}
	public String getEt(){
		return mExternToken;
	}
	public String getPi(){
		return mPartnerID;
	}
	public String getBt(){
		return mBizType;
	}
	public String getBst(){		
		return mBizSubType;
	}
	public void clearAll()
	{
		mActivity = null;
		mHandler = null;
		mTradeNO = null;
		mExternToken = null;
		mPartnerID = null;
		mBizType= null;
		mBizSubType = null;
	}
}
