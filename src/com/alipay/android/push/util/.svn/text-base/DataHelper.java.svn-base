package com.alipay.android.push.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.util.mock.testAPNConfig;
import com.alipay.android.push.data.ConfigData;
import com.alipay.android.push.data.MsgInfo;
import com.alipay.android.push.data.MsgRecord;
import com.alipay.android.push.data.NotifierInfo;
import com.alipay.android.push.data.PerMsgRecord;
import com.alipay.android.push.data.PubMsgRecord;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;


public class DataHelper {
	private static final String LOGTAG = LogUtil.makeLogTag(DataHelper.class);
	private static final String NOTIFICATION_ID = "1127";
	
	private Context mContext;
	
	public DataHelper(Context context) {
		mContext = context;
	}
	
	// it's for config request to handle msg list
	public void processMsgList(String msgData, boolean pubFlag) {
		JSONArray appArray = null;
		NotifierInfo noteInfo = null;
		
		if (msgData != null && msgData.length() > 0) {
			try {
				appArray = new JSONArray(msgData);
				LogUtil.LogOut(3, LOGTAG, "processMsgList() pubFlag:"+pubFlag +", msgLen=" + appArray.length());
				
				JSONObject msgItem = new JSONObject();
				for (int i = 0; i < appArray.length(); i++) {
					msgItem = appArray.getJSONObject(i);
					
					noteInfo = handlePushMsg(msgItem, pubFlag);
					showMsgDetail(noteInfo, Constants.ACTION_SHOW_PUBMESSAGE);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LogUtil.LogOut(2, LOGTAG, "processMsgList() msgData is null!");
		}
	}
	
	public NotifierInfo handlePushMsg(JSONObject msgItem, boolean pubFlag) {
		NotifierInfo noteInfo = new NotifierInfo();
		
		noteInfo.setId(NOTIFICATION_ID);
		noteInfo.setTitle(msgItem.optString(Constants.RPF_MSG_TITLE));
		noteInfo.setContent(msgItem.optString(Constants.RPF_MSG_CONTENT));
		noteInfo.setUri(msgItem.optString(Constants.RPF_MSG_URI));
		noteInfo.setStyle(msgItem.optString(Constants.RPF_MSG_STYLE));
		noteInfo.setBizType(msgItem.optString(Constants.RPF_MSG_BIZTYPE));
		
		MsgInfo msgInfo = new MsgInfo();
		if (pubFlag) {
			msgInfo.setPubMsgId(msgItem.optString(Constants.RPF_MSG_ID));
		} else {
			msgInfo.setMissionId(msgItem.optString(Constants.RPF_MISSION_ID));
			msgInfo.setPerMsgId(msgItem.optString(Constants.RPF_MSG_ID));
		}
		noteInfo.setMsgInfo(msgInfo);
		
		LogUtil.LogOut(3, LOGTAG, "processMsgList() curPerMsgId=" + msgInfo.getPerMsgId()
				+ ", bizType="+noteInfo.getBizType() + ", uri="+noteInfo.getUri()
				+ ", title="+noteInfo.getTitle() + ", content="+noteInfo.getContent());
		
		return noteInfo;
	}
	
	public void showMsgDetail(NotifierInfo noteInfo, String action) {
		MsgInfo msgInfo = noteInfo.getMsgInfo();
		MsgRecord msgRecord = null;
		
		if (msgInfo.getPubMsgId().length() > 0) {
			msgRecord = new PubMsgRecord(mContext);
		} else {
			msgRecord = new PerMsgRecord(mContext);
			
			AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
	    	//获取当前用户信息
	        UserInfo tCurUserInfo = application.getDataHelper().getLatestLoginedUser(mContext);
	        if (tCurUserInfo != null && tCurUserInfo.userId != null
	        		&& tCurUserInfo.userId.length() > 0) {
	        	msgRecord.setCurUserId(tCurUserInfo.userId);
	        }
		}
		
		if (!msgRecord.isContainMsg(noteInfo)) {
			LogUtil.LogOut(4, LOGTAG, "processMsgList() will show msg.");
			// will display this notifierInfo
			Intent intent = new Intent(action);
			Bundle bundle = new Bundle(); 
			bundle.putParcelable(Constants.PUSH_NOTIFIRE, noteInfo);
			intent.putExtras(bundle);

			mContext.sendBroadcast(intent);
			LogUtil.LogOut(4, LOGTAG, "showMsgDetail() sendBroadcast!");
			
			//保存收到的内容MsgID
			msgRecord.saveMsgRecord(noteInfo);
		} else {
			LogUtil.LogOut(2, LOGTAG, "processMsgList() Drop the Packet!");
			LogUtil.LogOut(3, LOGTAG, "processMsgList() curMissionId="+msgInfo.getMissionId()
					+ ", curPerMsgId="+msgInfo.getPerMsgId() +", curPubMsgId="+msgInfo.getPubMsgId());
		}
	}
	
	
	public void processCfgData(String cfgData, String cfgId) {
		JSONObject cfgObject = null;
		
		if (cfgData != null && cfgData.length() > 0) {
			try {
				cfgObject = new JSONObject(cfgData);

				ConfigData config = new ConfigData();
				config.cfgId = cfgId;
				config.domain = cfgObject.optString(Constants.RPF_CFG_DOMAIN);
				config.port = cfgObject.optInt(Constants.RPF_CFG_PORT);
				config.encryptFlag = cfgObject.optString(Constants.RPF_CFG_SSL);
				config.zipFlag = cfgObject.optString(Constants.RPF_CFG_ZIP);
				config.protoVersion = cfgObject.optInt(Constants.RPF_CFG_VERSION);

				LogUtil.LogOut(3, LOGTAG, "processCfgData() cfgId="+cfgId +", domain=" + config.domain
						+ ", port="+config.port + ", protoVersion="+config.protoVersion
						+ ", encryptFlag="+config.encryptFlag + ", zipFlag="+config.zipFlag);
				
				saveCfgData(config);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LogUtil.LogOut(2, LOGTAG, "processCfgData() cfgData is null!");
		}
	}
	
	public void saveCfgData(ConfigData config) {
		SharedPreferences sharedPrefs = 
				mContext.getSharedPreferences(Constants.PUSH_PREFERENCE_NAME,Context.MODE_PRIVATE);
		
		Editor editor = sharedPrefs.edit();
		
		editor.putString(Constants.REQ_PUSH_CFG_ID, config.cfgId);
        editor.putString(Constants.XMPP_HOST, config.domain);
        editor.putInt(Constants.XMPP_PORT, config.port);
        editor.putInt(Constants.PROTOCOL_VERSION, config.protoVersion);
        editor.putString(Constants.SSL_USED, config.encryptFlag);
        editor.putString(Constants.ZIP_FLAG, config.zipFlag);
        
        editor.commit();
	}
	
	public ConfigData getCfgData() {
		SharedPreferences sharedPrefs = 
				mContext.getSharedPreferences(Constants.PUSH_PREFERENCE_NAME,Context.MODE_PRIVATE);
		
		ConfigData cfgData = new ConfigData();
		cfgData.cfgId = sharedPrefs.getString(Constants.REQ_PUSH_CFG_ID, "");
		cfgData.domain = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
		cfgData.port = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
		cfgData.protoVersion = sharedPrefs.getInt(Constants.PROTOCOL_VERSION, PacketConstants.PACKET_VERSION_2);
		cfgData.encryptFlag = sharedPrefs.getString(Constants.SSL_USED, "1");
		cfgData.zipFlag = sharedPrefs.getString(Constants.ZIP_FLAG, "0");
		
		LogUtil.LogOut(4, LOGTAG, "cfgId:"+cfgData.cfgId 
				+ ", domain:"+cfgData.domain + ", port:"+cfgData.port
				+ ", ssl:"+cfgData.encryptFlag + ", protoVersion:"+cfgData.protoVersion);
		
		return cfgData;
	}
	
	public long getCfgPolicy(String key) {
		SharedPreferences sharedPrefs = 
				mContext.getSharedPreferences(Constants.PUSH_PREFERENCE_NAME,Context.MODE_PRIVATE);

		return sharedPrefs.getLong(key, 0);

	}
	
	public void saveCfgPolicy(String key, long value) {
		SharedPreferences sharedPrefs = 
				mContext.getSharedPreferences(Constants.PUSH_PREFERENCE_NAME,Context.MODE_PRIVATE);
		
		Editor editor = sharedPrefs.edit();
		editor.putLong(key, value);
        editor.commit();
	}
	
	// 判断手机的数据网络开关的状态
	public static boolean getMobileDataStatus(Context context) {
		ConnectivityManager conMgr = 
 				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
 		
 		Class<?> conMgrClass = null; 				// ConnectivityManager类
        Field iConMgrField = null; 					// ConnectivityManager类中的字段
        Object iConMgr = null; 						// IConnectivityManager类的引用
        Class<?> iConMgrClass = null; 				// IConnectivityManager类
        Method getMobileDataEnabledMethod = null; 	// getMobileDataEnabled方法

	    try {
	    	// 取得ConnectivityManager类
	        conMgrClass = Class.forName(conMgr.getClass().getName());
	         
	        // 取得ConnectivityManager类中的对象mService
	        iConMgrField = conMgrClass.getDeclaredField("mService");
	         
	        // 设置mService可访问
	        iConMgrField.setAccessible(true);
	         
	        // 取得mService的实例化类IConnectivityManager
	        iConMgr = iConMgrField.get(conMgr);
	         
	        // 取得IConnectivityManager类
	        iConMgrClass = Class.forName(iConMgr.getClass().getName());
	         
	        // 取得IConnectivityManager类中的getMobileDataEnabled(boolean)方法
	        getMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("getMobileDataEnabled");
	         
	        // 设置getMobileDataEnabled方法可访问
	        getMobileDataEnabledMethod.setAccessible(true);
	         
	        // 调用getMobileDataEnabled方法
	        return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
        } catch (ClassNotFoundException e) {
         	e.printStackTrace();
        } catch (NoSuchFieldException e) {
         	e.printStackTrace();
        } catch (SecurityException e) {
         	e.printStackTrace();
        } catch (NoSuchMethodException e) {
         	e.printStackTrace();
        } catch (IllegalArgumentException e) {
         	e.printStackTrace();
        } catch (IllegalAccessException e) {
         	e.printStackTrace();
        } catch (InvocationTargetException e) {
     	   // TODO Auto-generated catch block
     	   e.printStackTrace();
        }
         
        return false;
   }
	
	public JSONObject getRequestObj() {
    	JSONObject requestData = new JSONObject();
    	
    	AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
    	
    	//用于进行多维度消息分发
    	String channelId = "";
    	String productId = "";
    	String productVersion = "";
    	String osType = "Android";
    	String osVersion = "";
    	
    	try {
    		String clientId = application.getConfigData().getClientId();
    		requestData.put(ConnectParamConstant.IMEI, clientId);
    		
	        channelId = CacheSet.getInstance(mContext).getString(Constants.CHANNELS);
	        productId = application.getConfigData().getProductId();	
	        productVersion = application.getConfigData().getProductVersion();
	        osVersion = Build.VERSION.RELEASE;

			requestData.put(ConnectParamConstant.INSTALLCHANNEL, channelId);
			requestData.put(Constants.REQ_PRODUCT_ID, productId);
			requestData.put(ConnectParamConstant.PRODUCTVERSION, productVersion);
			requestData.put(Constants.REQ_OS_TYPE, osType);
			requestData.put(Constants.REQ_OS_VERSION, osVersion);
			
			String manufacturer = "";
			try {
				Field mField = Build.class
						.getDeclaredField("MANUFACTURER");
				mField.setAccessible(true);
				manufacturer = mField.get(Build.class).toString();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			requestData.put(ConnectParamConstant.MANUFACTURER, manufacturer);
			requestData.put(ConnectParamConstant.MODEL, Build.MODEL);
			
			requestData.put(ConnectParamConstant.CONNECTTYPE,
					DeviceHelper.getApnInUse(mContext));
//			requestData.put(ConnectParamConstant.CONNECTTYPE, testAPNConfig.getTestAPNName());
			requestData.put(ConnectParamConstant.MOBILE_STATE,
					DataHelper.getMobileDataStatus(mContext));
			requestData.put(ConnectParamConstant.MOBILE_IP, StringUtils.getLocalIpAddress());
			
			LogUtil.LogOut(3, LOGTAG, "getRequestObj() requestData=" + requestData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return requestData;
    }
	
}
