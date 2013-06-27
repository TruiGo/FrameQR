
package com.alipay.android.push.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.eg.android.AlipayGphone.R;


/**
 * Provides control of power-related settings from a widget.
 */
public class TestWidgetProvider extends AppWidgetProvider {
    static final String TAG = "TestWidgetProvider";
    
    private static int mCountTime = 0;
    private int mPushLinkServiceState = -1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
    	//count_time = 0;
    	Log.d(TAG, "onUpdate: count_time = " + mCountTime);  
    	super.onUpdate(context, appWidgetManager, appWidgetIds);  
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    	//count_time = 0;
    	Log.d(TAG, "onDeleted: count_time = " + mCountTime); 
    	super.onDeleted(context, appWidgetIds);  
    }

    public static void updateWidgets() {
    	//
    }


    /**
     * Creates PendingIntent to notify the widget of a button click.
     *
     * @param context
     * @param appWidgetId
     * @return
     */
    private static PendingIntent getLaunchPendingIntent(Context context, int appWidgetId,
            int buttonId) {
        Intent launchIntent = new Intent();
        launchIntent.setClass(context, TestWidgetProvider.class);
        launchIntent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        launchIntent.setData(Uri.parse("custom:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0 /* no requestCode */,
                launchIntent, 0 /* no flags */);
        return pi;
    }
    
    @Override
    public void onEnabled(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, TestWidgetProvider.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onDisabled(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
        		new ComponentName(context, TestWidgetProvider.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Receives and processes a button pressed intent or state change.
     *
     * @param context
     * @param intent  Indicates the pressed button.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d(TAG, "onReceive: Intent = " + intent 
    			+ " TimeTick = " + intent.getIntExtra("TimeTick", 0));  
    	
    	//1. 获取RemoteViews对象  
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.push_status_widget_layout); 
        
        //2. 获得组建的完整名字  
        ComponentName thisWidget = new ComponentName(context, TestWidgetProvider.class);    
        AppWidgetManager manager = AppWidgetManager.getInstance(context);  
    	
    	String action = intent.getAction();  
    	int timeTick = intent.getIntExtra("TimeTick", 0);
    	int pushStatus = intent.getIntExtra("PushStatus", -1);
    	
        if (Intent.ACTION_TIME_CHANGED.equals(action) && timeTick == 1) {  
        	mCountTime = mCountTime +1;
            //3. 显示新消息提醒  
            views.setTextViewText(R.id.count_txt, String.valueOf(mCountTime));  
        }  
        else  {
        	switch (pushStatus) {
	            case 1:
	                views.setImageViewResource(R.id.ind_status, R.drawable.appwidget_settings_ind_on_c);
	                mCountTime = 0;
	                mPushLinkServiceState = 1;
	                break;
	            case 0:
	                views.setImageViewResource(R.id.ind_status, R.drawable.appwidget_settings_ind_mid_c);
	                mCountTime = 0;
	                mPushLinkServiceState = 0;
	                break;
	            default:
	            	views.setImageViewResource(R.id.ind_status, R.drawable.appwidget_settings_ind_off_c);
	            	mCountTime = 0;
	            	mPushLinkServiceState = -1;
	                break;
        	}
    	
        	//3. 显示push状态  
        	views.setTextViewText(R.id.count_txt, String.valueOf(timeTick));  
        }
        
        //执行内容更新  
        manager.updateAppWidget(thisWidget, views);
        
        super.onReceive(context, intent);
    }

}
