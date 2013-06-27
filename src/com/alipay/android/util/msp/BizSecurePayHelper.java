/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.alipay.android.util.msp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.PayData;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.ActivityShell;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.net.http.HttpClient;
import com.alipay.android.safepay.AlixDefine;
import com.alipay.android.safepay.MobileSecurePayer;
import com.alipay.android.ui.transfer.TransferRootControllerActivity;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class BizSecurePayHelper{
	static final String TAG = "MobileSecurePayHelper";

	private ProgressDiv mProgress = null;
	RootActivity mcontext = null;
	private DialogInterface.OnClickListener mBtnForCancel = null;
	final String PAY_STYLE = "Pay_Style";
	boolean isexist;
	
	private Handler mHandler ;
	public BizSecurePayHelper(){
		HandlerThread handlerThread = new HandlerThread("safePayCheck");
		handlerThread.start();
		mHandler = new SafePayApkInstallHandler(handlerThread.getLooper());
	}
	
	boolean timeOut = false;
//	boolean cancelInstall = false;
	/**
	 * 支付
	 * 
	 * @param activity
	 * @param context
	 * @param handler
	 * @param progress
	 * @param externToken
	 * @param bizType
	 */
	public String payBizDeal(final RootActivity activity, 
	                         ProgressDiv progress, final String tradeNO,
			final String externToken, final String partnerID,
			final String bizType, String bizSubType) {

		isexist = detectMobile_sp(activity, 0);
		if(!isexist){
			registerInstalledReceiver(activity, tradeNO, externToken, partnerID, bizType, bizSubType);
			timeOut = true;
			try {
				synchronized (TAG) {
					TAG.wait(10 * 60 * 1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (isexist && !timeOut) {
			try {
				BizSecurePayer mp = new BizSecurePayer();
				JSONObject responseJson = mp.pay(tradeNO, externToken, partnerID, bizType, bizSubType, activity);
				if (responseJson != null)
					return responseJson.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		if(timeOut){//取消安装快捷支付
			JSONObject responseJson = new JSONObject();
			try {
				return responseJson.put("timeOut", timeOut).toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void registerInstalledReceiver(final Activity activity, final String tradeNO, final String externToken, final String partnerID,
			final String bizType, String bizSubType) {
		IntentFilter intf = new IntentFilter();
		intf.addAction("android.intent.action.PACKAGE_ADDED");
		intf.addDataScheme("package");
		
		BroadcastReceiver br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equalsIgnoreCase("android.intent.action.PACKAGE_ADDED")) {
					String packagename = intent.getDataString().substring(8);
					if (packagename.equals("com.alipay.android.app")) {
						context.getApplicationContext().unregisterReceiver(AlipayApplication.mSafepayInstallreceiver);
						AlipayApplication.mSafepayInstallreceiver = null;
						isexist = true;
						timeOut = false;
						synchronized (TAG) {
							TAG.notify();
						}
					}
				}
			}
		};
		
		if (AlipayApplication.mSafepayInstallreceiver != null) {
			activity.getApplicationContext().unregisterReceiver(AlipayApplication.mSafepayInstallreceiver);
			AlipayApplication.mSafepayInstallreceiver = null;
		}
		AlipayApplication.mSafepayInstallreceiver = br;
		activity.getApplicationContext().registerReceiver(br, intf);
	}

	/**
	 * @param context
	 * @param style
	 *            0:支付，1：充值
	 * @return
	 */
	public boolean detectMobile_sp(final RootActivity context, final int style) {
		mcontext = context;		

		// get the cacheDir.
		File cacheDir = context.getCacheDir();
		final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
		
		Bundle tBundle = new Bundle();
		tBundle.putInt(PAY_STYLE, style);
		tBundle.putString(Constant.RPF_CACHEPATH, cachePath);
		return detectMobile_sp(context, mHandler, AlipayHandlerMessageIDs.RQF_INSTALL_CHECK, tBundle);
	}
	
	public boolean detectMobile_sp(final RootActivity context,
			final Handler handler, final int what, final Bundle bundle) {
		mcontext = context;
		// get the cacheDir.
		File cacheDir = context.getCacheDir();
		final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
		File akpfile = new File(cachePath);
		boolean isMobile_spExist = isMobile_spExist(context);
		
		if (!isMobile_spExist) {
			mProgress = mcontext.getDataHelper().showProgressDialogWithoutCancelButton(context,
					null, context.getString(R.string.GetingSafepayversion), false, true, mcontext.getDataHelper().cancelListener,
					mcontext.getDataHelper().cancelBtnListener);
			final String updateTime = "UPDATE_TIME";
			final AlipayDataStore settings = new AlipayDataStore(context);
			
			if (!akpfile.exists())
				retrieveApkFromAssets(context, "mobile_sp.apk", cachePath);

			new Thread(new Runnable() {
				public void run() {
					//
					// 检测是否有新的版本。
					PackageInfo apkInfo = getApkInfo(context, cachePath);
					String newApkdlUrl = checkNewUpdate(context,apkInfo);

					//
					// 动态下载
					if (newApkdlUrl != null)
						retrieveApkFromNet(context, newApkdlUrl, cachePath);
					Time nowtime = new Time();
					nowtime.setToNow();
					settings.putString(updateTime, nowtime.format2445());
					// send the result back to caller.
					Message msg = new Message();
					msg.what = what;
					msg.obj = bundle;
					handler.sendMessage(msg);
				}
			}).start();
		}
		// else ok.

		return isMobile_spExist;
	}

	public boolean detectMobile_spUpdate(final RootActivity context,
			final Handler handler, final int what, final String str) {
		mcontext = context;

		// get the cacheDir.
		File cacheDir = context.getCacheDir();
		final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
		boolean isMobile_spExist = isMobile_spExist(context);

		final String updateTime = "UPDATE_TIME";
		final AlipayDataStore settings = new AlipayDataStore(context);

		if (!isMobile_spExist) {
			Bundle tBundle = new Bundle();
			tBundle.putInt(Constant.RPF_UPDATESTATUS, 3);
			tBundle.putString(Constant.RPF_CACHEPATH, cachePath);
			//
			// send the result back to caller.
			Message msg = new Message();
			msg.what = what;
			msg.obj = tBundle;
			handler.sendMessage(msg);
		} else {
			mProgress = mcontext.getDataHelper().showProgressDialogWithoutCancelButton(context,
					null, str, false, true, mcontext.getDataHelper().cancelListener,
					mcontext.getDataHelper().cancelBtnListener);
			new Thread(new Runnable() {
				String url = null;

				public void run() {
					// 检测是否有新的版本。
					String serverVersion = null;
					String lasttime = settings.getString(updateTime);
					PackageInfo apkInfo1 = getPackageVersion(context);
					PackageInfo apkInfo2 = getApkInfo(context, cachePath);
					boolean mustUpdate = false;
					boolean update = false;

					Time oldtime = new Time();
					if (lasttime.length() > 0) {
						oldtime.parse(lasttime);
					}
					if (apkInfo1 != null) {
						try {
							JSONObject resp = sendCheckNewUpdate(context,apkInfo1.versionName);
							// JSONObject resp = sendCheckNewUpdate("1.0.0");
							update = resp.optString("needUpdate")
									.equalsIgnoreCase("true");
							if (update) {
								url = resp.getString("updateUrl");
								mustUpdate = resp.getString("type")
										.equalsIgnoreCase("must");
								serverVersion = resp.getString("latestVersion");
							}
							// else ok.
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					//
					// 动态下载
					Time nowtime = new Time();
					nowtime.setToNow();

					if (update) {
						if (mustUpdate) {
							if (apkInfo2 == null|| serverVersion.compareTo(apkInfo2.versionName) != 0)
								retrieveApkFromNet(context, url, cachePath);

							Bundle tBundle = new Bundle();
							tBundle.putInt(Constant.RPF_UPDATESTATUS, 1);
							tBundle
									.putString(Constant.RPF_CACHEPATH,
											cachePath);
							tBundle.putString(Constant.SERVER_VERSION,
									serverVersion);
							tBundle.putString(Constant.NOW_VERSION,
									apkInfo1.versionName);
							settings
									.putString(updateTime, nowtime.format2445());
							// send the result back to caller.
							Message msg = new Message();
							msg.what = what;
							msg.obj = tBundle;
							handler.sendMessage(msg);
						} else {
							long nowmillis = nowtime.toMillis(true);
							long oldmillis = oldtime.toMillis(true);
							long interval = 30 * 24 * 60 * 60 * 1000l;
							if ((nowmillis - oldmillis) > interval) {
								final CharSequence contentTitle = mcontext
										.getString(R.string.InstallSafePaytitle);
								final String body = mcontext
										.getString(R.string.InstallSafePay);
								new Thread(new Runnable() {
									public void run() {
										boolean ret = retrieveApkFromNet(
												mcontext, url, cachePath);
										if (ret) {

											NotificationManager nm = (NotificationManager) mcontext
													.getSystemService(Context.NOTIFICATION_SERVICE);
											Notification n = new Notification(
													R.drawable.alipay_statusbar_info,
													body,
													System.currentTimeMillis());
											Intent intent = installSafePay(cachePath);
											PendingIntent pi = PendingIntent
													.getActivity(context, 0,
															intent, 0);

											n.flags |= Notification.FLAG_AUTO_CANCEL;
											n.setLatestEventInfo(context,
													contentTitle, body, pi);

											nm.notify(R.string.InstallSafePay,
													n);
										}
									}

								}).start();
								settings.putString(updateTime, nowtime
										.format2445());
							}
							Bundle tBundle = new Bundle();
							tBundle.putInt(Constant.RPF_UPDATESTATUS, 2);
							tBundle
									.putString(Constant.RPF_CACHEPATH,
											cachePath);
							// send the result back to caller.
							Message msg = new Message();
							msg.what = what;
							msg.obj = tBundle;
							handler.sendMessage(msg);
						}
					} else {
						Bundle tBundle = new Bundle();
						tBundle.putInt(Constant.RPF_UPDATESTATUS, 2);
						tBundle.putString(Constant.RPF_CACHEPATH, cachePath);
						// send the result back to caller.
						Message msg = new Message();
						msg.what = what;
						msg.obj = tBundle;
						handler.sendMessage(msg);
					}
					if (mProgress != null) {
						mProgress.dismiss();
					}
				}
			}).start();
		}
		// else ok.
		return isMobile_spExist;
	}

	public void showInstallConfirmDialog(final Context context,
			final String cachePath, final String tip) {
		if(AlipayApplication.mFinishPageFirst)
			AlipayApplication.mFinishPage=true;
        StyleAlertDialog dialog = new StyleAlertDialog(context, R.drawable.infoicon, 
        		context.getResources().getString(R.string.confirm_install_hint), 
        		tip, context.getResources().getString(R.string.Ensure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = installSafePay(cachePath);
						context.startActivity(intent);
					}
				},
				context.getResources().getString(R.string.Cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Activity curContext = (Activity) context;
						String className = curContext.getComponentName().getClassName();
						if (className != null
								&& ActivityShell.class.getCanonicalName()
										.equalsIgnoreCase(className)) {
						    AlipayApplication application = (AlipayApplication) context.getApplicationContext();
						    application.closeProgress();
							if(AlipayApplication.mFinishPageFirst){
		                        AlipayApplication.mFinishPage=false;
		                        AlipayApplication.mFinishPageFirst=false;
								((ActivityShell)context).getEngine().exit();
							}
						}
						
						timeOut = true;
						synchronized (TAG) {
							TAG.notify();
						}
						
						/*else if (className != null && TransferRootControllerActivity.class.getCanonicalName().equalsIgnoreCase(className)) {
							if(((TransferRootControllerActivity)mcontext).getTopController() != null)
								((TransferRootControllerActivity)mcontext).getTopController().closeProgress();
							
							timeOut = true;
							synchronized (TAG) {
								TAG.notify();
							}
						}*/
					}
				}, 
				null);
            dialog.getDialog().setCancelable(false);
            dialog.show();
	}

	public Intent installSafePay(String cachePath) {
		DataHelper.chmod("777", cachePath);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// install the apk.
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + cachePath),
				"application/vnd.android.package-archive");
		return intent;
	}

	public boolean isMobile_spExist(Context context) {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app"))
				return true;
		}

		return false;
	}

	//
	// 捆绑安装
	public boolean retrieveApkFromAssets(Context context, String fileName,
			String path) {
		boolean bRet = false;

		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 获取未安装的APK信息
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            APK文件的路径。如：/sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_META_DATA);
		return apkInfo;
	}

	// 
	// 检查是否有新的版本，如果有，返回apk的下载地址。
	public static String checkNewUpdate(Context context,PackageInfo packageInfo) {
		String url = null;

		try {
			JSONObject resp = sendCheckNewUpdate(context,packageInfo.versionName);
			// JSONObject resp = sendCheckNewUpdate("1.0.0");
			if (resp.getString("needUpdate").equalsIgnoreCase("true")) {
				url = resp.getString("updateUrl");
			}
			// else ok.
		} catch (Exception e) {
			e.printStackTrace();
		}

		return url;
	}

	public static JSONObject sendCheckNewUpdate(Context context,String versionName) {
		JSONObject objResp = null;
		try {
			JSONObject req = new JSONObject();
			req.put(AlixDefine.action, AlixDefine.actionUpdate);

			JSONObject data = new JSONObject();
			data.put(AlixDefine.platform, "android");
			data.put(AlixDefine.VERSION, versionName);
			data.put(AlixDefine.partner, "");

			req.put(AlixDefine.data, data);

			objResp = SendRequest(context,req.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return objResp;
	}

	public static JSONObject SendRequest(Context context,final String content) {
		APHttpClient nM = new APHttpClient(Constant.getSafepayURL(context),
			    context.getApplicationContext());

		//
		JSONObject jsonResponse = null;
		try {
			String response = null;

			synchronized (nM) {
				//
				response = nM.sendSynchronousRequest(content, false);
			}

			jsonResponse = new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		if (jsonResponse != null)
			LogUtil.logOnlyDebuggable(TAG, jsonResponse.toString());

		return jsonResponse;
	}

	// 
	// 动态下载
	public static boolean retrieveApkFromNet(Context context, String strurl,
			String filename) {
		boolean bRet = false;

		try {
			bRet = HttpClient.urlDownloadToFile(strurl, filename, false, null,
				    context.getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bRet;
	}

	//
	// close the progress bar
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// the handler use to receive the install check result.
	class SafePayApkInstallHandler extends Handler{
		public SafePayApkInstallHandler(Looper looper) {
			super(looper);
		}
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case AlipayHandlerMessageIDs.CHECK_UPDATE_FINISH:
					startNext(msg);
					break;

				case AlipayHandlerMessageIDs.RQF_INSTALL_CHECK: {
					//
					closeProgress();
					Bundle tBundle = (Bundle) msg.obj;
					int style = tBundle.getInt(PAY_STYLE);
					String cachePath = tBundle.getString(Constant.RPF_CACHEPATH);
					String tip = "";
					switch (style) {
					case 0:
						tip = mcontext.getResources().getString(
								R.string.confirm_install_pay);
						break;
					case 1:
						tip = mcontext.getResources().getString(
								R.string.confirm_install_charge);
						break;
					}
					showInstallConfirmDialog(mcontext, cachePath, tip);
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public PackageInfo getPackageVersion(Context context) {
		PackageInfo apkInfo = null;
		PackageManager pm = context.getPackageManager();
		try {
			apkInfo = pm.getPackageInfo("com.alipay.android.app",
					PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			apkInfo = null;
		}
		return apkInfo;
	}

	public boolean compareVersion(String version1, String version2) {
		StringTokenizer ver1 = new StringTokenizer(version1, ".");
		StringTokenizer ver2 = new StringTokenizer(version2, ".");
		while (ver1.hasMoreTokens() && ver2.hasMoreTokens()) {
			Integer v1 = new Integer(ver1.nextToken());
			Integer v2 = new Integer(ver2.nextToken());
			if (v1.intValue() > v2.intValue())
				return true;
			if (v2.intValue() > v1.intValue())
				break;
		}
		return false;
	}

	public boolean detectSafepayUpdate(final RootActivity context, int show,
			DialogInterface.OnClickListener btnForCancel) {
		mcontext = context;
		mBtnForCancel = btnForCancel;

		boolean isMobile_spExist = isMobile_spExist(context);
		int updateValue = -1;
		if (!isMobile_spExist) { // 未安装快捷支付
			// 安装快捷支付
			updateValue = 0;
		} else {
			// 需要检查快捷支付的版本
			PackageInfo apkInfo1 = getPackageVersion(context);
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG,
					"detectSafepayUpdate want mSafepayVersion:"
							+ Constant.mSafepayVersion + ", real versionName="
							+ apkInfo1.versionName);
			if (compareVersion(Constant.mSafepayVersion, apkInfo1.versionName)) {
				// 附带的快捷支付版本较高，必须升级
				updateValue = 1;
			} else {
				// 不用处理，现有的包可以用
			}
		}

		installNext(context, updateValue, show);

		if (updateValue >= 0) {
			return true;
		} else {
			return false;
		}
	}
	//宝令用到的安装完成后进入快捷支付check
	public boolean detectSafepayUpdate(final RootActivity context, int show,
			DialogInterface.OnClickListener btnForCancel,  Handler handler) {
		mcontext = context;
		mBtnForCancel = btnForCancel;

		boolean isMobile_spExist = isMobile_spExist(context);
		int updateValue = -1;
		if (!isMobile_spExist) { // 未安装快捷支付
			// 安装快捷支付
			updateValue = 0;

			IntentFilter intf = new IntentFilter();
			intf.addAction("android.intent.action.PACKAGE_ADDED");
			intf.addDataScheme("package");
			AlipayApplication.mPayData.clearAll();
			AlipayApplication.mPayData.setData(
					mcontext, handler, "", "", "",
					"", "");
			BroadcastReceiver br = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
//					Activity mcontext  = (Activity)context;
					if (intent.getAction().equalsIgnoreCase(
							"android.intent.action.PACKAGE_ADDED")) {
						String packagename = intent.getDataString()
								.substring(8);
						if (packagename.equals("com.alipay.android.app")) {
							PayData pd = AlipayApplication.mPayData;
							
							
							 // added by ZhanZhi
					        // 因为条码相关业务免登录模式，所以此token不只仅仅是在login时获取到
							String extern_token = mcontext.getExtToken();

					        if(mcontext.getUserData()!=null){
					        	
					            BaseHelper.checkStatus(pd.getAc(), pd.getHd(), 
					                    AlipayHandlerMessageIDs.SAFEPAY_CHECK_GET_AUTHLIST, 
					                    null, "", Constant.SAFE_GET_AUTHLIST, 1, null);
					        }else{
					            if(extern_token!=null&&extern_token.toString().length()>0)//有账号
					                BaseHelper.safeTokenInit(pd.getAc(), pd.getHd(), null, extern_token.toString(), "otp_account");
					            else
					                BaseHelper.safeTokenInit(pd.getAc(), pd.getHd(), null, null, "otp_noaccount");
					        }
							context
									.getApplicationContext()
									.unregisterReceiver(
											AlipayApplication.mSafepayInstallreceiver);
							AlipayApplication.mPayData.clearAll();
							AlipayApplication.mSafepayInstallreceiver = null;
							AlipayApplication.mFinishPageFirst=false;
							AlipayApplication.mFinishPage=false;
						}
					}
				}

			};
			if (AlipayApplication.mSafepayInstallreceiver != null) {
				mcontext
						.getApplicationContext()
						.unregisterReceiver(
								AlipayApplication.mSafepayInstallreceiver);
				AlipayApplication.mSafepayInstallreceiver = null;
			}
			AlipayApplication.mSafepayInstallreceiver = br;

			mcontext.getApplicationContext().registerReceiver(br, intf);
		} else {
			// 需要检查快捷支付的版本
			PackageInfo apkInfo1 = getPackageVersion(context);
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG,
					"detectSafepayUpdate want mSafepayVersion:"
							+ Constant.mSafepayVersion + ", real versionName="
							+ apkInfo1.versionName);
			if (compareVersion(Constant.mSafepayVersion, apkInfo1.versionName)) {
				// 附带的快捷支付版本较高，必须升级
				updateValue = 1;
			} else {
				// 不用处理，现有的包可以用
			}
		}

		installNext(context, updateValue, show);

		if (updateValue >= 0) {
			return true;
		} else {
			return false;
		}
	}
	private void installNext(Context context, int value, int show) {
		// get the cacheDir.
		File cacheDir = context.getCacheDir();
		final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";

		File t = new File(cachePath);
		if (t.isFile()) {
			t.deleteOnExit();
		}

		Bundle tBundle = new Bundle();
		tBundle.putInt(Constant.RPF_UPDATESTATUS, value);
		tBundle.putString(Constant.RPF_CACHEPATH, cachePath);

		if (value >= 0) {// send the result back to caller.
			Message msg = new Message();
			msg.what = AlipayHandlerMessageIDs.CHECK_UPDATE_FINISH;
			msg.obj = tBundle;
			msg.arg1 = show;
			mHandler.sendMessage(msg);
		}
	}

	private void startNext(Message msg) {
		Bundle ret = (Bundle) msg.obj;
		// int retint = ret.getInt(Constant.RPF_UPDATESTATUS);
		if (msg.arg1 == 1) {
			// 显示提示对话框
			showInstallConfirmDialog(mcontext, ret);
		} else {
			// 不显示对话框
		}

	}

	public void showInstallConfirmDialog(final Activity context,
			final Bundle bundle) {
		final String cachepath = bundle.getString(Constant.RPF_CACHEPATH);

		if(AlipayApplication.mFinishPageFirst)
			AlipayApplication.mFinishPage=true;
		retrieveApkFromAssets(context, "mobile_sp.apk", bundle
				.getString(Constant.RPF_CACHEPATH));

		int retint = bundle.getInt(Constant.RPF_UPDATESTATUS);
		AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
		tDialog.setIcon(R.drawable.infoicon);

		String titleStr = null;
		String bodyStr = null;

		switch (retint) {
		case 0: // 全新安装
			titleStr = context.getResources().getString(
					R.string.confirm_install_hint);
			bodyStr = context.getResources().getString(
					R.string.confirm_install_pay);
			break;

		case 1: // 升级安装
			titleStr = context.getResources().getString(
					R.string.confirm_update_hint);
			String msg = context.getResources().getString(
					R.string.barcode_safepay_update);
			bodyStr = String.format(msg, Constant.mSafepayVersion);
			break;

		default:
			break;
		}
		if (retint >= 0) {
			tDialog.setTitle(titleStr);
			tDialog.setMessage(bodyStr);
			tDialog.setCancelable(false);

			tDialog.setPositiveButton(R.string.Ensure,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							DataHelper.chmod("777", cachepath);

							// install the apk.
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setDataAndType(Uri.parse("file://"
									+ cachepath),
									"application/vnd.android.package-archive");
							context.startActivity(intent);

						}
					});

			tDialog.setNegativeButton(context.getResources().getString(
					R.string.Cancel), mBtnForCancel);

			tDialog.show();
		}
	}

    public ProgressDiv safeTokenInit(final RootActivity activity, final Handler handler,
                                        final ProgressDiv progress, final String externToken, final String bizType) {

        boolean isexist = detectMobile_sp(activity, 0);
        if (isexist) {
            try {
                MobileSecurePayer mp = new MobileSecurePayer();
                boolean bRet = mp.pay(mp.getPayInfo(externToken, bizType), handler, AlipayHandlerMessageIDs.RQF_SAFE_TOKEN_INIT,activity);
                if (bRet) {
                    // show the progress bar to indicate
                    // that we have started paying.
                } else
                    ;
            } catch (Exception ex) {

            }
        } else {
            IntentFilter intf = new IntentFilter();
            intf.addAction("android.intent.action.PACKAGE_ADDED");
            intf.addDataScheme("package");
            BroadcastReceiver br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equalsIgnoreCase(
                            "android.intent.action.PACKAGE_ADDED")) {
                        String packagename = intent.getDataString()
                                .substring(8);
                        if (packagename.equals("com.alipay.android.app")) {
                            BaseHelper.safeTokenInit(activity, handler, null, externToken, bizType);
                            context.getApplicationContext().unregisterReceiver(AlipayApplication.mSafepayInstallreceiver);
                            AlipayApplication.mPayData.clearAll();
                            AlipayApplication.mSafepayInstallreceiver = null;
                            AlipayApplication.mFinishPage=false;
                            AlipayApplication.mFinishPageFirst=false;
                        }
                    }
                }

            };
            if (AlipayApplication.mSafepayInstallreceiver != null) {
                activity
                        .getApplicationContext()
                        .unregisterReceiver(
                                AlipayApplication.mSafepayInstallreceiver);
                AlipayApplication.mSafepayInstallreceiver = null;
            }
            AlipayApplication.mSafepayInstallreceiver = br;

            activity.getApplicationContext().registerReceiver(br, intf);
        }
        return progress;
    }
}
