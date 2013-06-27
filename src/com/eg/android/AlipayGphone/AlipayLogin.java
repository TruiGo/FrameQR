/*
 * Copyright (C) 2011 The Alipay Client Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.eg.android.AlipayGphone;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.Main;
import com.alipay.android.client.PromotionHelper;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.manufacture.AppLaunchInterceptor;
import com.alipay.android.client.manufacture.InterceptorFactory;
import com.alipay.android.client.manufacture.LaunchCallback;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.dao.ContactPersonDAO;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.nfd.NFDUtils;
import com.alipay.android.push.ServiceManager;
import com.alipay.android.util.LogUtil;
import com.taobao.statistic.TBS;

public class AlipayLogin extends RootActivity implements LaunchCallback{
	private HallData mHallLogic;
	private Timer mTimer;

	private AlipayApplication application;
	
	private PromotionHelper mPromotionHelper;

	private boolean mLockBack = false;
	private boolean autologin = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mLockBack) {
				//do nothing for now
			} else {
				mExitOnBack = true;
				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				
				finish();
			}
			
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setShowMenu(false);
		
		application = (AlipayApplication) this.getApplicationContext();
		application.putGlobalData("bootFromPaipai", false);

		setContentView(R.layout.welcome_use);

		mHallLogic = application.getHallData();

		mPromotionHelper = new PromotionHelper();

		AppLaunchInterceptor appLaunchInterceptor = InterceptorFactory
				.getInstance()
				.getLaunchInterceptor(
						CacheSet.getInstance(this).getString(Constant.CHANNELS));
		if (appLaunchInterceptor != null) {
			// 执行拦截操作
			appLaunchInterceptor.execLaunch(this, this);
		} else {
			onConfirm();
		}
        // 2秒钟后台还未同步完成跳转到AppHallActivity
        if (null == mTimer) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LogUtil.logAnyTime("step", "doNext:" + this.getClass().getName());    
                    doNext();
                }
            }, 1500);
        }
	}


	private void doInitialize() {
		// Use as promotion.
	    mPromotionHelper.init(getApplicationContext());
        
		application.getAdvertData().requestData(getDataHelper());
	}

	private void updateChannelInfo() {
		String oldChannelInfo = CacheSet.getInstance(this).getString(
				Constant.CHANNELS);
		String curChannelInfo = this.getString(R.string.channels);

		if ("".equals(oldChannelInfo)) {
			oldChannelInfo = "alipay";
		}

		if ("".equals(curChannelInfo)) {
			curChannelInfo = "alipay";
		}

		if (!curChannelInfo.equalsIgnoreCase("alipay")) {
			CacheSet.getInstance(this).putString(Constant.CHANNELS,
					curChannelInfo);
			return;
		}

		if (!oldChannelInfo.equalsIgnoreCase("alipay")
				&& "alipay".equalsIgnoreCase(curChannelInfo)) {
			CacheSet.getInstance(this).putString(Constant.CHANNELS,
					oldChannelInfo);
			return;
		}

		if (oldChannelInfo.equalsIgnoreCase("alipay")
				&& curChannelInfo.equalsIgnoreCase("alipay")) {
			CacheSet.getInstance(this).putString(Constant.CHANNELS, "alipay");
			return;
		}
	}
	
	
	private void initializeAppHall() {
        // update channel info
        updateChannelInfo();
		
		// 浏览器调起悦享拍，不进行业务同步，通过传进来的版本号处理
		if (mHallLogic.getState()<=0) {
			mHallLogic.initializeAppList();
			if(autologin){
			    try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}
			mHallLogic.syncAppStatus();
            doInitialize();
		}
	}

	public void onConfirm() {
		AlipayLogAgent.unInitClient();
		AlipayLogAgent.initClient(getConfigData().getProductId(), getConfigData().getProductVersion(), getConfigData().getClientId(), UUID.randomUUID().toString(), "1");

        // 自动登录判断
        DBHelper db = new DBHelper(this);
        UserInfo userInfo = db.getLastLoginUser(null);
        if (null != userInfo && !"".equals(userInfo.rsaPassword)) {
            autologin = (1 == db.getAutoLogin(userInfo.userAccount, userInfo.type));
        }
        db.close();
        
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// Asynchronously initialize the AppHall.
				initializeAppHall();
			}
		};
		application.executeTask(runnable);

		application.executeTask(new Runnable() {
			@Override
			public void run() {
				if (!Constant.isDebug(AlipayLogin.this)) {
					try {
						TBS.setKey("21093141",
								"767dbfd306277bcffc952d26d518bacd");
						TBS.setChannel(CacheSet.getInstance(AlipayLogin.this)
								.getString(Constant.CHANNELS));
						TBS.syncInit(getApplicationContext());
						TBS.CrashHandler.turnOn();
						
						
						// save productId and productVersion
//						StorageStateInfo.getInstance().putValue(Constants.STORAGE_PRODUCTID,
//								getConfigData().getProductId());
//						StorageStateInfo.getInstance().putValue(
//								Constants.STORAGE_PRODUCTVERSION,
//								getConfigData().getProductVersion());
//						StorageStateInfo.getInstance().putValue(Constants.STORAGE_CLIENTID,
//								getConfigData().getClientId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
//				NFDUtils.getInstance(AlipayLogin.this).restorationBluetoothName();
			}
		});
        NFDUtils.getInstance(AlipayLogin.this).restorationBluetoothName();
        application.executeTask(new Runnable() {
            @Override
            public void run() {
                new ContactPersonDAO().loadAllContacts(AlipayLogin.this);
            }
        });
        LogUtil.logAnyTime("step", "onfinishCreate:" + this.getClass().getName());    
	}


	@Override
	public void onCancel() {
		AlipayApplication application = (AlipayApplication) getApplicationContext();
		application.destroy();
		finish();
	}
	
	private void warmupUrl() {
//    	Utilz.warmupUrl(Constant.getContainerURL(this), this, false);
    	Utilz.warmupUrl(Constant.getAlipayURL(this), this, false);
	}

	/**
	 * 跳转到主界面
	 */
	public void gotoAppHallActivity() {

		Intent intent = null;

		if (autologin && getUserData() == null) {
			intent = new Intent(AlipayLogin.this, Login.class);
			intent.putExtra(Login.LOGINTYPE, Login.LOGINTYPE_AUTOLOGIN);
			
			//悦享拍和触宝特殊处理，没有手势检查
			Uri uri = getIntent().getData();
			if (null != uri/* && null != uri.getQueryParameter(Constant.QRCODE)*/) {
				intent.putExtra(Login.FORCENOPATTERNCHECK, true);
			}
			
			intent.putExtra(Defines.NextActivity, Main.class.getName());
		} else {
	        warmupUrl();
			intent = new Intent(AlipayLogin.this, Main.class);
			
			// 启动push服务——低于2.0的不需要启动
			int expectedVersion = 4;
			if (Integer.valueOf(Build.VERSION.SDK).intValue() > expectedVersion) {
				ServiceManager serviceManager = new ServiceManager(this);
				AlipayDataStore OtherDataSettings = new AlipayDataStore(
						AlipayLogin.this);
				String pushStatus = OtherDataSettings
						.getString(AlipayDataStore.NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS);
				CacheSet.getInstance(this).putString(Constant.PUSH_SWITCH,
						pushStatus);
				if (pushStatus != null && pushStatus.equals("false")) {
					serviceManager.stopService();
				} else {
					serviceManager.startService(0);
				}
			}
		}
		
		Uri uri = getIntent().getData();
		intent.setData(uri);
		
		intent.setAction(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		getMBus().destroy();//初始化引擎
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LogUtil.logAnyTime("step", "next:" + this.getClass().getName());    

		// 只启动一次
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		startActivity(intent);
		finish();
	}

	private boolean mExitOnBack = false;
	private void doNext() {
		//用户单击back退出
		if (mExitOnBack) {
			return ;
		}

		if (Constant.mSafePayIsRunning) {
		    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View toastView = LayoutInflater.from(AlipayLogin.this).inflate(R.layout.alipay_toast_view, null);
                    TextView toastTextView = (TextView) toastView.findViewById(R.id.toastText);
                    toastTextView.setText(R.string.clientPaying);
                    Toast toast = new Toast(AlipayLogin.this);
                    toast.setView(toastView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
			
			finish();
			return;
		}

        gotoAppHallActivity();
	}

}
