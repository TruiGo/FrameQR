package com.alipay.android.bizapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.client.AlipayDataCacheHelper;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.net.http.RespData;
import com.alipay.android.safepay.MobileSecureCheck;
import com.alipay.android.safepay.MobileSecurePayHelper;
import com.alipay.android.util.LogUtil;

public abstract class BaseBiz {
	protected static final String TAG = "BaseBiz";
	protected BizCallBack mBizCallback;
    public APHttpClient mHttpClient;
	private Context mContext = null;
    
    /*
     * 对外的联网接口，由子类实�?
     */
    public abstract void sendHttpRequest(BizCallBack bizCallback, String requestType, final HashMap<String, String> RequestData, int messageId);
    /**
     * 对外联网接口，使用get方式
     * @param bizCallback
     * @param messageId
     * @param isGet
     */
    public abstract void sendHttpRequestToGet(BizCallBack bizCallback,int messageId);
    
    public BaseBiz(Context context){
    	mContext  = context;
        mHttpClient = new APHttpClient(Constant.getAlipayURL(context.getApplicationContext()), context.getApplicationContext());
    }
    
    /*
     * 联网返回�?
     */
//    public class Responsor {
//        final public String type;
//
//        final public int status;
//
//        final public JSONObject obj;
//
//        public String memo; // need modify to meet spec.
//
//        Responsor(String requestType, int resultStatus, JSONObject jsonObject, String jsonMemo) {
//            type = requestType;
//            status = resultStatus;
//            obj = jsonObject;
//            memo = jsonMemo;
//        }
//
//    }
    /*
     * 发送网络请�?
     */
	protected void SendRequest(RootActivity activity,String requestType, final HashMap<String, String> RequestData,
            final Handler callback, final int myWhat) {
        // post-process request . client
        RequestData.put(Constant.RQF_CLIENT_ID, activity.getConfigData().getClientId()); 
        JSONObject jsonRequest = new JSONObject(RequestData);
        SendJSONRequest(activity,requestType, jsonRequest, callback, myWhat);
    }

