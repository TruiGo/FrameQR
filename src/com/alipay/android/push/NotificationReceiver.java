package com.alipay.android.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.data.NotifierInfo;

/** 
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml. 
 */
public final class NotificationReceiver extends BroadcastReceiver {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationReceiver.class);

    private String mAction = "";
    public NotificationReceiver(String action) {
    	//关注的事件类型
    	mAction = action;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	LogUtil.LogOut(4, LOGTAG, "NotificationReceiver.onReceive()...");
    	
        String action = intent.getAction();
        LogUtil.LogOut(3, LOGTAG, "mAction:"+mAction +", action=" + action);

        if (mAction.equals(action)) {
        	NotifierInfo notifierInfo = (NotifierInfo)intent.getParcelableExtra(Constants.PUSH_NOTIFIRE); 
            
        	int value = 0;
            String style = notifierInfo.getStyle();
            if (style!=null && style.length()>0) {
            	value = Integer.valueOf(style).intValue();
            }
            
            switch (value) {
	            case 0:
	            case 1:
	            case 2:
	            case 3:
	            	if (notifierInfo.getTitle()!=null && notifierInfo.getTitle().length()>0 
	            	&& notifierInfo.getContent()!=null && notifierInfo.getContent().length()>0) {
		            	Notifier notifier = new Notifier(context);
		                notifier.notify(notifierInfo);
		            } else {
		            	LogUtil.LogOut(2, LOGTAG, "notificationMessage is invalid!");
		            }
	            	break;
	            	
	            case 4:
	            	/*
	            	if (notificationUri!=null && notificationUri.length()>0) {
	            		Intent cmd = new Intent();
	            		cmd.setClass(context, PushDistributer.class);
	            		cmd.putExtra("pushData", notificationUri);
	            		cmd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            		context.startService(cmd);
		            } else {
		            	LogUtil.LogOut(2, LOGTAG, "notificationUri is invalid!");
		            }
		            */
	            	break;
	            default:
	            	LogUtil.LogOut(2, LOGTAG, "The style is unkonw!");
	            	break;
            }
        }
    }

}
