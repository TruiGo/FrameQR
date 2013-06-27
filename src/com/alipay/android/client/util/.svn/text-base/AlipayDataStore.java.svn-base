package com.alipay.android.client.util;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;

public class AlipayDataStore
{
	static final String TAG = "setting";
	Context myActivity;
	public SharedPreferences settings;
	
	public static final String CURENTVERSION = "curentVersion";
	public static final String TRANSFERVERSION = "transferVersion";
	public static final String ADDCONTACTVERSION = "addContactVersion";
	public static final String TRANSFERINDEX = "transferIndex";
	public static final String TRANSFERCONFIRM = "transferConfirm";
	public static final String MAIN_VERSION = "main_version";
	public static final String RECORDLIST_VERSION = "recordlist_version";
	public static final String VOUCHERLIST_VERSION = "voucherlist_version";

    public static final String QUICKPAY = "quickpay";
	public static final String PERSONLCARDFIST = "personlcardFist";
	
	public static final String SETTING_INFOS = "SETTING_Infos";
	public static final String NAME = "name";
	public static final String NAME_TAOBAO = "name_taobao";
	public static final String PASSWORD = "pwd";
	public static final String PASSWORD_TAOBAO = "pwd_taobao";
	public static final String LOGIN_CHECKED = "login_check";
	public static final String PWD_CHECKED = "pwd_check";
	public static final String PWD_CHECKED_TAOBAO = "pwd_check_taobao";
	public static final String SAVED_URL = "check";
	public static final String STARTDATETIME = "startdatetime";
	public static final String LOGIN_SERVER_TIME 	= "loginServerTime";
	//
	// wait pay trade hint check box.
	public static final String WPTHintCheck 		= "WPTHintCheck";
	public static final String LOGINTYPE = "logintype";
	
	public static final String SAM_FIRST="firstloginsam";//三星第一次登陆
	public static final String DISPLAY_LICENSE = "license";//moto是否显示免责声明

	public static final String BARCODE_SHOW_GUIDE = "barcode_first_guide";
	public static final String BARCODE_BACK_IMG = "barcode_backimg";
	public static final String BARCODE_SERVER_ENV = "barcode_server_env";
	
	public static final String ALIPAY_CLIENT_ID="clientId";
	public static final String MOTO_BRAND = "motoInternal";//moto内置标识
	public static final String SAMSUNG_BRAND = "samsungInternal";//三星内置标识
	public static final String LASTUPDATEVERSION = "lastupdateversion";//最后一次跟新标识
	
	public static final String LASTNETSTATE = "lastNetState";//最后一次跟网络状态
	public static final String REJECTTIMES = "rejectTimes";//拒绝更新的次数
	public static final String LASTREJECTTIME = "lastRejectTime";//上次拒绝更新的时间
	
	//安全支付更新状态保存
	public static final String SAFEPAYLASTNETSTATE = "safePayLastNetState";
	public static final String SAFEPAYREJECTTIMES = "safePayRejectTimes";
	public static final String QRCODE_PREFIX = "qrcodePrefix";//最后一次跟新的二维码前缀
	
	// 推送通知的设置数据.
	public static final String NOTIFICATION_SETTING_STARTTIME="SETTINGS_TIME_START";
	public static final String NOTIFICATION_SETTING_ENDTIME="SETTINGS_TIME_END";
	public static final String NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS="SETTINGS_NOTIFICATION_ENABLED";
	public static final String NOTIFICATION_SETTING_VOICECHECKEDSTATUS="SETTINGS_SOUND_ENABLED";
	public static final String NOTIFICATION_SETTING_VIBRATORCHECKEDSTATUS="SETTINGS_VIBRATE_ENABLED";
	
	//信用卡还款设置数据
	public static final String BANK_CCR_SMS_FORMAT="BANK_CCR_SMS_FORMAT";
	public static final String BANK_CCR_SMS_OPEN="BANK_CCR_SMS_OPEN";
	public static final String BANK_CCR_SMS_CONTENT1="BANK_CCR_SMS_CONTENT1";
	public static final String BANK_CCR_SMS_CONTENT2="BANK_CCR_SMS_CONTENT2";
	public static final String BANK_CCR_SMS_CONTENT3="BANK_CCR_SMS_CONTENT3";
	public static final String BANK_CCR_SAVED_BANKLIST="BANK_CCR_SAVED_BANKLIST";
	public static final String BANK_CCR_SUPPORT_BANKLIST="BANK_CCR_SUPPORT_BANKLIST";
	public static final String BANK_CCR_UPDATESAVED="BANK_CCR_UPDATESAVED";
	public static final String BANK_CCR_IS_NEW = "BANK_CCR_IS_NEW";//上次成功交易是否为新信用卡
	public static final String BANK_CCR_SAVED_TIME = "BANK_CCR_SAVED_TIME";//更新已存信用卡列表时间位
	
	public static final String ISFIRSTLOGIN = "isFirstLogin";
	public static final String CHOOSELOGINTYPE = "chooseloginType";
	public static final String LOGINTYPE_ALIPAY = "alipay";
	public static final String LOGINTYPE_TAOBAO = "taobao";
	public static final String THE_LAST_TIME_SUCCESS_CARD_INDEX_NUMBER = "THE_LAST_TIME_SUCCESS_CARD_INDEX_NUMBER";
	
	public static final String APP_FREQUENCY_COUNT = "appFrequencyCount";
	
	public static final String LASTLOGINUSERAVTARPATH = "lastLoginUserAvtarPath";
	
	public static final String CURUSER_QRCODE_PATH = "curUserQrcodePath";
	
	public AlipayDataStore(Context theActivity) 
	{
		myActivity 	= theActivity;
		settings 	= myActivity.getSharedPreferences(SETTING_INFOS, 0);

	}

	public boolean containString(String key) {
		return settings.contains(key);
	}
	
	public String getString(String key)
	{
		return settings.getString(key, "");
	}
	
	public String getString(String key, String defaultValue)
	{
		return settings.getString(key, defaultValue);
	}

	public void putString(String key, String value)
	{
		settings.edit().putString(key, value).commit();
	}
	
	public boolean getBoolean(String key){
		return settings.getBoolean(key, false);
	}
	
	public boolean getBoolean(String key,Boolean defaultValue){
		return settings.getBoolean(key, defaultValue);
	}
	
	public void putBoolean(String key,Boolean value){
		settings.edit().putBoolean(key, value).commit();
	}
	
	public void romve(String key){
		settings.edit().remove(key).commit();
	}

	public void TryRemoveDownloadFile()
	{
		String url = settings.getString(SAVED_URL, "");
		if (url.length() > 0)
		{
			File file = new File(url);
			file.delete();
			settings.edit().putString(SAVED_URL, "");
		}
	}

}
