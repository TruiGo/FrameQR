package com.alipay.android.nfd;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.alipay.android.biz.NfdBiz;
import com.alipay.android.util.LogUtil;

public class NFDUtils {
	private final static String TAG = "NFDUtils";
	public final static int SERVER_PORT = 0x1234;
	public final static int UDP_BROADCAST_PORT = 0x1235;
	BluetoothAdapter mBlueTooth;
	private static NFDUtils instance;
	public SharedPreferences settings;
	public static String ALIPAY_INFO = "c503d718";
	public static String BlueToothKey = "BlueTooth";
	public static String BlueToothOpreator = "BlueToothOpreator";
	private static boolean mIsOriginalEable;
	Context mContext;
	private final static String bizType = "testDetectNetWorkA";
	private boolean mDetectNetWorkStatus;

	public final static String getNfdType(Context context) {
		if (isDebug(context)) {
			return getValue(context, "content://com.alipay.setting/xmpp_nfd",
					"15");
		} else {
			return "15";
		}
	}

	public static String getValue(Context context, String uri, String defaultVal) {
		Cursor cursor = context.getContentResolver().query(Uri.parse(uri),
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			String ret = cursor.getString(0);
			cursor.close();
			return ret;
		}
		return defaultVal;
	}

	public static boolean isDebug(Context context) {
		try {
			ApplicationInfo applicationInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			if ((applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public synchronized static NFDUtils getInstance(Context context) {
		if (instance == null) {
			instance = new NFDUtils(context);
		}
		return instance;
	}

	public NFDUtils(Context context) {
		mContext = context;
		settings = context.getSharedPreferences("nfdShareData", 0);
	}

	public synchronized void relaseInstance() {
		instance = null;
		restorationBluetoothName();
		if ((getBoolean(NFDUtils.BlueToothOpreator))
				&& mBlueTooth != null && mBlueTooth.isEnabled()) {
			mBlueTooth.disable();
			putBoolean(NFDUtils.BlueToothOpreator, false);
		}
	}

	public boolean init(int type) {
		mDetectNetWorkStatus = true;
		// detectNetWork();
		restorationBluetoothName();
		mIsOriginalEable = false;
		if ((type & RegistType.BlueTooth.getCode()) == 0) {
			return false;
		}
		if (mBlueTooth == null) {
			LogUtil.logOnlyDebuggable(TAG, "此手机不支持蓝牙 ");
			return false;
		}
		try {
			if (mBlueTooth.isEnabled()) {
				mIsOriginalEable = true;
				LogUtil.logOnlyDebuggable(TAG, mBlueTooth.getName());
			} else {
				if (!mBlueTooth.enable()) {
					return false;
				}
				putBoolean(NFDUtils.BlueToothOpreator, true);
			}
		} catch (Exception e) {
			LogUtil.logOnlyDebuggable(TAG, e.toString());
			return false;
		}

		return true;
	}

	public void openBlueTooth() {
		mBlueTooth = BluetoothAdapter.getDefaultAdapter();
		if (mBlueTooth == null) {
			LogUtil.logOnlyDebuggable(TAG, "此手机不支持蓝牙 ");
			return;
		}
		try {
			if (mBlueTooth.isEnabled()) {
				LogUtil.logOnlyDebuggable(TAG, mBlueTooth.getName());
			} else {
				if (!mBlueTooth.enable()) {
					return;
				}
				putBoolean(NFDUtils.BlueToothOpreator, true);
			}
		} catch (Exception e) {
			LogUtil.logOnlyDebuggable(TAG, e.toString());
		}
	}

	public void restorationBluetoothName() {
		mBlueTooth = BluetoothAdapter.getDefaultAdapter();
		if (mBlueTooth == null) {
			LogUtil.logOnlyDebuggable(TAG, "此手机不支持蓝牙 ");
			return;
		}
		if (mBlueTooth.isEnabled()) {
			String blueToothName = mBlueTooth.getName();
			if (blueToothName != null
					&& (blueToothName.startsWith("$") && blueToothName
							.endsWith("^"))) {
				mBlueTooth.setName(getString(NFDUtils.BlueToothKey,
						blueToothName));
			}
		}
	}

	public void detectNetWork() {
		if (!hasNetWork()) {
			mDetectNetWorkStatus = false;
			return;
		}
		NfdBiz nfdbiz = new NfdBiz();
		String cellStr = "{}";
		mDetectNetWorkStatus = nfdbiz.detectNetWorkStatus(String.valueOf(0),
				String.valueOf(0), cellStr, bizType);
	}

	public boolean getDetectNetWorkResult() {
		return mDetectNetWorkStatus;
	}

	public void putString(String key, String value) {
		settings.edit().putString(key, value).commit();
	}

	public String getString(String key, String defaultValue) {
		return settings.getString(key, defaultValue);
	}

	public boolean getBoolean(String key) {
		return settings.getBoolean(key, false);
	}

	public void putBoolean(String key, Boolean value) {
		try {
			settings.edit().putBoolean(key, value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getBlueToothStatus() {
		return mIsOriginalEable;
	}

	public boolean hasNetWork() {
		ConnectivityManager conManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager == null) {
			return false;
		}
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	public boolean isUseWifi() {
		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			NetworkInfo activeNetInfo = connMgr.getActiveNetworkInfo();
			if (activeNetInfo != null
					&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		} catch (Exception e) {
			LogUtil.logOnlyDebuggable(TAG, e.toString());
			return false;
		}
		return false;
	}
}
