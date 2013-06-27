/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.alipay.android.util.msp;

import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.alipay.android.app.IAliPay;
import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserData;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.safepay.SafePayResultChecker;
import com.alipay.android.util.LogUtil;

public class BizSecurePayer
{
	static String TAG = "MobileSecurePayer";
	
	public static final String SAFEPAY_STATE_OK 		= "9000";

	Integer lock = 0;
	IAliPay mAliPay = null;
	IAlixPay mAlixPay = null;
	boolean mbPaying = false;
	
	Activity mActivity = null;
	Context mContext = null;
	public final static int PAY_OK = 1;
	public final static int PAY_FAIL = 0;
	public final static int PAY_OLD = 2;
	
	private ServiceConnection mAlixPayConnection = new ServiceConnection() 
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
		    synchronized( lock )
		    {	
		    	mAlixPay 	= IAlixPay.Stub.asInterface(service);
		    	lock.notify();
		    }
		}
	
		public void onServiceDisconnected(ComponentName className)
		{
			mAlixPay	= null;
		}
	};
	
	public JSONObject pay(final String tradeNO, final String externToken, final String partnerID,final String bizType, final String bizSubType, final RootActivity activity)
	{
		if( mbPaying )
			return null;
		mbPaying = true;
		
		mActivity = activity;
		mContext = mActivity.getApplicationContext();
		
		// bind the service.
		if(mAlixPay == null)
		{
			mContext.bindService(new Intent(IAlixPay.class.getName()), mAlixPayConnection, Context.BIND_AUTO_CREATE);
		}
		
		//else ok.
		String strOrderInfo = getPayInfo(tradeNO, externToken, partnerID, bizType, bizSubType);
		JSONObject responseJson = new JSONObject();
		String status = null;
		try
		{
			responseJson.put(Constant.RQ_PAY, AlipayHandlerMessageIDs.RQF_PAY);
			// wait for the service bind operation to completely finished.
			// Note: this is important,otherwise the next mAlixPay.Pay() will fail.
			synchronized (lock)
			{
				if (mAlixPay == null)
					lock.wait();
			}

			Constant.mSafePayIsRunning = true;
			// register a Callback for the service.
			mAlixPay.registerCallback(mCallback);
			
			// call the MobileSecurePay service.
			String strRet = mAlixPay.Pay(strOrderInfo);
			LogUtil.logOnlyDebuggable(TAG, "After Pay: " + strRet);

			// set the flag to indicate that we have finished.
			// unregister the Callback, and unbind the service.
			mbPaying = false;
			mAlixPay.unregisterCallback(mCallback);
			mContext.unbindService(mAlixPayConnection);
			
			SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
			status = resultChecker.getReturnStr("resultStatus");
			LogUtil.logOnlyDebuggable(TAG, "After Pay: status=" + status);
			
			responseJson.put(Constant.SAFE_PAY_RESULT_STATUS, status);
			// send the result back to caller.
			if (status != null && Integer.valueOf(status).intValue() == 9000 && resultChecker.isPayOk()) {
				responseJson.put(Constant.SAFE_PAY_RESULT_ARRAY, PAY_OK);
			} else {
				responseJson.put(Constant.SAFE_PAY_RESULT_ARRAY, PAY_OLD);
			}
			responseJson.put(Constant.SAFE_PAY_RESULT_MEMO, resultChecker.getReturnStr("memo"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// send the result back to caller.
			try {
				responseJson.put(Constant.SAFE_PAY_RESULT_STATUS, status);
				responseJson.put(Constant.SAFE_PAY_RESULT_MEMO, e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}finally{
			if(!"barcode_prepay".equals(bizSubType)){
				logBizResult(status,activity);
			}
			
			//更新用户信息
            if(status != null && !status.equals("") &&Integer.valueOf(status).intValue() == 9000 ){
                AlipayApplication application = (AlipayApplication) mActivity.getApplicationContext();
                UserData userData = application.getUserData();
                if(userData!=null){
                    Observer observer = null;
                    if(mActivity instanceof Observer)
                        observer = (Observer) mActivity;
                    userData.resetStatus();
                    userData.requestData(observer, application.getDataHelper());
                }
            }
		}
		Constant.mSafePayIsRunning = false;
		return responseJson;
	}
	
	public JSONObject pay(final String strOrderInfo,final RootActivity activity)
	{
		if( mbPaying )
			return null;
		mbPaying = true;
		mActivity = activity;
		
		// bind the service.
		if (mAlixPay == null)
		{
			mActivity.bindService(new Intent(IAlixPay.class.getName()), mAlixPayConnection, Context.BIND_AUTO_CREATE);
		}
		
		String status = null;
		JSONObject responseJson = new JSONObject();
		try
		{
			//标记是调起快捷支付的结果
			responseJson.put(Constant.RQ_PAY, AlipayHandlerMessageIDs.RQF_PAY);
			// wait for the service bind operation to completely finished.
			// Note: this is important,otherwise the next mAlixPay.Pay() will fail.
			synchronized (lock)
			{
				if (mAlixPay == null)
					lock.wait();
			}
			
			Constant.mSafePayIsRunning = true;
			// register a Callback for the service.
			mAlixPay.registerCallback(mCallback);
			
			// call the MobileSecurePay service.
			String strRet = mAlixPay.Pay(strOrderInfo);
			LogUtil.logOnlyDebuggable(TAG, "After Pay: " + strRet);
			
			//得到支付结果
			SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
			status = resultChecker.getReturnStr("resultStatus");

			// set the flag to indicate that we have finished.
			// unregister the Callback, and unbind the service.
			mbPaying = false;
			mAlixPay.unregisterCallback(mCallback);
			mActivity.unbindService(mAlixPayConnection);
			
			responseJson.put(Constant.SAFE_PAY_RESULT_STATUS, status);
			// send the result back to caller.
			if (status != null && Integer.valueOf(status).intValue() == 9000 && resultChecker.isPayOk()) {
				responseJson.put(Constant.SAFE_PAY_RESULT_ARRAY, PAY_OK);
			} else {
				responseJson.put(Constant.SAFE_PAY_RESULT_ARRAY, PAY_OLD);
			}
			responseJson.put(Constant.SAFE_PAY_RESULT_MEMO, resultChecker.getReturnStr("memo"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try {
				responseJson.put(Constant.SAFE_PAY_RESULT_STATUS, status);
				responseJson.put(Constant.SAFE_PAY_RESULT_MEMO, e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}finally{
			logBizResult(status,activity);
			//更新用户信息
            if(status != null && !status.equals("") &&Integer.valueOf(status).intValue() == 9000 ){
                AlipayApplication application = (AlipayApplication) mActivity.getApplicationContext();
                UserData userData = application.getUserData();
                if(userData!=null){
                    Observer observer = null;
                    if(mActivity instanceof Observer)
                        observer = (Observer) mActivity;
                    userData.resetStatus();
                    userData.requestData(observer, application.getDataHelper());
                }
            }
		}
		
		Constant.mSafePayIsRunning = false;
		return responseJson;
	}
	
	private void logBizResult(String status,RootActivity activity) {
		/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
		AlipayLogAgent.onEvent(mContext, 
				Constants.MONITORPOINT_EVENT_BIZRESULT, 
				status != null && Integer.valueOf(status).intValue() == 9000 ? "Y" : "N", 
						storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID), 
						storageStateInfo.getValue(Constants.STORAGE_APPID), 
						storageStateInfo.getValue(Constants.STORAGE_APPVERSION), 
						storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
						activity.getUserId(), 
						storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
						storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
	}
	
	 /**
	 * This implementation is used to receive callbacks from the remote
	 * service.
	 */
	private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() 
	{
		/**
		 * This is called by the remote service regularly to tell us
		 * about new values. Note that IPC calls are dispatched through
		 * a thread pool running in each process, so the code executing
		 * here will NOT be running in our main thread like most other
		 * things -- so, to update the UI, we need to use a Handler to
		 * hop over there.
		 */
		@Override public void startActivity(String packageName, String className, int iCallingPid, Bundle bundle)
				throws RemoteException
		{
			Intent intent	= new Intent(Intent.ACTION_MAIN, null);
			
			if( bundle == null )
				bundle = new Bundle();
			// else ok.
			
			try
			{
				bundle.putInt("CallingPid", iCallingPid);
				intent.putExtras(bundle);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			intent.setClassName(packageName, className);
			mActivity.startActivity(intent);
		}

		@Override
		public boolean isHideLoadingScreen() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void payEnd(boolean arg0, String arg1) throws RemoteException {
		}
	};
	
	private String getPayInfo(final String tradeNO, final String externToken, final String partnerID,final String bizType,final String bizSubType){
		String strOrderInfo = "";
		
		//条码支付，进行快捷支付激活
		if (bizSubType.equals("barcode_prepay")) {
			strOrderInfo		+= "biz_sub_type=" + "\"" + bizSubType + "\"";
			
			strOrderInfo 		+= "&";
			strOrderInfo		+= "extern_token=" + "\"" + externToken + "\"";
			
			strOrderInfo 		+= "&";
			strOrderInfo		+= "app_name=" + "\"" + "alipay" + "\"";
			
			strOrderInfo 		+= "&";
			strOrderInfo		+= "biz_type=" + "\"" + bizType + "\"";
			
			strOrderInfo 		+= "&";
			strOrderInfo 		+= "trade_no=" + "\"" + tradeNO + "\"";
		} else {
			if(tradeNO != null && !tradeNO.equals(""))	//交易数据
			{
				if (bizType != null && bizType.equals("peer_pay")) {
					//代付人付款交易
					strOrderInfo 		+= "biz_no=" + "\"" + tradeNO + "\"";
				} else {
					// trade
					strOrderInfo 		+= "trade_no=" + "\"" + tradeNO + "\"";
				}
				strOrderInfo 		+= "&";
			}
			
			strOrderInfo		+= "app_name=" + "\"" + "alipay" + "\"";
			strOrderInfo 		+= "&";
			
			strOrderInfo		+= "extern_token=" + "\"" + externToken + "\"";
			strOrderInfo 		+= "&";
				
			strOrderInfo		+= "partner=" + "\"" + partnerID + "\"";
			strOrderInfo 		+= "&";
			
			strOrderInfo		+= "biz_type=" + "\"" + bizType + "\"";
			strOrderInfo 		+= "&";
			
			strOrderInfo		+= "biz_sub_type=" + "\"" + bizSubType + "\"";
		}
		
		return strOrderInfo;
	}
	
	public String getPayInfo(String externToken, String bizType){
	    String strOrderInfo = "";
	    if(externToken!=null){
            strOrderInfo        += "extern_token=" + "\"" + externToken + "\"";
            strOrderInfo        += "&";
	    }
        strOrderInfo        += "app_name=" + "\"" + "alipay" + "\"";
        
        strOrderInfo        += "&";
        strOrderInfo        += "biz_type=" + "\"" + bizType + "\"";
        
        return strOrderInfo;
	}
}

