package com.alipay.android.push;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.util.record.RecordtoFile;

/** 
 * A broadcast receiver to handle the changes from client.
 */
public class ClientActionReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil.makeLogTag(ClientActionReceiver.class);
    
    private static int MAX_TIME_CHECKING = 3 * 60 * 60;		//单位：秒

    private NotificationService notificationService;
    
    public ClientActionReceiver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	String action = intent.getAction();
        LogUtil.LogOut(3, LOGTAG, "onReceive() action=" + action);
        
        AlipayApplication application = (AlipayApplication) context.getApplicationContext();
        UserInfo tCurUserInfo = application.getDataHelper().getLatestLoginedUser(context);
        
        NetworkInfo networkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null) {
        	isConnected = networkInfo.isConnected();
        }
        
        //判断是否要启动push connect
        Notifier notifier = new Notifier(context);
        LogUtil.LogOut(3, LOGTAG, "isNotificationEnabled=" + notifier.isNotificationEnabled()
        		+ ", isConnected="+isConnected);
        
        RecordtoFile.recordPushInfo(RecordtoFile.REASON_UNKNOWN, RecordtoFile.ACTIONT_STATUS_CHECK,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_UNKNOWN, 
				System.currentTimeMillis()+1*0*1000,
				"ClientActionReceiver_onReceive getAction:"+action,
				1);


        if (action != null && action.equals(Constants.ACTION_KEEPLIVE_TIMER)) {
        	//需要出发心跳请求
        	this.notificationService.getXmppManager().submitHeartBeatTask();
        } else if (action != null && action.equals(Constants.ACTION_PUSH_CONNECT)) {
        	synchronized (this) {
    			//首先检查通知服务设置的状态
    	    	boolean settingStatus = notifier.isNotificationEnabled() && notifier.isNotificationTime();
    	    	
    	    	RecordtoFile.recordPushInfo(RecordtoFile.REASON_CLIENT_RECONN, RecordtoFile.ACTIONT_STATUS_CHECK,
        				System.currentTimeMillis(), 
        				RecordtoFile.ACTION_CONN, 1*0*1000,
        				"ClientActionReceiver_PushCheck:settingStatus="+settingStatus,
        				1);
    	    	LogUtil.LogOut(3, LOGTAG, "run settingStatus="+settingStatus);
    	    	
    	    	//应该发起，且可以发起连接时
    	    	if (settingStatus && isConnected) {
//    	    		this.notificationService.getXmppManager().connect();
    	    		notificationService.connect();
    	    	}
    			
    		}
        } else if(NotificationService.NOTICE_CHECK_ALARM_TIMER.equals(action)){                     
//            mHandler.sendEmptyMessage(MSG_DISPLAY_ALARM_TIMER);  
        	//检查是否应该启动连接
        	int curStatus = checkPushStatus();
        	
        	RecordtoFile.recordPushInfo(RecordtoFile.REASON_CLIENT_CHECK, RecordtoFile.ACTIONT_TIMER,
    				System.currentTimeMillis(), 
    				RecordtoFile.ACTIONT_STATUS_CHECK, System.currentTimeMillis()+getNextClock()*1000,
    				"ClientActionReceiver_ClientCheck:curStatus="+curStatus,
    				1);
        	
        	switch(curStatus) {
	        	case -1:
	        		//应该关闭连接，之后继续触发定时器
	        		notificationService.disconnect();
	        		notificationService.startAlarmTimer(getNextClock()*1000);
	        		break;
	        	case 0:
	        		//状态一致，不做新动作，继续触发定时器
	        		notificationService.startAlarmTimer(getNextClock()*1000);
	        		break;
	        	case 1:
	        		//应该打开连接，之后继续触发定时器
//	        		notificationService.getXmppManager().startReconnectionThread();
	        		
	        		//首先检查push条件
	        		if (tCurUserInfo != null && tCurUserInfo.userId != null && tCurUserInfo.userId.length() > 0
	                		&& notifier.isNotificationEnabled() && isConnected) {
	        			//做时间间隔检查
	        			long lastConnectedTime = SmackConfiguration.getLastConnectedTime();
	        			long curWaitTime = 0;
	        			
	        			if (lastConnectedTime > 0) {
	        	        	long lostedTime = System.currentTimeMillis() - lastConnectedTime;
	        	        	// 距离上次连接的时间间隔： milliseconds
	        	        	curWaitTime = SmackConfiguration.getReconnectInterval()*1000 - lostedTime;
	        	        	
	        	        	RecordtoFile.recordPushInfo(RecordtoFile.REASON_CLIENT_CHECK, RecordtoFile.ACTIONT_STATUS_CHECK,
	        	    				System.currentTimeMillis(), 
	        	    				RecordtoFile.ACTIONT_UNKNOWN, System.currentTimeMillis()+getNextClock()*1000,
	        	    				"ClientActionReceiver_onReceive:curWaitTime="+curWaitTime,
	        	    				1);
	        	        	
	        	        	if (curWaitTime <= 0) {
	        	        		curWaitTime = 0;
	        	        		this.notificationService.disconnect();
	        	        		notificationService.connect();
	        	        	} else {
	        	        		//还未到需要等待的时间间隔
	        	        		//继续等待
	        	        	}
	        			} else {
	        				notificationService.connect();
	        			}
	                }

	        		//继续启动定时器，作为保护机制
	        		notificationService.startAlarmTimer(getNextClock()*1000);
	        		break;
        	}
        }
    }
    
    private int checkPushStatus() {
    	int ret = -1;
    	
    	//1、获取当前连接状态
    	boolean linkStatus = false;
    	if (this.notificationService.getXmppManager().getConnection() != null) {
    		linkStatus = this.notificationService.getXmppManager().getConnection().isConnected();
    	}
    	
    	//2、通知服务设置的状态
    	Notifier notifier = new Notifier(this.notificationService);
    	boolean settingStatus = notifier.isNotificationEnabled() && notifier.isNotificationTime();
    	LogUtil.LogOut(3, LOGTAG, "checkPushStatus linkStatus="+linkStatus 
    			+", settingStatus="+settingStatus);
    	
    	if (settingStatus == linkStatus) {
    		//当前状态和设置情况一致
    		ret = 0;
    	} else {	//不一致
    		if (settingStatus){
	    		//需要打开
	    		ret = 1;
	    	} else {
	    		//应该关闭
	    		ret = -1;
	    	}
    	}
    	return ret;
    }
    
    //获取当前时间和整点时间的分钟差值
    private int getNextClock() {
    	int ret = 37*60;
    	
    	if (SmackConfiguration.getReconnectInterval() > 0) {
    		//计算检查的间隔，单位-秒
    		ret = 2*SmackConfiguration.getReconnectInterval() + 17*60;
    		if (ret >= MAX_TIME_CHECKING) {
    			//当服务端给定的checkingTime大于1小时时，则采用服务端的配置参数
    			//目的：减少此检查任务的间隔
    			ret = SmackConfiguration.getReconnectInterval();
    		}
    	}
    	/*
    	String curMinutes = new SimpleDateFormat("mm").format(new Date());
    	ret = ret - Integer.parseInt(curMinutes);
    	LogUtil.LogOut(3, LOGTAG, "isInTime curMinutes="+curMinutes +", ret="+ret);
    	*/
    	
    	return ret;
    }

}
