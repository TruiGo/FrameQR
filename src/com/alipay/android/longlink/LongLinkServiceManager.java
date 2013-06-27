package com.alipay.android.longlink;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.safepay.SafePayResultChecker;
import com.alipay.android.ui.voucher.VerifyResultNotifier;
import com.alipay.android.util.LogUtil;


public class LongLinkServiceManager {
	private static final String TAG = "LongLinkServiceManager";
	private static LongLinkServiceManager instance;
	private RootActivity mContext;
	private Handler mHandler;
	private VerifyResultNotifier verifyResultNotifier;
	private String curTradeNo;
	
	private PayResultHanlder mPayResultHanlder = new DefaultPayResultHanlder();// 
	
	class DefaultPayResultHanlder implements PayResultHanlder {//默认什么都不处理
		@Override
		public void paySuccess(String tradeNo) {
			LogUtil.logOnlyDebuggable(TAG, "==============>mPayResultHanlder paySuccess");
		}
		
		@Override
		public void payFail(String tradeNo) {
			LogUtil.logOnlyDebuggable(TAG, "==============>mPayResultHanlder payFail");
		}
		
		@Override
		public void onCancel(String tradeNo) {
			LogUtil.logOnlyDebuggable(TAG, "==============>mPayResultHanlder onCancel");
		}
	};
	
	/**
	 * 需要设置Context  防止安全支付界面不可见
	 * @param context
	 */
	public void setContext(RootActivity context){
		this.mContext = context;
	}
	
