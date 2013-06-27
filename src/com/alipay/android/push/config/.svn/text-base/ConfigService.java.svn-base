package com.alipay.android.push.config;


import com.alipay.android.push.NotificationReceiver;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.record.RecordtoFile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

public class ConfigService extends Service {
	private static final String LOGTAG = LogUtil.makeLogTag(ConfigService.class);
	public static final String SERVICE_NAME = "com.alipay.android.push.config.ConfigService";
	
	private Context mContext = null;
	private BroadcastReceiver notificationReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.LogOut(3, LOGTAG, "onCreate Enter... ");
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_UNKNOWN, RecordtoFile.ACTION_SERVICE_START,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_SERVICE_START, 
				System.currentTimeMillis()+1*10*1000,
				"ConfigService_onCreate Enter",
				1);
		
		mContext = this;
		registerNotificationReceiver();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.LogOut(3, LOGTAG, "onStartCommand Received start id " + startId + ", intent: " + intent);
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_UNKNOWN, RecordtoFile.ACTION_SERVICE_START,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_SERVICE_START, 
				System.currentTimeMillis()+1*10*1000,
				"ConfigService_onStartCommand: intent="+intent,
				1);
		
		final ConfigReqHelper cfgReqHelper = new ConfigReqHelper();
        new Thread(new Runnable() {
            public void run() {
            	cfgReqHelper.request(mContext);
            }
        }).start();
        
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		LogUtil.LogOut(4, LOGTAG, "onDestroy()...");
		
		unregisterNotificationReceiver();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.LogOut(4, LOGTAG, "onUnbind()...");
		return true;
	}
	
	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}
	
	private void registerNotificationReceiver() {
		notificationReceiver = new NotificationReceiver(Constants.ACTION_SHOW_PUBMESSAGE);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SHOW_PUBMESSAGE);
		registerReceiver(notificationReceiver, filter);
	}
	
	private void unregisterNotificationReceiver() {
		try {
			unregisterReceiver(notificationReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		notificationReceiver = null;
	}

}
