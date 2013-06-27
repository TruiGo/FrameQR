package com.alipay.android.push.config;

import java.lang.reflect.Field;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.net.alipayclient2.APHttpClient;

import com.alipay.android.push.NotificationService;
import com.alipay.android.push.data.MsgRecord;
import com.alipay.android.push.data.PubMsgRecord;
import com.alipay.android.push.util.ConnectParamConstant;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.DataHelper;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;
import com.alipay.android.push.util.record.RecordtoFile;

public class ConfigReqHelper {
	private static final String LOGTAG = LogUtil.makeLogTag(ConfigReqHelper.class);
	private static final int MSG_CONFIG_DATA = 0x10000;
	private static final int MIN_TRY_COUNT = 1;
	private static final int MIN_TRY_INTERVAL = 4*60;	//分钟
	
	private Context mContext = null;
	private String mUserId = "";
	private DataHelper mDataHelper;
	
    public void request(Context context) {
    	mContext = context;
    	
    	AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
    	//获取当前用户信息
        UserInfo tCurUserInfo = application.getDataHelper().getLatestLoginedUser(mContext);
        if (tCurUserInfo != null && tCurUserInfo.userId != null
        		&& tCurUserInfo.userId.length() > 0) {
        	mUserId = tCurUserInfo.userId;
        }
        
        mDataHelper = new DataHelper(context);
    	
    	String alipayUrl = getCfgServerURL(context);
    	JSONObject requestData = mDataHelper.getRequestObj();
    	JSONObject jsonResponse = null;
		int resultStatus = 0;
		String memo = null;
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_SERVICE_START, RecordtoFile.ACTION_CONN,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_DISCONN, 
				System.currentTimeMillis()+1*10*1000,
				"ConfigReqHelper_request: requestData="+requestData.toString(),
				1);
		
		if (requestData != null) {
			try {
				requestData.put(Constants.REQ_USER_ID, mUserId);
				MsgRecord msgRecord = new PubMsgRecord(mContext);
				requestData.put(Constants.REQ_PUB_MSG_ID, msgRecord.getMaxMsgid());
				requestData.put(Constants.REQ_PUSH_CFG_ID, mDataHelper.getCfgData().cfgId);
				
	    		APHttpClient httpClient = new APHttpClient(alipayUrl, application);
	    		
	    		String resp = httpClient.sendSynchronousRequest(
	    				requestData.toString(), false);
	    		
	    		if (resp != null) {
					jsonResponse = new JSONObject(resp);
					resultStatus = jsonResponse
							.getInt(Constants.RPF_RESULT_STATUS);
					memo = jsonResponse.optString(Constants.RPF_MEMO);
					LogUtil.LogOut(3, LOGTAG, "request() resultStatus=" + resultStatus
	                		+ ", memo="+memo);
				} else {
					resultStatus = APHttpClient.IO_ERROR;
				}
	    	} catch (Exception e) {
				e.printStackTrace();
			}
	    		
			try {
				Message msg = new Message();
				msg.what = MSG_CONFIG_DATA;
				msg.arg1 = resultStatus;
				msg.obj = jsonResponse;
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LogUtil.LogOut(2, LOGTAG, "request() getRequestObj is null!");
		}
	}
    
    /**
     * 获取配置请求的响应处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        	switch (msg.what) {
            	case MSG_CONFIG_DATA:
            		long curTime = System.currentTimeMillis();
        			if (msg.arg1 == 100) {
        				JSONObject jsonResponse = (JSONObject)msg.obj;
        				handleCfgData(jsonResponse);
        				
        				// need to save current time for next run
        				mDataHelper.saveCfgPolicy(Constants.LAST_CONFIG_TIME, curTime);
        				LogUtil.LogOut(3, LOGTAG, "handleMessage() saveCfgPolicy Time is:"
        						+StringUtils.timeLong2Date(curTime));
        			} else {
        				//请求配置信息失败
        				LogUtil.LogOut(2, LOGTAG, "handleMessage() resultStatus is:"+msg.arg1);
        			}
        			
        			try {
    					Thread.sleep(500);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				
    				RecordtoFile.recordPushInfo(RecordtoFile.REASON_CONN_TIMEOUT, RecordtoFile.ACTION_SERVICE_STOP,
    						System.currentTimeMillis(), 
    						RecordtoFile.ACTION_SERVICE_START, 
    						System.currentTimeMillis()+1*10*1000,
    						"ConfigReqHelper_handleMessage: curTime="+StringUtils.timeLong2Date(curTime),
    						1); 
        			
        			Intent intent = ConfigService.getIntent();
    				mContext.stopService(intent);
    		        LogUtil.LogOut(3, LOGTAG, "handleMessage() stop ConfigService is called.");
            		break;
        	}
            
        	super.handleMessage(msg);
        }
    };
    
    private void handleCfgData(JSONObject jsonResponse) {
    	DataHelper dataHelper = new DataHelper(mContext);
    	
    	int successCount = jsonResponse.optInt(Constants.RPF_SUCCESS_COUNT);
		if (successCount <= 0) {
			successCount = MIN_TRY_COUNT;
		}
		dataHelper.saveCfgPolicy(Constants.RPF_SUCCESS_COUNT, successCount);
		
		int intervalTime = jsonResponse.optInt(Constants.RPF_INTERVAL_TIME);
		if (intervalTime <= 0) {
			intervalTime = MIN_TRY_INTERVAL;
		}
		dataHelper.saveCfgPolicy(Constants.RPF_INTERVAL_TIME, intervalTime);
		
		String pushCfgId = jsonResponse.optString(Constants.REQ_PUSH_CFG_ID);
		String pushCfgData = jsonResponse.optString(Constants.RPF_PUSH_CFG);
		if (pushCfgData != null && pushCfgData.length() > 0) {
			mDataHelper.processCfgData(pushCfgData, pushCfgId);
		}
		
		String pubMsg = jsonResponse.optString(Constants.RPF_PUSH_PUB_MSG);
		if (pubMsg != null && pubMsg.length() > 0) {
			mDataHelper.processMsgList(pubMsg, true);
		}
		
		String personalMsg = jsonResponse.optString(Constants.RPF_PUSH_PERSONAL_MSG);
		if (personalMsg != null && personalMsg.length() > 0) {
			mDataHelper.processMsgList(personalMsg, false);
		}
		
		LogUtil.LogOut(3, LOGTAG, "handleCfgData() successCount=" + successCount
				+ ", intervalTime="+intervalTime);
		
		LogUtil.LogOut(4, LOGTAG, "handleCfgData() pushCfgId="+pushCfgId 
				+ ", pushCfgData="+pushCfgData.toString());
		LogUtil.LogOut(4, LOGTAG, "handleCfgData() pubMsg="+pubMsg.toString() 
				+ ", personalMsg="+personalMsg.toString());
    }
    
    /**
     * 获取配置服务器的地址
     * @param context
     * @return
     */
    private final String getCfgServerURL(Context context){
    	if (Constant.isDebug(context)) {
            return Constant.getConfigrURL(context);
        } else {
            return "http://mobilecns.alipay.com/getMsgAndCfg.htm";
        }
    }
}
