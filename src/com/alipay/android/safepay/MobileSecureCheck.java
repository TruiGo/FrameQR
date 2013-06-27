/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.alipay.android.safepay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.util.LogUtil;

public class MobileSecureCheck
{
	static String TAG = "MobileSecureCheck";
	
	public static final String SAFEPAY_STATE_OK 		= "9000";

	Integer lock = 0;
	IAlixPay mAlixPay = null;
	boolean mbPaying = false;
	
	Activity mActivity = null;
	Context mContext = null;
//	private final String APP_NAME = "alipay";

	public final int PAY_OK = 1;
	public final int PAY_FAIL = 0;
	public final int PAY_OLD = 2;
	
	private Handler mHandler = null;
	private int mMsg = 0;
	
	private ServiceConnection mAlixPayConnection = new ServiceConnection() 
	{
		public void onServiceConnected(ComponentName className, IBinder service)
		{
		    //
		    // wake up the binder to continue.
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

	public boolean check(final String externToken, final String bizSubType, final Handler callback,
			final int myWhat, final Activity activity)
	{
		if( mbPaying )
			return false;
			
		mbPaying = true;
		mHandler = callback;
		mMsg = myWhat;
		//
		mActivity = activity;
		mContext = mActivity.getApplicationContext();
		
		// bind the service.
		if (mAlixPay == null)
		{
			mContext.bindService(new Intent(IAlixPay.class.getName()), mAlixPayConnection, Context.BIND_AUTO_CREATE);
		}
		
		//for the further extend
		final String strOrderInfo = getCheckInfo(bizSubType);
		
		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					// wait for the service bind operation to completely finished.
					// Note: this is important,otherwise the next mAlixPay.Pay() will fail.
					synchronized (lock)
					{
						if (mAlixPay == null)
							lock.wait();
					}

					// register a Callback for the service.
					mAlixPay.registerCallback(mCallback);
					
					// call the MobileSecurePay service.
					String strRet = mAlixPay.prePay(strOrderInfo);
					LogUtil.logOnlyDebuggable(TAG, "After Check: " + strRet);

					// set the flag to indicate that we have finished.
					// unregister the Callback, and unbind the service.
					mbPaying = false;
					mAlixPay.unregisterCallback(mCallback);
					mContext.unbindService(mAlixPayConnection);
					
					// send the result back to caller.
					Message msg = new Message();
					msg.what = myWhat;
					if (strOrderInfo == null) {	//获取token的bizSubType
						SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
						String status = resultChecker.getReturnStr("resultStatus");
						LogUtil.logOnlyDebuggable(TAG, "After Check: status=" + status);
						
						if (status != null && Utilz.valueOf(status).intValue() == 9000) {
							msg.arg1 = PAY_OK;
						} else {
							msg.arg1 = PAY_FAIL;
						}
					} else {	//获取认证list的bizSubType
						msg.arg1 = PAY_OK;
					}
					msg.obj = strRet;
					callback.sendMessage(msg);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					// send the result back to caller.
					Message msg = new Message();
					msg.what = myWhat;
					msg.obj = e.toString();
					callback.sendMessage(msg);
				}
			}
		}).start();
		
		return true;
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
			return true;
		}

		@Override
		public void payEnd(boolean arg0, String arg1) throws RemoteException {
			// TODO Auto-generated method stub
			
			Message msg = new Message();
			msg.what = mMsg;
			if(arg0){
				msg.arg1 = PAY_OK;
			}else{
				msg.arg1 = PAY_FAIL;
			}
			msg.obj = arg1;
			mHandler.sendMessage(msg);
		}
	};
	
	private String getCheckInfo(final String bizSubType){
		String strCheckInfo = "";
		
		if (bizSubType.equals(Constant.SAFE_PAY_CHECK)) {
			//
		} else if (bizSubType.equals(Constant.SAFE_GET_AUTHLIST)) {
			strCheckInfo = Constant.SAFE_ORDER_GET_AUTHLIST;
		} else {
			//
		}
		
		return strCheckInfo;
	}
}
