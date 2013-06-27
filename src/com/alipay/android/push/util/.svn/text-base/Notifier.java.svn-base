package com.alipay.android.push.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alipay.android.client.PushDistributer;
import com.alipay.android.push.data.MsgInfo;
import com.alipay.android.push.data.NotifierInfo;
import com.eg.android.AlipayGphone.R;

/** 
 * This class is to notify the user of messages with NotificationManager.
 */
public class Notifier {

    private static final String LOGTAG = LogUtil.makeLogTag(Notifier.class);

    private static final Random random = new Random(System.currentTimeMillis());

    private Context context;

    private SharedPreferences settings;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;

        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        
        // get setting about notification: time, effect and state
        SharedPreferences sharedPrefs = context.getSharedPreferences(
        		Constants.PUSH_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String settingsName = sharedPrefs.getString(Constants.NOTIFICATION_SETTING_NAME, "");
         
        this.settings = context.getSharedPreferences(settingsName, Context.MODE_PRIVATE);
    }

    public void notify(NotifierInfo notifierInfo) {
    	LogUtil.LogOut(4, LOGTAG, "notify()...");

        // 是否显示通知的控制
        if (isNotificationEnabled()) {
            // Show the toast
            if (isNotificationToastEnabled()) {
                Toast.makeText(context, notifierInfo.getContent(), Toast.LENGTH_LONG).show();
            }

            // Notification
            Notification notification = new Notification();
//            notification.icon = getNotificationIcon();
            notification.icon=R.drawable.appicon;
            notification.defaults = Notification.DEFAULT_LIGHTS;
            
            if (isNotificationTime()) {
	            if (isNotificationSoundEnabled()) {
	                notification.defaults |= Notification.DEFAULT_SOUND;
	            }
	            if (isNotificationVibrateEnabled()) {
	                notification.defaults |= Notification.DEFAULT_VIBRATE;
	            }
            }
            
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            
            //当我们点击通知时显示的内容
            notification.tickerText = notifierInfo.getContent();
            MsgInfo msgInfo = notifierInfo.getMsgInfo();

            Intent intent = new Intent();
            intent.setClass(context, PushDistributer.class);
            intent.putExtra(Constants.NOTIFICATION_ID, notifierInfo.getId());
            intent.putExtra(Constants.NOTIFICATION_TITLE, notifierInfo.getTitle());
            intent.putExtra(Constants.NOTIFICATION_MESSAGE, notifierInfo.getContent());
            intent.putExtra(Constants.NOTIFICATION_PUSH_DATA, notifierInfo.getUri());
            intent.putExtra(Constants.NOTIFICATION_MISSION_ID, msgInfo.getMissionId());
            intent.putExtra(Constants.NOTIFICATION_MSG_ID, msgInfo.getPerMsgId());
            intent.putExtra(Constants.NOTIFICATION_PUB_MSG_ID, msgInfo.getPubMsgId());
            intent.setAction(Long.toString(System.currentTimeMillis()));
            
            LogUtil.LogOut(4, LOGTAG, "notify()... pushData="+notifierInfo.getUri()
            		+ ", curMissionId="+msgInfo.getMissionId()
            		+ ", curMsgId="+msgInfo.getPerMsgId()
            		+ ", curPubMsgId="+msgInfo.getPubMsgId());
            
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            RemoteViews contentView = new RemoteViews(context.getPackageName(),R.layout.alipay_push_notification); 
            contentView.setImageViewResource(R.id.push_app_image,R.drawable.appicon);
            contentView.setTextViewText(R.id.push_msg_title, notifierInfo.getTitle());
            
            Date currentTime = new Date();
    	    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    	    String dateString = formatter.format(currentTime);
            contentView.setTextViewText(R.id.push_msg_time, dateString);
            
            contentView.setTextViewText(R.id.push_msg_body, notifierInfo.getContent());
            notification.contentView = contentView; 
            
            PendingIntent contentIntent = null;

//            if (style!=null && style.equals(Constants.ACTION_NOTIFICATION_CLICKED)) {
//            	contentIntent = PendingIntent.getActivity(context, 0, 
//            			intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            } else if (style!=null && style.equals(Constants.ACTION_SHOW_NOTIFICATION)) {
//            	contentIntent = PendingIntent.getActivity(context, 0, null, 0);
//            }

            contentIntent = PendingIntent.getService(context, 0, 
            		intent, PendingIntent.FLAG_UPDATE_CURRENT);

        	notification.contentIntent = contentIntent; 
//        	notification.setLatestEventInfo(context, title, message, contentIntent);
        	
            LogUtil.LogOut(3, LOGTAG, "notificationManager() notify is called!");
            notificationManager.notify(random.nextInt(), notification);
        } else {
        	LogUtil.LogOut(2, LOGTAG, "Notificaitons disabled.");
        }
    }

    private int getNotificationIcon() {
    	String icon = settings.getString(Constants.NOTIFICATION_ICON, "0");
        return Integer.parseInt(icon);
    }

    public boolean isNotificationEnabled() {
    	String enable = settings.getString(Constants.SETTINGS_NOTIFICATION_ENABLED, "true");
    	return enable.equals("true");
    }
    
    public boolean isConfigEnabled() {
    	String enable = settings.getString(Constants.SETTINGS_NOTIFICATION_ENABLED, "true");
    	return enable.equals("true");
    }

    private boolean isNotificationSoundEnabled() {
    	String enable = settings.getString(Constants.SETTINGS_SOUND_ENABLED, "false");
    	return enable.equals("true");
    }

    private boolean isNotificationVibrateEnabled() {
    	String enable = settings.getString(Constants.SETTINGS_VIBRATE_ENABLED, "false");
    	return enable.equals("true");
    }
    
    public int getNotificationStartTime() {
    	String start = settings.getString(Constants.SETTINGS_TIME_STARTTIME, Constants.NOTIFICATION_DEAFULT_STARTTIME);
        return Integer.parseInt(start);
    }
    
    private int getNotificationEndTime() {
    	String end = settings.getString(Constants.SETTINGS_TIME_ENDTIME, Constants.NOTIFICATION_DEAFULT_ENDTIME);
        return Integer.parseInt(end);
    }

    private boolean isNotificationToastEnabled() {
    	String toast = settings.getString(Constants.SETTINGS_TOAST_ENABLED, "false");
    	return toast.equals("true");
    }
    
    public boolean isNotificationTime() {
    	int settingStart = getNotificationStartTime();
    	int settingEnd = getNotificationEndTime();
    	
//    	int curHour = Integer.parseInt(getHour());
    	int curHour = Integer.valueOf(getHour()).intValue();
    	
    	LogUtil.LogOut(4, LOGTAG, "isNotificationTime() settingStart="+settingStart 
    			+", settingEnd="+settingEnd +", curHour="+curHour);
    	
    	if(curHour < settingEnd && curHour >= settingStart) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
    * 得到现在的小时
    */
    public static String getHour() {
	    Date currentTime = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formatter.format(currentTime);
	    String hour;
	    hour = dateString.substring(11, 13);
	    return hour;
    }
}
