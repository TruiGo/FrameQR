package com.alipay.android.push;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.alipay.android.push.config.ConfigService;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.record.RecordtoFile;

public class BroadcastActionReceiver extends BroadcastReceiver{
	private static boolean isTestClient = true;
	
	private static final String LOGTAG = LogUtil.makeLogTag(BroadcastActionReceiver.class);
    
    static final String ALIPAY_PROCESS = "com.eg.android.AlipayGphone";
    
    private static List<ActivityManager.RunningAppProcessInfo> procList = null;
    private static List<ActivityManager.RunningServiceInfo> runServiceList = null;
    //所要获取的最大的服务数目
    private static final int maxServiceNum = 50;
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        LogUtil.LogOut(3, LOGTAG, "onReceive() getAction=" + action);
		
		//启动push服务——低于2.0的不需要启动
		int expectedVersion = 4;
		if(Integer.valueOf(Build.VERSION.SDK).intValue() > expectedVersion){
			if (isTestClient && !checkAlipayProcess(context)) {
				RecordtoFile.recordPushInfo(RecordtoFile.REASON_PHONE_BOOTED, RecordtoFile.ACTION_SERVICE_START,
						System.currentTimeMillis(), 
						RecordtoFile.ACTION_SERVICE_START, 
						System.currentTimeMillis()+1*10*1000,
						"BroadcastActionReceiver_onReceive getAction:"+action,
						1);
				
				int triger = Integer.parseInt(Constants.SERVICE_INIT_TRIGER);
				
				if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
					triger = Integer.parseInt(Constants.SERVICE_BOOT_TRIGER);
					startPushService(context, triger);
				} else if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
					triger = Integer.parseInt(Constants.SERVICE_POWER_TRIGER);;
					startPushService(context, triger);
				} else if (action.equals(Intent.ACTION_USER_PRESENT)) {
					triger = Integer.parseInt(Constants.SERVICE_USER_TRIGER);;
					startPushService(context, triger);
				} else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
					triger = Integer.parseInt(Constants.SERVICE_CONN_TRIGER);;
					handleConnTriger(context, triger);
				}
			}
		}
	}
	
	private void handleConnTriger(Context context, int triger) {
		NetworkInfo networkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (networkInfo != null) {
        	LogUtil.LogOut(3, LOGTAG, "handleConnTriger Network_Type="+networkInfo.getTypeName()
        			+ ", Network_State = "+networkInfo.getState());
        	
            if (networkInfo.isConnected()) {
            	long lastConnectedTime = SmackConfiguration.getLastConnectedTime();
            	long lostedTime = System.currentTimeMillis() - lastConnectedTime;
            	
            	LogUtil.LogOut(3, LOGTAG, "handleConnTriger lostedTime="+lostedTime 
            			+", lastConnectedTime="+lastConnectedTime);
            	
            	RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_FIND, RecordtoFile.ACTIONT_TIMER,
        				System.currentTimeMillis(), 
        				RecordtoFile.ACTION_SERVICE_START, 
        				System.currentTimeMillis()+1*0*1000,
        				"BroadcastActionReceiver_handleConnTriger:Network isConnected",
        				1);
            	
            	//网络ok，启动服务
            	startPushService(context, triger);
            } else {
            	//网络不ok，不做后续处理
            }	
        } else {
        	RecordtoFile.recordPushInfo(RecordtoFile.REASON_NET_LOST, RecordtoFile.ACTION_DISCONN,
    				System.currentTimeMillis(), 
    				RecordtoFile.ACTIONT_TIMER, 
    				System.currentTimeMillis()+1*0*1000,
    				"BroadcastActionReceiver_handleConnTriger:Network unavailable",
    				1);
        	
        	LogUtil.LogOut(2, LOGTAG, "Network unavailable");
        	//此处不做后续处理，期望通过异常，service自身能发现网络中断而清理资源
        }
	}
	
	// Start the service
	private static void startPushService(Context context, int triger) {
		ServiceManager serviceManager = new ServiceManager(context);
//        serviceManager.setNotificationIcon(R.drawable.notification);
		serviceManager.startService(triger);
	}
	

	private boolean checkAlipayProcess(Context context) {
		//进程还不存在，需要启动
		boolean ret = false;
		
		/*
		if (getProcessInfo(context) > 0) {
			for (Iterator<RunningAppProcessInfo> iterator = procList.iterator(); iterator.hasNext();) {
				RunningAppProcessInfo procInfo = iterator.next();
				if (ALIPAY_PROCESS.equals(procInfo.processName)) {
					LogUtil.LogOut(4, LOGTAG, "checkAlipayProcess find process="+ALIPAY_PROCESS);
					
					ret = true;
					break;
				}
			}
		}
		*/
		
		if (getServiceInfo(context) > 0) {
			for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {
	        	// 获得该Service的组件信息 可能是pkgname/servicename  
	        	ComponentName serviceCMP = runServiceInfo.service;  
	            String serviceName = serviceCMP.getShortClassName(); // service 的类名  
	            String pkgName = serviceCMP.getPackageName(); // 包名  
	            
	            if(serviceName != null && serviceName.equals("NotificationService")) {
	            	LogUtil.LogOut(4, LOGTAG,"checkAlipayProcess find NotificationService now!"+ " and " + pkgName);  
	            	ret = true;
	            	break;
	            }
	        }
		}
		
		return ret;
	}
	
	private int getProcessInfo(Context context) {
		int size = 0;
		try {
			ActivityManager activityManager = 
					(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			procList = activityManager.getRunningAppProcesses();
			
			LogUtil.LogOut(4, LOGTAG, "getProcessInfo="+procList.size());
			size = procList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	
	private int getServiceInfo(Context context) {
		int size = 0;
		try {
			ActivityManager activityManager = 
					(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			runServiceList = activityManager.getRunningServices(maxServiceNum);
			
			LogUtil.LogOut(4, LOGTAG, "getServiceInfo="+runServiceList.size());
			size = runServiceList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return size;
	}
}
