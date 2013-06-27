package com.alipay.android.client;

import com.alipay.android.client.constant.Constant;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class PatternMonitor {
	
	private boolean mStarted = false;
	private boolean iOnTop = true;
	private AlipayApplication mAppContext = null;
	private BroadcastReceiver mScreenChangeReceiver = null;

	public PatternMonitor(AlipayApplication context) {
		mAppContext = context;
	}
	
	public void start() {
		if (mStarted || null == mAppContext) {
			return ;
		}
		
		mStarted = true;
		
		final ActivityManager activityManager = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE); 
//		new Thread() {
//			@Override
//			public void run() {
//				// 看门狗, 不停的查看当前activity任务栈的栈顶
//				while (mStarted) {
//					// 首先获取到最上面的任务栈, get(0) 获取到任务栈栈顶的activity
//					String packname = activityManager.getRunningTasks(1).get(0).topActivity
//							.getPackageName();
//					boolean onTopCurrent = packname.equalsIgnoreCase(mContext.getPackageName());
//					if (!iOnTop && onTopCurrent)
//					{
//						startLockActivity();
//						Log.i("PatternMonitor", "getRunningTasks");
//					}
//					iOnTop = onTopCurrent;
//
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();

	    IntentFilter screenMonitorFilter = new IntentFilter();
	    screenMonitorFilter.addAction(Intent.ACTION_SCREEN_ON);
	    
		if (null == mScreenChangeReceiver) {
			mScreenChangeReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String packname = activityManager.getRunningTasks(1).get(0).topActivity
							.getPackageName();
					boolean onTopCurrent = packname.equalsIgnoreCase(mAppContext.getPackageName());
					if (onTopCurrent
							&& intent.getAction().equalsIgnoreCase(
									Intent.ACTION_SCREEN_ON))
						startLockActivity();
						Log.i("PatternMonitor", "mScreenChangeReceiver");
				}

			};
		}

		mAppContext.registerReceiver(mScreenChangeReceiver, screenMonitorFilter);
	}
	
	public void startLockActivity() {
		if (PatternLockActivity.PATTERNLOCKED) {
			return ;
		}
		
		if (null != mAppContext && mStarted && !Constant.mSafePayIsRunning) {
			Activity currentActivity = mAppContext.getActivity();
			if (null != currentActivity) {
				Intent intent = new Intent(PatternLockActivity.ACTION_PATTERNLOCK);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				currentActivity.startActivity(intent);
			}
		}
	}

	public void stop() {
		if (mStarted) {
			mStarted = false;
			mAppContext.unregisterReceiver(mScreenChangeReceiver);
		}
	}
}