	/**
	 * 长连接调起安全支付后默认的处理
	 */
	class TradeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int ret = msg.arg1;
			String strRet = (String) msg.obj;
			SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
			if (ret == 1) {
				if (strRet != null && strRet.contains("resultStatus")) {
					String status = resultChecker.getReturnStr(Constant.SAFE_PAY_RESULT_STATUS);
					if (status != null && status.equals("9000")) {
						mPayResultHanlder.paySuccess(curTradeNo);
					}
				}
			} else {
				if (strRet != null && strRet.contains("resultStatus")) {
					String status = resultChecker.getReturnStr(Constant.SAFE_PAY_RESULT_STATUS);
					if (status != null && status.equals("6001")) { // 用户中途取消了此次支付
						mPayResultHanlder.onCancel(curTradeNo);
					} else { // 其他快捷支付错误
						mPayResultHanlder.payFail(curTradeNo);
					}
				}else if(strRet != null){//网络异常返回的只有字符串............
					mPayResultHanlder.payFail(curTradeNo);
				}
			}
			super.handleMessage(msg);
		}
	}
	
	/**
	 * 默认的Callback回调处理器,目前只统一处理安全支付
	 */
	private IVerifyClientCallback mDefaultCallback = new IVerifyClientCallback.Stub() {
		@Override
		public void processPacket(Bundle bundle) throws RemoteException {
			if(bundle != null){
				String dealJSON = bundle.getString("appdata");
				JSONObject jsonResponse = null;
				String curTradeAct = null;
				try {
					jsonResponse = new JSONObject(dealJSON);
					if (jsonResponse != null) {
						curTradeAct = jsonResponse.optString(Constant.RPF_PUSH_TRADE_ACTION);
//						((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(Constant.DEFAULT_VIBRATE_PATTERN, -1);
						if(curTradeAct != null){
							if (curTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_PAY)) {
								((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(Constant.DEFAULT_VIBRATE_PATTERN, -1);
								curTradeNo = jsonResponse.optString(Constant.RPF_TRADENO);
								if (curTradeNo != null && !curTradeNo.equals("")) {
									//  得到交易数据，调用快捷支付接口付款
									BaseHelper.payDeal(mContext, mHandler, null, curTradeNo, mContext.getExtToken(), "", "trade", Constant.SAFE_PAY_PAY);
								}
							}else if(curTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_VERIFY)){//verify
								//将结果分发给每个Callback
								((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(Constant.DEFAULT_VIBRATE_PATTERN, -1);
								if(verifyResultNotifier != null){
//									"memo":"success/fail","action":" verify","tradeNO":""
									String memo = jsonResponse.optString(Constant.RPF_MEMO);
									if("success".equalsIgnoreCase(memo)){
										verifyResultNotifier.onSuccess(jsonResponse);
									}else if("fail".equalsIgnoreCase(memo)){
										verifyResultNotifier.onFail(jsonResponse);
									}
								}
							}else if(curTradeAct.equalsIgnoreCase(Constant.RPF_PUSH_TRADE_ACT_FASTPAY) 
									 || curTradeAct.equalsIgnoreCase(Constant.RPF_PUSH_TRADE_ACT_GETGOODSLIST)){
								//处理快付的action。
								String memo = jsonResponse.optString(Constant.RPF_MEMO);
//								if("success".equalsIgnoreCase(memo)){
									verifyResultNotifier.onSuccess(jsonResponse);
//								}else if("fail".equalsIgnoreCase(memo)){
//									verifyResultNotifier.onFail(jsonResponse);
//								}											
							}else if(curTradeAct.equalsIgnoreCase(Constant.RPF_PUSH_TRADE_ACT_SOUNDWAVEPAYPUSH)){
								  verifyResultNotifier.onSuccess(jsonResponse);
							}																				
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.logOnlyDebuggable(TAG, "==================> process info fail");
				}
			}
		}
	};
	
	private LongLinkServiceManager(RootActivity context){
		this.mContext = context;
		mHandler = new TradeHandler();
	}
	
	private void setPayResultHadler(PayResultHanlder payResultHanlder){
		if(payResultHanlder != null)
			this.mPayResultHanlder = payResultHanlder;
	}
	
	/**
	 * 获取实例
	 * @param context
	 * @param payResultHanlder 安全支付调用处理回调，可以为null
	 */
	public static synchronized LongLinkServiceManager getInstance(RootActivity context,PayResultHanlder payResultHanlder){
		if(instance == null){
			instance = new LongLinkServiceManager(context);
			instance.setPayResultHadler(payResultHanlder);//设置处理器
		}
		
		return instance;
	}
	
	/**
	 * @param callback 用户自定义Callback，每个界面可以有自己的callback
	 */
	public void bindService(VerifyResultNotifier callback,PayResultHanlder resultHandler){
		if(mService == null){
			//建立长连接
			mContext.getApplicationContext().bindService(new Intent(mContext,VerifyLinkService.class), mServiceConn, Context.BIND_AUTO_CREATE);
			registUserCallback(callback,resultHandler);
		}else{
			reConnect();
		}
	}
	
	/**
	 * @param callback 注册用户Callback
	 */
	public void registUserCallback(VerifyResultNotifier callback,PayResultHanlder resultHandler){
		verifyResultNotifier = callback;
//		mPayResultHanlder = (resultHandler == null) ? mPayResultHanlder : resultHandler;
		if(resultHandler != null )
			mPayResultHanlder = resultHandler;
	}
	
	/**
	 * @param callback 移除用户界面的Callback
	 */
	public void unRegisterUserCallback(){
		verifyResultNotifier = null;
	}
	
	public void unBindService(){
		if(mService != null){
			try {
				mService.closePushLink();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		//断开长连接
	    if (mIsServiceBound){
			mContext.getApplicationContext().unbindService(mServiceConn);
			mIsServiceBound = false;
	    }
	    mContext.getApplicationContext().stopService(new Intent(mContext, VerifyLinkService.class));
	    mService = null;
	}
	
	private boolean mIsServiceBound;
	private IVerifyLinkService mService;
	private ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsServiceBound = false;
			LogUtil.logOnlyDebuggable("LongLinkService", "onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IVerifyLinkService.Stub.asInterface(service);
			if(mService != null){//长连接建立后注册默认的回调
				try {
					mService.registCallback(mDefaultCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
            mIsServiceBound = true;
            LogUtil.logOnlyDebuggable("LongLinkService", "onServiceConnected");
		}
	};
	
	public void reConnect(){
		if(mService != null){
			try {
				mService.reConnect();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isSocketConnected(){
		if(mService != null){
			try {
				return mService.isSocketConnected();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isBindService(){
		if(mService != null){
			return true;
		}else{
		   return false;
		}
	}
	
	public void setSocketListener(ISocketResultNotifer socketResultNotifer) {
		if(mService != null){
			try {
				mService.registSocketNotifer(socketResultNotifer);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void unregistSocketListener() {
		if(mService != null){
			try {
				mService.unRegistSocketNofiter();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
