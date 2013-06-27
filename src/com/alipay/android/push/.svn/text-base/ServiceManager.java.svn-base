package com.alipay.android.push;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.policy.ConfigPolicy;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.DataHelper;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.data.ConfigData;
import com.alipay.android.push.util.record.RecordtoFile;

/** 
 * This class is to manage the notification service and to load the configuration.
 */
public final class ServiceManager {

    private static final String LOGTAG = LogUtil.makeLogTag(ServiceManager.class);

    private Context context;
    private Properties props;
    private String cfgId;
    private String xmppHost;
    private String xmppPort;
    private String protocolVern;
    private String sslUsed;
    private String zipFlag;
    private String retryTimes;
    
    private String mUserId = "";
    
    private SharedPreferences sharedPrefs;
    private SharedPreferences serviceRecord;
    private String settingName;

    private String callbackActivityPackageName;
    private String callbackActivityClassName;

    public ServiceManager(Context context) {
        this.context = context;

        if (context instanceof Activity) {
        	LogUtil.LogOut(5, LOGTAG, "Callback Activity...");
            
            Activity callbackActivity = (Activity) context;
            callbackActivityPackageName = callbackActivity.getPackageName();
            callbackActivityClassName = callbackActivity.getClass().getName();
        }

//        xmppHost = Constant.getPushURL(context);//props.getProperty("xmppHost", "127.0.0.1");
//        xmppPort = Constant.getPushPort(context) +"";//props.getProperty("xmppPort", "5222");
//        sslUsed = Constant.getPushSsl(context)+"";//props.getProperty("sslUsed", "true");
//        retryTimes = 2 + "";//props.getProperty("retryTimes", "1");
        
        sharedPrefs = context.getSharedPreferences(Constants.PUSH_PREFERENCE_NAME, Context.MODE_PRIVATE);
        DataHelper dataHelper = new DataHelper(context);
        ConfigData cfgData = dataHelper.getCfgData();
        cfgId = cfgData.cfgId;
        if (cfgId != null && cfgId.length() > 0) {
        	//不在从安装包中加载push的config数据
        	LogUtil.LogOut(3, LOGTAG, "push use config data based on cfgId:"+cfgId);
        } else {
        	//需要加载默认的配置数据
        	props = loadProperties();
        	
            xmppHost = props.getProperty(Constants.XMPP_HOST, "127.0.0.1");
            xmppPort = props.getProperty(Constants.XMPP_PORT, "5222");
            protocolVern = props.getProperty(Constants.PROTOCOL_VERSION, "2");
            sslUsed = props.getProperty(Constants.SSL_USED, "1");
            zipFlag = props.getProperty(Constants.ZIP_FLAG, "0");
            retryTimes = props.getProperty("retryTimes", "1");
            LogUtil.LogOut(3, LOGTAG,"push dest==>" + xmppHost +":"+xmppPort+":"+protocolVern+":"+sslUsed);
            
            Editor editor = sharedPrefs.edit();
            
            editor.putString(Constants.REQ_PUSH_CFG_ID, "");
            editor.putString(Constants.XMPP_HOST, xmppHost);
            editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
            editor.putInt(Constants.PROTOCOL_VERSION, Integer.parseInt(protocolVern));
            editor.putString(Constants.SSL_USED, sslUsed);
            editor.putString(Constants.ZIP_FLAG, zipFlag);
            editor.putInt(Constants.RETRY_TIMES, Integer.parseInt(retryTimes));
            
            editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME,callbackActivityPackageName);
            editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME,callbackActivityClassName);
            editor.commit();
            // Log.i(LOGTAG, "sharedPrefs=" + sharedPrefs.toString());
        }

        serviceRecord = context.getSharedPreferences(Constants.PUSH_SERVICE_RECORD, Context.MODE_PRIVATE);
    }
    
    private void recordService(int triger) {
    	//获取当前信息
    	String curTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

    	synchronized(serviceRecord){
	    	//读取之前的信息，转存
		    Editor editor = serviceRecord.edit();
	    	String old_triger = serviceRecord.getString(Constants.SERVICE_PRE_TRIGER, Constants.SERVICE_INIT_TRIGER);
	    	String old_time = serviceRecord.getString(Constants.SERVICE_PRE_TIME, Constants.SERVICE_INIT_TIME);
	    	editor.putString(Constants.SERVICE_OLD_TRIGER, old_triger);
	        editor.putString(Constants.SERVICE_OLD_TIME, old_time);
	        
	    	String pre_triger = serviceRecord.getString(Constants.SERVICE_CUR_TRIGER, Constants.SERVICE_INIT_TRIGER);
	    	String pre_time = serviceRecord.getString(Constants.SERVICE_CUR_TIME, Constants.SERVICE_INIT_TIME);
	    	editor.putString(Constants.SERVICE_PRE_TRIGER, pre_triger);
	        editor.putString(Constants.SERVICE_PRE_TIME, pre_time);
	        
	    	editor.putString(Constants.SERVICE_CUR_TRIGER, String.valueOf(triger));
	        editor.putString(Constants.SERVICE_CUR_TIME, curTime);
	        editor.commit();
    	}
    }
    
    public String getServiceRecord() {
    	StringBuffer bufRecord = new StringBuffer();
    	
    	synchronized(serviceRecord){
	    	bufRecord.append(serviceRecord.getString(Constants.SERVICE_OLD_TIME, Constants.SERVICE_INIT_TIME))
	    		.append(Constants.SERVICE_RECORD_LINKED)
	    		.append(serviceRecord.getString(Constants.SERVICE_OLD_TRIGER, Constants.SERVICE_INIT_TRIGER))
	    		.append(Constants.SERVICE_RECORD_SEPERATOR)
	    		.append(serviceRecord.getString(Constants.SERVICE_PRE_TIME, Constants.SERVICE_INIT_TIME))
	    		.append(Constants.SERVICE_RECORD_LINKED)
	    		.append(serviceRecord.getString(Constants.SERVICE_PRE_TRIGER, Constants.SERVICE_INIT_TRIGER))
	    		.append(Constants.SERVICE_RECORD_SEPERATOR)
	    		.append(serviceRecord.getString(Constants.SERVICE_CUR_TIME, Constants.SERVICE_INIT_TIME))
	    		.append(Constants.SERVICE_RECORD_LINKED)
	    		.append(serviceRecord.getString(Constants.SERVICE_CUR_TRIGER, Constants.SERVICE_INIT_TRIGER));
    	}
    	return bufRecord.toString();
    }

    public void startService(int triger) {
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
            	//因为任何一个触发事件，都会走到这部分逻辑；可能导致连接才中断不久，也被认为之前连接未能建立
            	//因此，去除此复位动作——此参数的默认值本已经为0
            	//SmackConfiguration.setLastConnectedTime(0);
            	
            	String curTriger = Constants.SERVICE_INIT_TRIGER;
            	synchronized(serviceRecord){
            		curTriger = serviceRecord.getString(Constants.SERVICE_CUR_TRIGER, Constants.SERVICE_INIT_TRIGER);
            	}

                Intent intent = NotificationService.getIntent();
                if (curTriger != null) {
                	//将服务启动原因传给服务
                	intent.putExtra(Constants.SERVICE_CUR_TRIGER, curTriger);
                }
                context.startService(intent);
                
            }
        });

        //获取当前用户信息
        AlipayApplication application = (AlipayApplication) context.getApplicationContext();
        UserInfo tCurUserInfo = application.getDataHelper().getLatestLoginedUser(context);
        if (tCurUserInfo != null && tCurUserInfo.userId != null
        		&& tCurUserInfo.userId.length() > 0) {
        	mUserId = tCurUserInfo.userId;
        }
        
        //判断是否要启动push service
        Notifier notifier = new Notifier(this.context);
        LogUtil.LogOut(3, LOGTAG, "isNotificationEnabled=" + notifier.isNotificationEnabled()
        		+ ", triger="+triger +", mUserId="+mUserId);

        //符合启动条件，并且有用户帐号信息，才能启动服务
        if (notifier.isNotificationEnabled()) {
        	RecordtoFile.recordPushInfo(1);
        	
        	RecordtoFile.recordPushInfo(RecordtoFile.REASON_USER_CHECKED, RecordtoFile.ACTION_SERVICE_START,
    				System.currentTimeMillis(), 
    				RecordtoFile.ACTION_SERVICE_START, 
    				System.currentTimeMillis()+1*10*1000,
    				"ServiceManager_startService:mUserId="+mUserId,
    				1);
        	
        	recordService(triger);
        	
        	if (mUserId.length() > 0) {
        		serviceThread.start();
        	}
        	
        	//调度config服务
        	ConfigPolicy cfgPolicy = new ConfigPolicy(context);
        	cfgPolicy.toStart(triger);
        }
        
        
    }

    public void stopService() {
        Intent intent = NotificationService.getIntent();
        context.stopService(intent);
        LogUtil.LogOut(3, LOGTAG, "stopService is called.");
        
        RecordtoFile.recordPushInfo(RecordtoFile.REASON_SERVICE_STOP, RecordtoFile.ACTION_SERVICE_STOP,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_UNKNOWN, 
				System.currentTimeMillis()+1*10*1000,
				"ServiceManager_stopService:mUserId="+mUserId,
				1);
        
        mUserId = "";
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("androidpn", "raw",context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e(LOGTAG, "Could not find the properties file.", e);
            // e.printStackTrace();
        }
        return props;
    }
    
    public void setSettingPreferences(String settings) {
    	Editor editor = sharedPrefs.edit();
        editor.putString(Constants.NOTIFICATION_SETTING_NAME, settings);
        editor.commit();
	}

    public void setNotificationIcon(int iconId) {
        Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.NOTIFICATION_ICON, iconId);
        editor.commit();
    }
}