	protected void SendJSONRequest(final RootActivity activity,final String requestType, final JSONObject jsonRequest,
            final Handler callback, final int myWhat) {
        // 只要访问网络，都置为需要刷新状�?
        AlipayDataCacheHelper.setRefreshFlag(true);
        // Asynchronous
        new Thread(new Runnable() {
            public void run() {
                JSONObject jsonResponse = null;
                int resultStatus = 0;
                String memo = null;
                try {
                    synchronized (mHttpClient) {
                        String sessionId = activity.getConfigData().getSessionId();

                        // process JSON object before send.
                        jsonRequest.put(Constant.RQF_OPERATION_TYPE, requestType);
                        StorageStateInfo.getInstance().putValue(Constants.STORAGE_REQUESTTYPE, requestType);
                        jsonRequest.put(Constant.RPF_SESSION_ID, sessionId);

                        
                        String resp = mHttpClient.sendSynchronousRequest(jsonRequest.toString());
                        if (resp != null && resp != "") {
                            jsonResponse = new JSONObject(resp);
                            resultStatus = jsonResponse.getInt(Constant.RPF_RESULT_STATUS);
                            memo = jsonResponse.optString(Constant.RPF_MEMO);
                        } else {
                            if (mHttpClient.errorType == APHttpClient.SSL_ERROR) {
                                resultStatus = 1; // tbd
                            } else if (mHttpClient.errorType == APHttpClient.IO_ERROR) {
                                resultStatus = 0; // tbd
                            } else {
                                resultStatus = 0; // tbd
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Responsor responsor = new Responsor(requestType, resultStatus, jsonResponse, memo);
                Message msg = new Message();
                msg.what = myWhat;
                msg.arg1 = resultStatus;
                msg.obj = responsor;
                if (callback != null) {
                    callback.sendMessage(msg);
                }
            }
        }).start();
    }
	
	protected void SendRequestToGet(final RootActivity activity,final Handler callback, final int myWhat) {
        // 只要访问网络，都置为需要刷新状�?
        AlipayDataCacheHelper.setRefreshFlag(true);
        // Asynchronous
        new Thread(new Runnable() {
            public void run() {
            	String strResponse = "";
                JSONObject jsonResponse = null;
                int resultStatus = 0;
                try {
                    synchronized (mHttpClient) {
                    	LogUtil.logOnlyDebuggable(TAG, "Request " + mHttpClient.getUrl());
                    	RespData respData = mHttpClient.sendSynchronousGetRequest(null);
                        
                    	if(respData != null){//联网正常
                    		strResponse = respData.strResponse;
                    		if(strResponse != null && !strResponse.equals("")){
                    			jsonResponse = new JSONObject(strResponse);
                                //分解数据
                                if(jsonResponse.has(Constant.RPF_STAT)){//数据正常返回
                                	String stat = jsonResponse.getString(Constant.RPF_STAT);
                                    if(stat.equals(Constant.OK)){//数据正常返回，获取cacheKey成功
                                    	resultStatus = 100;
                                    }else if(stat.equals(Constant.FAILURE)){//数据正常返回，获取cacheKey失败
                                    	resultStatus = Constant.CCDC_FAILURE;
                                    }
                                }else{//返回数据异常
                                	resultStatus = Constant.CCDC_FAILURE;
                                }
                    		}
                    	}else{//联网失败
                    	    if (mHttpClient.errorType == APHttpClient.SSL_ERROR) {
                                resultStatus = 1; // tbd
                            } else if (mHttpClient.errorType == APHttpClient.IO_ERROR) {
                                resultStatus = 0; // tbd
                            } else {
                                resultStatus = 0; // tbd
                            }
                    	}
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                	mHttpClient.setUrl(Constant.getAlipayURL(mContext.getApplicationContext()));
                }

                Responsor responsor = new Responsor("", resultStatus, jsonResponse, "");
                Message msg = new Message();
                msg.what = myWhat;
                msg.arg1 = resultStatus;
                msg.obj = responsor;
                if (callback != null) {
                    callback.sendMessage(msg);
                }
            }
        }).start();
    }
    
    /*
     * 快捷支付相关业务
     */
    /**
     * 支付
     * @param activity
     * @param context
     * @param handler
     * @param progress
     * @param externToken
     * @param bizType
     */
    public void payDeal(final RootActivity activity, Handler handler,
                                         final String tradeNO,
                                         final String externToken, final String partnerID,
                                         final String bizType, String bizSubType) {
        if (bizSubType != null)
            bizSubType = bizSubType.toLowerCase();
        MobileSecurePayHelper mph = new MobileSecurePayHelper();
        mph.payDeal(activity, handler, null, tradeNO, externToken, partnerID, bizType, bizSubType);
    }
    
    public void safeTokenInit(final RootActivity activity, Handler handler,
                                         final String externToken, final String bizType) {
        MobileSecurePayHelper mph = new MobileSecurePayHelper();
        mph.safeTokenInit(activity, handler, null, externToken, bizType);
    }

    //快捷支付相关状态查询，为了以后扩展
    public void checkStatus(final RootActivity activity, Handler handler,
                                             final int myWhat,
                                             final String externToken, String bizSubType, int show, 
                                 			 DialogInterface.OnClickListener btnForCancel){
        MobileSecurePayHelper mph = new MobileSecurePayHelper();
        boolean isUpdate = mph.detectSafepayUpdate(activity, show, btnForCancel,handler);
		if(!isUpdate){
            try {
                MobileSecureCheck mp = new MobileSecureCheck();
                boolean bRet = mp.check(externToken, bizSubType, handler, myWhat, activity);
                if (bRet) {
                    //
                } else {
                    //
                }
            } catch (Exception ex) {
                //
            }
        }
    }

}
