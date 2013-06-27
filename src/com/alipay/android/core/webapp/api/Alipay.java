/**
 * 
 */
package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.safepay.SafePayHelper;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.webapp.AuthoActivity;
import com.alipay.android.common.data.UserData;
import com.alipay.android.safepay.MobileSecurePayer;
import com.alipay.android.safepay.SafePayResultChecker;

/**
 * @author sanping.li
 * 
 */
public class Alipay extends Plugin {
	
	public final static String Tag = "Alipay";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String strRet = (String)msg.obj;
			SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
			String status = resultChecker.getReturnStr("resultStatus");
			String memo = resultChecker.getReturnStr("memo");
			if(memo==null||memo.length()<=0){
			    memo = "null";
			}else{
			    memo = "'"+memo+"'"; 
			}
			if(status != null &&status.equals(MobileSecurePayer.SAFEPAY_STATE_OK)&&resultChecker.isPayOk()){
				sendJavascript("cordova.require('cordova/plugin/alipay').payCallBack('success',\"{status:'yes',memo:"+memo+"}\");");
            } else {
            	sendJavascript("cordova.require('cordova/plugin/alipay').payCallBack('error',\"{status:'no',memo:"+memo+"}\");cordova.require('cordova/plugin/context').startApp('09999985','');");
//                ((WebAppRunTime)ctx).setFrom(Main.TAB_INDEX_RECORDS_CONSUMPTION);
//                Intent intent = new Intent();
//                intent.setClass(mPage.getContext(), BillManagerRootControllerActivity.class);
//                mPage.getContext().startActivity(intent);
            }
            super.handleMessage(msg);
		};
	};
	
private Handler mCallSafepayServiceHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String strRet = (String) msg.obj;
			SafePayResultChecker resultChecker = new SafePayResultChecker(
					strRet);
			String status = resultChecker.getReturnStr("resultStatus");
			String memo = resultChecker.getReturnStr("memo");
			String result = resultChecker.getReturnStr("result");
			if(memo==null||memo.length()<=0){
			    memo = "null";
			}else{
			    memo = "'"+memo+"'"; 
			}
			if (result == null || result.length() <= 0) {
				result = "null";
			} else {
				result=Uri.encode(result);
				result = "'" + result + "'";
			}
			if (status != null
					&& status.equals(MobileSecurePayer.SAFEPAY_STATE_OK)) {
				sendJavascript("cordova.require('cordova/plugin/alipay').payCallBack('success',\"{status:'yes',memo:"
						+ memo + ",result:"
						+ result + "}\");");
			} else {
				sendJavascript("cordova.require('cordova/plugin/alipay').payCallBack('error',\"{status:'no',memo:"
						+ memo + ",result:"
						+ result + "}\");");
			}
			super.handleMessage(msg);
		};
	};
	@Override
	public PluginResult execute(String action, JSONArray args, String callbackId) {
		PluginResult.Status status = PluginResult.Status.OK;
		String result = "";

        AlipayApplication application = (AlipayApplication) mPage.getContext().getApplicationContext();
		if (action.equals("tradePay")) {
			//调起快捷支付需要刷新交易记录列表
	        application.setRecordsRefresh(true);
			
			String tradeNo, externToken, partnerID, bizType, bizSubType;
			try {
				tradeNo = args.getString(0);
				externToken = args.getString(1);
	            UserData userData = application.getUserData();
                externToken = (userData == null ? externToken : userData.getExtToken());
				partnerID = args.getString(2);
				bizType = args.getString(3);
				bizSubType = args.getString(4);
				
		        if (bizSubType != null && bizSubType!=""){
		        	bizSubType = bizSubType.toLowerCase();
		        }
				
				SafePayHelper.payDeal((RootActivity) (mPage.getContext()), mHandler, tradeNo,
						externToken, partnerID, bizType, bizSubType);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("login")) {
            Constant.GAS_JSON = null;
            Constant.WATER_JSON = null;
            Constant.ELECTRIC_JSON = null;
            Constant.COMMUN_JSON = null;

            if (application.getUserData() != null) {
                BaseHelper.exitProcessOnClickListener.clearUserCache(mPage.getContext());
                application.logoutUser();
            }
            
			Intent intent = new Intent((RootActivity) (mPage.getContext()), Login.class);
			mPage.getRunTime().startActivityForResult(mPage.getContext(),this,intent, Constant.REQUEST_LOGIN_BACK);
		} else if (action.equals("auth")) {
		    String partnerId;
		    String authLevel;
		    try {
                partnerId = args.getString(0);
                authLevel = args.getString(1);
                Intent intent = new Intent(mPage.getContext(), AuthoActivity.class);
                intent.putExtra(Constant.RQF_PARTNER_ID, partnerId);
                intent.putExtra(Constant.RQF_AUTH_LEVEL, authLevel);
                mPage.getRunTime().startActivityForResult(mPage.getContext(),this,intent, Constant.REQUEST_WEBAPP_AUTH);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (action.equals("callSafepayService")) {
			try {
				SafePayHelper.callSafepayService((RootActivity) (mPage.getContext()),
						mCallSafepayServiceHandler,args.getString(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new PluginResult(status, result);
	}

	@Override
	public boolean isSynch(String action) {
		if(action.equals("tradePay"));{
			return true;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == Constant.REQUEST_LOGIN_BACK && resultCode == Activity.RESULT_OK) {
			sendJavascript("cordova.require('cordova/plugin/alipay').callBack('success');");
            sendJavascript("cordova.require('cordova/channel').onLogin.fire();");
		}  else if(requestCode == Constant.REQUEST_LOGIN_BACK && resultCode == Activity.RESULT_CANCELED){
			sendJavascript("cordova.require('cordova/plugin/alipay').callBack('error');");
		} else if(requestCode == Constant.REQUEST_WEBAPP_AUTH && resultCode == Activity.RESULT_OK){
            sendJavascript("cordova.require('cordova/plugin/alipay').callBack('success');");
		} else if(requestCode == Constant.REQUEST_WEBAPP_AUTH && resultCode == Activity.RESULT_CANCELED){
            sendJavascript("cordova.require('cordova/plugin/alipay').callBack('error');");
        }
	}
	
	
}
