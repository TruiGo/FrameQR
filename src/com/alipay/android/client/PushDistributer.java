package com.alipay.android.client;

import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.util.LogUtil;

public class PushDistributer extends Service {
    String LOGTAG = "PushDistributer>>";
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.logMsg(3, LOGTAG, "onCreate()...");
        this.context = this;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtil.logMsg(3, LOGTAG, "onStart()...");
        if (intent != null) {
            bootType(intent);
            
            reportClickMsg(intent);
        }
        
        //减少不必要的线程服务存在
        try {
        	Thread.sleep(1000);
        } catch (Exception e) {
        	//
        }
        this.stopSelf();
        
    }

    private void bootType(Intent intent) {
        Bundle msgBun = intent.getExtras();
        // push方式启动
        if (msgBun != null && msgBun.getString(Constant.PUSH_DATA) != null
        		&& msgBun.getString(Constant.PUSH_DATA).length() > 0) {
            String msgStr = msgBun.getString(Constant.PUSH_DATA);
            LogUtil.logMsg(3, LOGTAG, "onStart() PUSH_DATA=" + msgStr);
            startApp(msgStr);
        }
    }
    
    private void updateClient(final String updateStr) {
    	Intent intent = new Intent(this,UpdateActivity.class);
    	intent.putExtra("updateParam", updateStr);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }
	
	private void reportClickMsg(Intent intent) {
        Bundle msgBun = intent.getExtras();
        // 获取push消息标示
        if (msgBun != null) {
            String missionId = msgBun.getString(Constant.PUSH_MISSION_ID);
            String perMsgId = msgBun.getString(Constant.PUSH_MSG_ID);
            String pubMsgId = msgBun.getString(Constant.PUSH_PUB_MSG_ID);
            LogUtil.logMsg(3, LOGTAG, "onStart() missionId=" + missionId 
            		+", perMsgId="+perMsgId +", pubMsgId="+pubMsgId);

            startReport(missionId, perMsgId, pubMsgId);
        }
    }

    //发码平台升级项目由浏览器启动客户端，以及传递的数据
    //数据格式:alipay://platformapi/startapp?saId=10000007&clientVersion=1.0.0.0&qrcode=10000000

    /**
     * 启动相应应用，如果客户端没有启动则启动welcome进行初始话，如果已启动则进入对应应用中
     * msgStr: { “type”:”sartApp”, “params”:{ “saId”:”99999”,"uId":"xx","cardNo":"xx"}}
     */
    private void startApp(String msgStr) {
		try {
			if (!Constant.mSafePayIsRunning) {
				JSONObject msgobj = new JSONObject(msgStr);
				if (msgobj != null) {
					String msgtype = msgobj.optString(Constant.PUSHMSGTYPE);
					JSONObject paramsObj = msgobj.optJSONObject(Constant.PARAMS);
					if (msgobj != null) {
						if("clientUpgrade".equals(msgtype)){
							updateClient(paramsObj.toString());
						}else{
							Iterator<?> iterator = paramsObj.keys();
							StringBuffer params = new StringBuffer();
							while (iterator.hasNext()) {
								String name = (String) iterator.next();
								String value = URLEncoder.encode(paramsObj.optString(name));
								if (params.length() > 0)
									params.append("&");
								if (name.equalsIgnoreCase(Constant.APPID)) {
									params.append("appId=" + value);
								} else {
									params.append(name + "=" + value);
								}
							}
							Uri uri = Uri.parse("alipays://platformapi/" + msgtype + "?" + params.toString());
							LogUtil.logMsg(3, LOGTAG, "startApp() " + uri.toString());
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							intent.addCategory(Intent.CATEGORY_BROWSABLE);
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							intent.addFlags(0X00008000);
							
							try {
								startActivity(intent);
							} catch (ActivityNotFoundException e) {
								e.printStackTrace();
							}
					}
				}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    private void startReport(final String missionId, final String perMsgId,final String pubMsgId) {
    	//startReport(missionId, perMsgId, pubMsgId);
    	if ((missionId != null && missionId.length() > 0
    			&& perMsgId != null && perMsgId.length() > 0)
    			|| (pubMsgId != null && pubMsgId.length() > 0)) {
    		
    		initWorker(new Runnable() {
                @Override
                public void run() {
                	//获取当前用户信息
                	String userId = "";
                    AlipayApplication application = (AlipayApplication) context.getApplicationContext();
                    UserInfo tCurUserInfo = application.getDataHelper().getLatestLoginedUser(context);
                    if (tCurUserInfo != null && tCurUserInfo.userId != null
                    		&& tCurUserInfo.userId.length() > 0) {
                    	userId = tCurUserInfo.userId;
                    }
                    String clientId = application.getConfigData().getClientId();
                    LogUtil.logMsg(3, LOGTAG, "startReport() clientId=" + clientId +", userId="+userId);
                    
                    try {
                    	JSONObject jsonResponse = null;
        				int resultStatus = 0;
        				String memo = null;

    					String alipayUrl = Constant.getReportURL(context);
    					APHttpClient httpClient = new APHttpClient(alipayUrl, application);
    					
    					JSONObject requestData = new JSONObject();
    					requestData.put(Constant.PUSH_REPORT_CLIENTID, clientId);
    					requestData.put(Constant.PUSH_REPORT_USERID, userId);
    					requestData.put(Constant.PUSH_REPORT_MISSIONID, missionId);
    					requestData.put(Constant.PUSH_REPORT_MSGID, perMsgId);
    					requestData.put(Constant.PUSH_REPORT_PUBMSGID, pubMsgId);
    					
    					String resp = httpClient.sendSynchronousRequest(
    							requestData.toString(), false);

    					if (resp != null) {
    						jsonResponse = new JSONObject(resp);
    						resultStatus = jsonResponse
    								.getInt(Constant.RPF_RESULT_STATUS);
    						memo = jsonResponse.optString(Constant.RPF_MEMO);
    						LogUtil.logMsg(3, LOGTAG, "startReport() resultStatus=" + resultStatus
    	                    		+ ", memo="+memo);
    					} else {
    						resultStatus = APHttpClient.IO_ERROR;
    					}
                    	
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    	}
    }
    
    /**
     * 创建一个独立线程处理点击上报的事宜
     * @param runnable
     */
     private void initWorker(Runnable runnable) {
         HandlerThread handlerThread = new HandlerThread("ReportThread");
         handlerThread.setPriority(Thread.MIN_PRIORITY);
         handlerThread.start();

         Handler handler = new Handler(handlerThread.getLooper()) {

             @Override
             public void handleMessage(Message msg) {
                 super.handleMessage(msg);
             }

         };
         handler.post(runnable);
     }
}
