package com.alipay.android.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipayDataCacheHelper;
import com.alipay.android.client.Login;
import com.alipay.android.client.Main;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.ShareAppListAdapter;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.dataprovider.ContactProvider;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.nfd.NFDUtils;
import com.alipay.android.safepay.MobileSecureCheck;
import com.alipay.android.safepay.MobileSecurePayHelper;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.util.LogUtil;
import com.alipay.android.util.msp.BizSecurePayHelper;
import com.eg.android.AlipayGphone.R;
import com.taobao.statistic.TBS;

@SuppressWarnings("deprecation")
public class BaseHelper {
	public static boolean tabOnKeyDown(RootActivity activity, int keyCode,
			KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				exitProcess(activity);
				return true;
			default:
				break;
			}
		}
		return false;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void ReLogin(int iconRes, String titleText,
			String MessageText, Activity context) {
		if (context != null && !context.isFinishing()) {
			AlipayApplication application = (AlipayApplication) context
					.getApplication();
			application.getDataHelper().showDialog(context, iconRes, titleText,
					MessageText, context.getString(R.string.Ensure),
					new MyOnClickListener(context), null, null, null, null);
			/*
			 * StyleAlertDialog alertDialog = new StyleAlertDialog(context,
			 * iconRes, titleText, MessageText,
			 * context.getString(R.string.Ensure), new
			 * MyOnClickListener(context), null, null, null);
			 * alertDialog.show();
			 */
		}
	}

	public static String setStarForPhoneNumber(String phone) {
		phone = trimMobilePhone(phone);
		if (phone.length() > 0) {
			return phone.substring(0, 3) + "****" + phone.substring(7);
		} else {
			return "";
		}
	}

	public static String setUnloginAccount(String account) {
		
		
		if (account==null||account.equals("")) {
			return "";
		}
		
		account = account.replace(" ", "");
		String ret = "";
		
		boolean isPhoneNum = false;
		try {

			Double.parseDouble(account);
			if (account.length() == 11) {
				isPhoneNum = true;
			}
		} catch (NumberFormatException e) {
		}
		
		if (!isPhoneNum&&account.indexOf("@")<0) {
			return Utilz.getSecuredNikName(account);
		}
		
		if (isPhoneNum) {
			ret = setStarForPhoneNumber(account);
		}else{
			ret = setStarForEmail(account);
		}
		return ret;
	}

	public static String setStarForEmail(String email) {
		int index = email.indexOf("@");
		if (index < 0 ) {
			return "";
		}
		String headEmail = email.substring(0, index);
		String endEmail = email.substring(index);

		
		if (headEmail != null && headEmail.length() > 3) {
			String headThree = headEmail.substring(0, 3);
			headEmail = headThree+"***";
		} else if (headEmail != null && headEmail.length() <= 3&&headEmail.length() >= 1) {
			headEmail = headEmail.substring(0,1)+"***";
		}
		return headEmail + endEmail;
	}

	public static class MyOnClickListener implements
			DialogInterface.OnClickListener {
		Activity context1;

		public MyOnClickListener(Activity context1) {
			this.context1 = context1;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent tIntent = new Intent(context1, Login.class);
			context1.startActivityForResult(tIntent,
					Constant.REQUEST_LOGIN_BACK);
			context1.finish();
		}
	}

	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics tDisplayMetrics = new DisplayMetrics();
		Display tDisplay = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		tDisplay.getMetrics(tDisplayMetrics);

		return tDisplayMetrics;
	}

	public static void showDialog(Context context, String strTitle,
			String strText, int icon) {
		try {
			StyleAlertDialog alertDialog = new StyleAlertDialog(context, icon,
					strTitle, strText, context.getString(R.string.Ensure),
					null, null, null, null);
			alertDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StyleAlertDialog showDialog(Context context, int type,
			String strText, final String ok, final String cancel,
			ArrayList<String> params, final ExpAppRuntime engine) {
		try {

			int iconID = 0;
			String title = "";

			// AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
			switch (type) {
			case 0:
				iconID = R.drawable.ok;
				title = context.getString(R.string.Ok);

				// tDialog.setIcon(R.drawable.ok);
				// tDialog.setTitle(context.getString(R.string.Ok));
				break;
			case 1:
				iconID = R.drawable.infoicon;
				title = context.getString(R.string.Hint);

				// tDialog.setIcon(R.drawable.infoicon);
				// tDialog.setTitle(context.getString(R.string.Hint));
				break;
			case 2:
				iconID = R.drawable.erroricon;
				title = context.getString(R.string.Error);

				// tDialog.setIcon(R.drawable.erroricon);
				// tDialog.setTitle(context.getString(R.string.Error));
				break;
			}
			if (strText == null)
				strText = "";
			// UBBPaser ubbPaser = new UBBPaser(strText);
			// View view = LayoutInflater.from(context).inflate(R.layout.dialog,
			// null);
			// final TextView message = (TextView)
			// view.findViewById(R.id.message);// new TextView(context);
			// final SpannableString s = new
			// SpannableString(Html.fromHtml(ubbPaser.parse2Html()));
			// message.setText(s);
			// message.setTextColor(0xffffffff);
			// message.setMovementMethod(LinkMovementMethod.getInstance());
			// tDialog.setView(view);
			// tDialog.setCancelable(false);//禁止硬返回

			String left = context.getString(R.string.Ensure);
			if (params != null && params.size() > 0) {
				left = params.get(0);
			}
			String right = context.getString(R.string.Cancel);
			if (params != null && params.size() > 1) {
				right = params.get(1);
			}

			// tDialog.setPositiveButton(left, new Dialog.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// if (ok != null && ok.length() > 0) {
			// if (engine.getCurrentPage() == null)
			// engine.destroyEngine();
			// else
			// engine.getCurrentPage().interpreter("dialog ok", ok);
			// }
			// }
			// });
			// if (cancel != null && cancel.length() > 0 && right.length() > 0)
			// tDialog.setNegativeButton(right, new Dialog.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// if (engine.getCurrentPage() == null)
			// engine.destroyEngine();
			// else
			// engine.getCurrentPage().interpreter("dialog cancel", cancel);
			// }
			// });

			StyleAlertDialog dialog = null;

			dialog = new StyleAlertDialog(context, iconID, title, strText,
					left, new Dialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (ok != null && ok.length() > 0) {
								if (engine.getCurrentPage() == null)
									engine.exit();
								else
									engine.getCurrentPage().interpreter(
											"dialog ok", ok);
							}
						}
					}, null, null, null);

			if (cancel != null && cancel.length() > 0 && right.length() > 0) {

				dialog = new StyleAlertDialog(context, iconID, title, strText,
						left, new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (ok != null && ok.length() > 0) {
									if (engine.getCurrentPage() == null)
										engine.exit();
									else
										engine.getCurrentPage().interpreter(
												"dialog ok", ok);
								}
							}
						}, right, new Dialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (engine.getCurrentPage() == null)
									engine.exit();
								else
									engine.getCurrentPage().interpreter(
											"dialog cancel", cancel);
							}
						}, null);
			}

			if (dialog != null) {
//				dialog.getDialog().setCancelable(false);
				dialog.show();
			}

			return dialog; // tDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void showDesktop(Activity context) {
		context.finish();
	}

	public static void exitProcess(RootActivity context) {
		AlipayApplication application = (AlipayApplication) context
				.getApplicationContext();
		application.getDataHelper()
				.showDialog(
						context,
						R.drawable.infoicon,
						context.getResources().getString(
								R.string.WarngingString),
						context.getResources().getString(
								R.string.ExitDialogInfo),
						context.getResources().getString(R.string.Yes),
						new exitProcessOnClickListener(context),
						context.getResources().getString(R.string.No), null,
						null, null);
	}

	public static void exitProcessSilently(Activity context) {
		try {
			AlipayApplication application = (AlipayApplication) context
					.getApplicationContext();
			if (application.getUserData() != null) {
				exitProcessOnClickListener.sendExit(context);
			} else {
				finishRes(context);
			}

			NFDUtils.getInstance(context).relaseInstance();
			if (!Constant.isDebug(context)) {
				TBS.uninit();
			}
			
			AlipayLogAgent.uploadLog(context);
			Constants.LOG_ACCOUNT = 0;// 清除记录埋点的次数
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class exitProcessOnClickListener implements
			DialogInterface.OnClickListener {
		Activity context1;

		public exitProcessOnClickListener(Activity context1) {
			this.context1 = context1;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			exitProcessSilently(context1);
		}

		// Asynchronous
		public static void clearUserCache(final Activity context) {
			AlipayApplication application = (AlipayApplication) context
					.getApplicationContext();
			final String sessionId = application.getConfigData().getSessionId();
			final String clientId = application.getConfigData().getClientId();
			UserData userData = application.getUserData();
			final String strUserId = userData == null ? "" : userData
					.getAccountName();
	        CookieManager.getInstance().removeAllCookie();
	        CookieManager.getInstance().removeSessionCookie();

			new Thread(new Runnable() {
				public void run() {
					APHttpClient httpClient = new APHttpClient(
							Constant.getAlipayURL(context), context);
					//
					try {
						JSONObject jsonRequest = new JSONObject();
						jsonRequest.put(Constant.RQF_CLIENT_ID, clientId); // put
																			// client
																			// id
																			// from
																			// default
						jsonRequest.put(Constant.RQF_OPERATION_TYPE,
								Constant.OP_LOGOUT);
						jsonRequest.put(Constant.RPF_SESSION_ID, sessionId);
						httpClient.sendSynchronousRequest(jsonRequest
								.toString());

						// 清除户端保留的cache
						String dir = Constant.FILE_PATH
								+ context.getPackageName() + "/files/"
								+ strUserId + "/" + Constant.accountDir;
						// 账户信息
						AlipayDataCacheHelper.clearCacheData(dir);
						// 交易记录
						dir = Constant.FILE_PATH + context.getPackageName()
								+ "/files/" + strUserId + "/"
								+ Constant.tradeListDir;
						AlipayDataCacheHelper.clearCacheData(dir);
						// 提醒消息
						dir = Constant.FILE_PATH + context.getPackageName()
								+ "/files/" + strUserId + "/"
								+ Constant.tradeRemindDir;
						AlipayDataCacheHelper.clearCacheData(dir);

						// Constant.mClientIsRunning = false;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}

		public static void sendExit(final Activity context) {
			clearUserCache(context);
			finishRes(context);
		}
	}

	public static void finishRes(final Activity context) {
		//
		if (context.getClass() == Main.class) {
			context.finish();
		} else {
			Intent intent = new Intent(context, Main.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Constant.EXIT, true);
			context.startActivity(intent);
			context.finish();
		}

		AlipayApplication application = (AlipayApplication) context
				.getApplicationContext();
		application.destroy();
		//清除缓存的券图片文件
		Helper.cleanFiles(context, Helper.getDiskCacheDir(context, Voucher.VOUCHERICONDIR));
		LogUtil.logAnyTime("BaseHelper", "BaseHelper invoke finishRes()");
		LephoneConstant.s_isLephone = false;
		ContactProvider.getInstance().setLoadingFinish(false);

		if (AlipayApplication.mSafepayInstallreceiver != null) {
			application
					.unregisterReceiver(AlipayApplication.mSafepayInstallreceiver);
			AlipayApplication.mSafepayInstallreceiver = null;
		}

		// 彻底结束应用，则reset标识位mClientIsRunning
		Constant.mClientIsRunning = false;
		Constant.isCommendCouponShowed = false;
		// Constant.mSafePayIsRunning = false;
	}

	public static void doPhoneContact(Activity context) {
		try {
			if (context instanceof RootActivity) {
				((RootActivity) context).countMeNotTemporary(true);
			}

			if (LephoneConstant.isLephone()) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("vnd.android.cursor.item/phone_v2");
				context.startActivityForResult(intent,
						Constant.REQUEST_LEPHONE_CONTACT);
			} else {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				if (Integer.parseInt(Build.VERSION.SDK) > 4)// 2.x，sdk版本
				{
					// intent.setData(Uri.parse("content://com.android.contacts/contacts"));
					intent.setData(ContactsContract.Contacts.CONTENT_URI);
				} else {
					intent.setData(Contacts.People.CONTENT_URI);
				}

				context.startActivityForResult(intent, Constant.REQUEST_CONTACT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getContactName(Uri data, Activity context) {
		Uri person = null;// ContentUris.withAppendedId(People.CONTENT_URI,
							// ContentUris.parseId(data));
		String version = Build.VERSION.SDK;
		try {
			if (Integer.parseInt(version) > 4)// 2.x，sdk版本
			{
				person = ContentUris
						.withAppendedId(
								ContactsContract.Contacts.CONTENT_URI
								/*
								 * Uri.parse(
								 * "content://com.android.contacts/contacts")
								 */, ContentUris.parseId(data));
			} else// 1.6以下SDK
			{
				person = ContentUris.withAppendedId(People.CONTENT_URI,
						ContentUris.parseId(data));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		String hintName = "";

		Cursor cur = context.managedQuery(person, null, null, null, null);
		try {
			if (cur.moveToFirst()) {
				int nameIndex = 0;

				nameIndex = cur.getColumnIndex(Contacts.Phones.DISPLAY_NAME);
				if (nameIndex != -1) {
					hintName = cur.getString(nameIndex);
				}
			}
		} catch (Exception e) {
			//
		} finally {
			cur.close();
			context.stopManagingCursor(cur);
		}

		return hintName;
	}

	public static ArrayList<String> getContactPhoneNo(Uri data, Activity context) {
		Uri person = null;// ContentUris.withAppendedId(People.CONTENT_URI,
							// ContentUris.parseId(data));
		String version = Build.VERSION.SDK;
		ArrayList<String> tArray = new ArrayList<String>();
		try {
			if (Integer.parseInt(version) > 4)// 2.x，sdk版本
			{
				person = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						ContentUris.parseId(data));
			} else// 1.6以下SDK
			{
				person = ContentUris.withAppendedId(People.CONTENT_URI,
						ContentUris.parseId(data));
			}
		} catch (Exception e) {
			return tArray;
		}

		// String number = context.getString(R.string.NoContactPhoneNo);
		try {
			Cursor cur = context.managedQuery(person, null, null, null, null);
			try {
				if (cur.moveToFirst()) {
					int i = 0;
					if (Integer.parseInt(version) > 4) {
						String contactId = cur.getString(cur
								.getColumnIndex("_id"));
						if (contactId != null && !contactId.equals("")) {
							// Cursor phones =
							// context.getContentResolver().query(Uri.parse("content://com.android.contacts/data/phones"),null,"contact_id"
							// +" = "+ contactId + " and " + "data2" + "=" + "2"
							// ,null, null);
							Cursor phones = context
									.getContentResolver()
									.query(Uri
											.parse("content://com.android.contacts/data/phones"),
											null,
											"contact_id" + " = " + contactId,
											null, null);
							while (phones.moveToNext()) {
								tArray.add(trimMobilePhone(phones
										.getString(phones
												.getColumnIndex("data1"))));
							}
							phones.close();
						}

					} else {
						i = cur.getColumnIndex(Contacts.Phones.NUMBER);
						if (i != -1) {
							tArray.add(trimMobilePhone(cur.getString(i)));
						}
					}

				}
			} catch (Exception e) {
				if (Integer.parseInt(version) > 4) {
					int i = cur.getColumnIndex(Contacts.Phones.NUMBER);
					if (i != -1) {
						tArray.add(trimMobilePhone(cur.getString(i)));
					}
				}
			} finally {
				cur.close();
				context.stopManagingCursor(cur);
			}
		} catch (Exception e) {

		}
		return tArray;
	}

	public static String trimMobilePhone(String number) {

		number = number.replace("-", "");
		number = number.replace(" ", "");
		int n = number.length();
		if (n > 11) {
			number = number.substring(n - 11);
		}

		return number;
	}

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
	public static ProgressDiv payDeal(final RootActivity activity,
			Handler handler, ProgressDiv progress, final String tradeNO,
			final String externToken, final String partnerID,
			final String bizType, String bizSubType) {
		// 调起快捷支付需要刷新交易记录列表
		AlipayApplication application = (AlipayApplication) activity
				.getApplicationContext();
		application.setRecordsRefresh(true);
		application.setSafePayCalled(true);
		UserData mUserdata = application.getUserData();
		if (mUserdata != null) {
			mUserdata.resetStatus();
		}

		if (bizSubType != null)
			bizSubType = bizSubType.toLowerCase();
		MobileSecurePayHelper mph = new MobileSecurePayHelper();

		UserData mUserData = activity.getUserData();
		if (mUserData != null) {
			mUserData.resetStatus();
		}
		return mph.payDeal(activity, handler, progress, tradeNO, externToken,
				partnerID, bizType, bizSubType);
	}

	/**
	 * 同步的调用方式
	 * 
	 * @param activity
	 * @param progress
	 * @param tradeNO
	 * @param externToken
	 * @param partnerID
	 * @param bizType
	 * @param bizSubType
	 * @return
	 */
	public static String payBizDeal(final RootActivity activity,
			ProgressDiv progress, final String tradeNO,
			final String externToken, final String partnerID,
			final String bizType, String bizSubType) {
		AlipayApplication application = (AlipayApplication) activity
				.getApplicationContext();
		application.setRecordsRefresh(true);
		application.setSafePayCalled(true);
		UserData mUserdata = application.getUserData();
		if (mUserdata != null) {
			mUserdata.resetStatus();
		}

		if (bizSubType != null)
			bizSubType = bizSubType.toLowerCase();
		BizSecurePayHelper mph = new BizSecurePayHelper();
		return mph.payBizDeal(activity, progress, tradeNO, externToken,
				partnerID, bizType, bizSubType);
	}

	public static ProgressDiv safeTokenInit(final RootActivity activity,
			Handler handler, ProgressDiv progress, final String externToken,
			final String bizType) {
		MobileSecurePayHelper mph = new MobileSecurePayHelper();
		return mph.safeTokenInit(activity, handler, progress, externToken,
				bizType);
	}

	// 快捷支付相关状态查询，为了以后扩展
	public static ProgressDiv checkStatus(final RootActivity activity,
			Handler handler, final int myWhat, ProgressDiv progress,
			final String externToken, String bizSubType, int show,
			DialogInterface.OnClickListener btnForCancel) {
		MobileSecurePayHelper mph = new MobileSecurePayHelper();
		boolean isUpdate = mph.detectSafepayUpdate(activity, show,
				btnForCancel, handler);
		if (!isUpdate) {
			try {
				MobileSecureCheck mp = new MobileSecureCheck();
				boolean bRet = mp.check(externToken, bizSubType, handler,
						myWhat, activity);
				if (bRet) {
					//
				} else {
					//
				}
			} catch (Exception ex) {
				//
			}
		} else {
			if (progress != null) {
				progress.dismiss();
				progress = null;
			}
		}
		return progress;
	}

	public static JSONObject string2JSON(String str, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arrStr = str.split(split);
			for (int i = 0; i < arrStr.length; i++) {
				String[] arrKeyValue = arrStr[i].split("=");
				json.put(arrKeyValue[0],
						arrStr[i].substring(arrKeyValue[0].length() + 1));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	public static String getDesKey(Context context) {
		//
		// 设备信息工具
		AlipayApplication application = (AlipayApplication) context
				.getApplicationContext();
		String strClientid = application.getConfigData().getClientId();
		String strKey = strClientid.substring(0, 8);

		return strKey;
	}

	// 设置密码显示模式和光标位置
	public static void setDispayMode(EditText editText,
			PasswordTransformationMethod password) {
		editText.setTransformationMethod(password);
		editText.setSelection(editText.getText().length());
	}

	public static void Share(RootActivity context, int shareType,
			String shareTxt) {
		// TODO Auto-generated method stub
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (shareType == Constant.SHARE_APP) {
			it.putExtra(Intent.EXTRA_SUBJECT,
					context.getString(R.string.shareClient));
		} else { // 代付链接分享
			it.putExtra(Intent.EXTRA_SUBJECT,
					context.getString(R.string.AgentPayLinkShareTitle));
		}
		it.setType("text/plain");
		shareData(context, it, shareType, shareTxt);
	}

	public static void shareData(RootActivity activity, Intent shareintent,
			int shareType, String shareTxt) {
		try {
			// 获取程序列表
			PackageManager pm = activity.getPackageManager();

			List<ResolveInfo> listResolveInfo = pm.queryIntentActivities(
					shareintent, PackageManager.MATCH_DEFAULT_ONLY);
			ArrayList<HashMap<String, Object>> shareappdata = new ArrayList<HashMap<String, Object>>();
			// List<ResolveInfo> --> ArrayList<HashMap<String, Object>>
			if (listResolveInfo != null) {
				int size = listResolveInfo.size();

				for (int i = 0; i < size + 1; i++) {
					HashMap<String, Object> shareappdatatemp = new HashMap<String, Object>();
					if (i == size) {
						shareappdatatemp.put(
								ShareAppListAdapter.APP_ICONS,
								activity.getResources().getDrawable(
										R.drawable.copy));
						// if(shareType == Constant.SHARE_APP){
						// shareappdatatemp.put(ShareAppListAdapter.APP_LABLE,
						// activity.getResources().getString(R.string.Copy_to_share));
						// shareappdatatemp.put(ShareAppListAdapter.SHARE_COPY,
						// activity.getResources().getString(R.string.Copy_by_qq));
						// } else {
						shareappdatatemp.put(
								ShareAppListAdapter.APP_LABLE,
								activity.getResources().getString(
										R.string.Copy_to_share));
						shareappdatatemp.put(
								ShareAppListAdapter.SHARE_COPY,
								activity.getResources().getString(
										R.string.Copy_by_qq));
						// }
						if (shareTxt != null && shareType == Constant.SHARE_PAY) {
							shareappdatatemp.put(
									ShareAppListAdapter.SEND_CONTENT, shareTxt);
						} else {
							shareappdatatemp.put(
									ShareAppListAdapter.SEND_CONTENT,
									activity.getString(R.string.share_content));
						}
					} else {
						ResolveInfo temp = (ResolveInfo) listResolveInfo.get(i);
						// 去除蓝牙程序
						if (temp.activityInfo.packageName.toLowerCase().equals(
								ShareAppListAdapter.APP_PACKAGENAME_BLUETOOTH))
							continue;
						shareappdatatemp.put(ShareAppListAdapter.APP_ICONS,
								temp.loadIcon(pm));
						shareappdatatemp.put(ShareAppListAdapter.APP_LABLE,
								temp.loadLabel(pm));
						shareappdatatemp.put(
								ShareAppListAdapter.APP_PACKAGENAME,
								temp.activityInfo.applicationInfo.packageName);
						shareappdatatemp.put(ShareAppListAdapter.APP_NAME,
								temp.activityInfo.name);
						if (shareType == Constant.SHARE_APP) {
							if (temp.activityInfo.packageName
									.toLowerCase()
									.equals(ShareAppListAdapter.APP_PACKAGENAME_SMS)) {
								shareappdatatemp
										.put(ShareAppListAdapter.SEND_CONTENT,
												activity.getString(R.string.share_content2));
							} else {
								shareappdatatemp
										.put(ShareAppListAdapter.SEND_CONTENT,
												activity.getString(R.string.share_content));
							}
						} else if (shareTxt != null) { // 代付链接
							if (temp.activityInfo.packageName
									.toLowerCase()
									.equals(ShareAppListAdapter.APP_PACKAGENAME_SMS)) {
								shareappdatatemp.put(
										ShareAppListAdapter.SEND_CONTENT,
										shareTxt);
							} else {
								shareappdatatemp.put(
										ShareAppListAdapter.SEND_CONTENT,
										shareTxt);
							}
						}

					}
					shareappdata.add(shareappdatatemp);
				}
			}
			if (shareappdata.size() <= 0) {
				Toast toast = Toast.makeText(activity, "无分享软件", 5000);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			ListView ShareListView = new ListView(activity);

			AlertDialog dialog;
			if (shareType == Constant.SHARE_APP) {
				dialog = new AlertDialog.Builder(activity)
						.setTitle(
								activity.getResources().getString(
										R.string.share_choose))
						.setView(ShareListView).create();
			} else {
				dialog = new AlertDialog.Builder(activity)
						.setTitle(
								activity.getResources().getString(
										R.string.share_choose))
						.setView(ShareListView).create();
			}
			SimpleAdapter shareSimpleAdapter = new ShareAppListAdapter(
					activity, shareappdata, shareintent, dialog);
			ShareListView.setBackgroundColor(Color.WHITE);
			ShareListView.setDividerHeight(1);
			ShareListView.setCacheColorHint(Color.WHITE);
			ShareListView.setAdapter(shareSimpleAdapter);
			ShareListView
					.setOnItemClickListener((OnItemClickListener) shareSimpleAdapter);

			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SpannableStringBuilder changeStringStyle(String oldStr,
			String startStr, String endStr, int color, int fontSize) {
		SpannableStringBuilder strStyle = null;

		if (oldStr != null) {
			int indexS = oldStr.indexOf(startStr);
			int indexE = oldStr.indexOf(endStr) + 1;
			if (indexS != -1 && indexE != -1) {
				strStyle = new SpannableStringBuilder(oldStr);
				strStyle.setSpan(new ForegroundColorSpan(color), indexS,
						indexE, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				strStyle.setSpan(
						new StyleSpan(android.graphics.Typeface.NORMAL),
						indexS, indexE, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				// CharacterStyle spanTxtSize=null;
				// spanTxtSize=new AbsoluteSizeSpan(fontSize);//字体大小
				// strStyle.setSpan(spanTxtSize, indexS, indexE,
				// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
			}
		}
		return strStyle;
	}

	public static boolean is3GOrWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				int netType = info.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {
					return true;
				} else if (netType == ConnectivityManager.TYPE_MOBILE) {
					String typeName = info.getTypeName().toLowerCase();
					typeName = info.getExtraInfo().toLowerCase();
					if (typeName.contains("3g")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isWapApn(Context context) {
		boolean iswap = false;

		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					int netType = info.getType();
					if (netType == ConnectivityManager.TYPE_WIFI) {
						iswap = false;
					} else if (netType == ConnectivityManager.TYPE_MOBILE) {
						String typeName = info.getTypeName().toLowerCase(); // cmwap/cmnet/wifi/uniwap/uninet
						typeName = info.getExtraInfo().toLowerCase();
						// 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
						if (typeName.contains("wap")) {
							iswap = true;
						}

						// 获取默认代理主机ip
						String host = android.net.Proxy.getDefaultHost();
						// 获取端口
						int port = android.net.Proxy.getDefaultPort();
						if (host != null && !host.equals("")) {
							iswap = true;
						}

						LogUtil.logOnlyDebuggable("BaseHelper",
								"checkConnectType(): typeName:" + typeName
										+ ", host=" + host + ", port=" + port);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			iswap = false;
		}

		return iswap;
	}

	public static AlipayLocateInfo getLocation(Context curActivity,
			LocationListener listener) {
		AlipayLocateInfo locationInfo = new AlipayLocateInfo();
		int curCellId = 0;
		int curCellIdCountry = 0;
		int curCellIdNet = 0;
		int curCellIdLac = 0;
		double latitude = 0.0f;
		double longititude = 0.0f;
		float accuracyRange = 0.0f;

		LocationManager locMgr = (LocationManager) curActivity
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String provider = locMgr.getBestProvider(criteria, true);
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "BaseHelper",
				"getLocation() provider:" + provider);
		if (provider == null) {
			return null;
		}

		Location location = locMgr.getLastKnownLocation(provider);
		if (location == null) {
			// 获取当前的cell id信息
			TelephonyManager tm = (TelephonyManager) curActivity
					.getSystemService(Context.TELEPHONY_SERVICE);
			int typeNetwork = tm.getNetworkType();
			int typePhone = tm.getPhoneType();
			String netOperator = tm.getNetworkOperator();
			if (netOperator != null && netOperator.length() >= 5) {
				curCellIdCountry = Integer.valueOf(netOperator.substring(0, 3));
				curCellIdNet = Integer.valueOf(netOperator.substring(3, 5));
			}
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "BaseHelper",
					"getLocation() typeNetwork=" + typeNetwork + ", typePhone="
							+ typePhone);

			switch (typePhone) {
			case TelephonyManager.PHONE_TYPE_GSM:
				GsmCellLocation gclocation = (GsmCellLocation) tm
						.getCellLocation();
				if (gclocation != null) {
					curCellId = gclocation.getCid();
					curCellIdLac = gclocation.getLac();
				}
				break;

			case 4:// TelephonyManager.PHONE_TYPE_CDMA:
				if (Utilz.parseInt(Build.VERSION.SDK) > 4) {
					try {
						CellLocation clocation = tm.getCellLocation();
						// CdmaCellLocation cclocation = (CdmaCellLocation)
						// tm.getCellLocation();
						if (clocation != null) {
							Class<?> clazz = clocation.getClass();
							Method mgbsi = clazz.getMethod("getBaseStationId",
									(Class[]) null);
							curCellId = (Integer) mgbsi.invoke(clocation,
									(Object[]) null);// cclocation.getBaseStationId();//基站小区号
														// cellId
							Method mgsi = clazz.getMethod("getSystemId",
									(Class[]) null);
							int sid = (Integer) mgsi.invoke(clocation,
									(Object[]) null);// cclocation.getSystemId();//系统标识
														// mobileNetworkCode
							Method mgni = clazz.getMethod("getNetworkId",
									(Class[]) null);
							int nid = (Integer) mgni.invoke(clocation,
									(Object[]) null);// cclocation.getNetworkId();//网络标识
														// locationAreaCode

							curCellIdLac = sid;
							curCellIdNet = nid;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;

			default:
				break;
			}
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "BaseHelper",
					"getLocation() curCellId:" + curCellId + ", curCellIdLac="
							+ curCellIdLac + ", curCellIdNet=" + curCellIdNet);

			// //获取邻接的cell id 信息
			// List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
			// //添加辅助定位信息
			// for(int i=0;i<list.size();i++)
			// {
			// NeighboringCellInfo cellinfo = list.get(i);
			// }
			// BaseHelper.LogMsg(Constant.LOG_LEVEL_DEBUG, "BaseHelper",
			// "getLocation() neighbour num="+list.size() +"lac="+lac);
		} else {
			latitude = location.getLatitude();
			longititude = location.getLongitude();
			accuracyRange = location.getAccuracy();
		}
		locationInfo.latitude = String.valueOf(latitude);
		locationInfo.longititude = String.valueOf(longititude);
		locationInfo.accuracyRange = String.valueOf(accuracyRange);
		locationInfo.curCellId = String.valueOf(curCellId);
		locationInfo.curCellIdLac = String.valueOf(curCellIdLac);
		locationInfo.curCellIdNet = String.valueOf(curCellIdNet);
		locationInfo.curCellIdCountry = String.valueOf(curCellIdCountry);

		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "BaseHelper",
				"getLocation() curCellId:" + curCellId + ", accuracyRange:"
						+ accuracyRange + ", latitude:" + latitude
						+ ", longititude:" + longititude);

		return locationInfo;
	}

	public static int checkCodeKeyType(String codeKey, String codeType,
			String prefix) {
		int result = -1;
		if (codeKey == null || codeKey.equals("")) {
			return result;
		}

		if (codeType != null
				&& codeType.equals(Constant.BARCODE_CAPTURE_TYPE_QRCODE)) {
			if (codeKey.contains(prefix)) {
				result = 0;
			}
		}

		return result;
	}

	public static String getTimeStamp() {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
		Date date = new Date();

		String timeStamp = format.format(date);
		return timeStamp;
	}

	public static void recordWarningMsg(RootActivity context,
			String... warningMsg) {
		/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();

		AlipayLogAgent.onEvent(
				context,
				Constants.MONITORPOINT_CLIENTSERR,
				warningMsg[0],
				warningMsg.length > 1 ? warningMsg[1] : storageStateInfo
						.getValue(Constants.STORAGE_CURRENTVIEWID),
				storageStateInfo.getValue(Constants.STORAGE_APPID),
				storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), context
						.getUserId(), storageStateInfo
						.getValue(Constants.STORAGE_PRODUCTVERSION),
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
	}

	/**
	 * 重置repeat
	 * 
	 * @param view
	 */
	public static void fixBackgroundRepeat(View view) {
		Drawable bg = view.getBackground();
		if (bg != null) {
			if (bg instanceof BitmapDrawable) {
				BitmapDrawable bmp = (BitmapDrawable) bg;
				bmp.mutate(); // make sure that we aren't sharing state anymore
				bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			}
		}
	}
}
