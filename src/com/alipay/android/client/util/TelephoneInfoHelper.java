package com.alipay.android.client.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.appHall.Helper;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

/*
 * 该类主要用于获取clientId等信息。
 * Refactored by cheng.zhou@alipay.com
 */

public class TelephoneInfoHelper{
	private static final String PRODUCT_ID_STR = "Android-container";
	private static final int IMEI_LEN = 15;
	private String m_ProductVersion;
	private int m_ScreenWidth;
	private int m_ScreenHeight;
	private int m_DensityDpi; 
	
	private static TelephoneInfoHelper telephoneHelper = null;
	private AlipayDataStore settings;
	private String mProductId = "";
	private String m_clientID;
	private static final String TAG = "TeleInfo";
	
	public static TelephoneInfoHelper getTelephoneHelper(Context context)
	{
		if(telephoneHelper == null) {
			telephoneHelper = new TelephoneInfoHelper(context);
		}
		
		return telephoneHelper;
	}
	
	public String getProductVersion() {
	    return m_ProductVersion;
	}
	
	public String getProductId(){
		if(isBlank(mProductId))
		{
			return PRODUCT_ID_STR;
		}
		else
		{
			return PRODUCT_ID_STR + "_" + mProductId;
		}
	}
	
	private TelephoneInfoHelper(Context context) {
		try{
            String tpackageName = context.getPackageName();
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(tpackageName, PackageManager.GET_ACTIVITIES);
            m_ProductVersion = mPackageInfo.versionName;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        m_ScreenWidth = Helper.getDisplayMetrics(context).widthPixels;
        m_ScreenHeight = Helper.getDisplayMetrics(context).heightPixels;
        m_DensityDpi = Helper.getDisplayMetrics(context).densityDpi;
        settings = new AlipayDataStore(context);
        m_clientID = initClientID(context);
        mProductId = context.getString(R.string.useragent);
    }
	
	public String getIMEI()
	{
		if(isValidClientID(m_clientID))
			return m_clientID.substring(m_clientID.length()-IMEI_LEN, m_clientID.length());
		else
			return "";
	}
	
	public int getDensity() {
        return m_DensityDpi;
    }
	/**
	 * 判断c是否是十六进制字符
	 * @param c
	 * @return
	 */
	private boolean isHexDigit(byte c)
	{
		return (c >= '0' && c <= '9')
				|| (c >= 'a' && c <= 'z') 
				|| (c >= 'A' && c <= 'Z');
	}
	/**
	 * 将非16进制字符替换成'0'
	 * @param imei
	 * @return
	 */
	private String replaceNonHexChar(String imei)
	{
		if(isBlank(imei))
			return imei;
		byte [] byteClientId = imei.getBytes();
		for(int i = 0; i< byteClientId.length; i++)
		{
			if( !isHexDigit(byteClientId[i]) ) //如果不是十六进制字符，则用0替换。
				byteClientId[i] = '0';
		}
		return new String(byteClientId);
	}
	
	public int getScreenWidth()
	{
		return m_ScreenWidth;
	}
	
	public int getScreenHeight()
	{
		return m_ScreenHeight;
	}	
	
	public String getClientID()
	{
		return m_clientID;
	}
	/**
	 * 判断clientId是否合法
	 * @param clientID
	 * @return
	 */
	private boolean isValidClientID(String clientID) {
		if(isBlank(clientID))
			return false;
		return clientID.matches("[[a-z][A-Z][0-9]]{15}\\|[[a-z][A-Z][0-9]]{15}");
	}

	/**
	 * 将imei或imsi正规化，不足位数则补全，有非数字则用0代替。
	 * @param imeiOrImsi
	 * @return
	 */
	private String normalize(String imeiOrImsi)
	{
		if(isBlank(imeiOrImsi))
		{
			imeiOrImsi = BaseHelper.getTimeStamp();
		}
		if(imeiOrImsi.length() < IMEI_LEN)
		{
			imeiOrImsi = (imeiOrImsi+"123456789012345").substring(0,IMEI_LEN);
		}
		return replaceNonHexChar(imeiOrImsi);
	}
	/**
	 * 根据imsi和imei得到规范化的clientId
	 * @param imsi
	 * @param imei
	 * @return
	 */
	private String normalizedClientId(String imsi, String imei)
	{
		return normalize(imsi) + "|" + normalize(imei) ;
	}
	
	private boolean isBlank(String s)
	{
		return s==null || s.length() == 0;
	}
	private boolean isNotBlank(String s)
	{
		return !isBlank(s);
	}
	/**
	 * 判断用户本次设备的clientId和上次登录时保存的是否相同。
	 * @param imei
	 * @param imsi
	 * @param savedClientId
	 * @return
	 */
	private boolean isClientIDChanged(String imsi, String imei, String savedClientId) {
		String savedImsi = savedClientId.substring(0,IMEI_LEN);
		String savedImei = savedClientId.substring(savedClientId.length()-IMEI_LEN,savedClientId.length());
		if( isNotBlank(imei) && !savedImei.equals(imei))//如果当前取到的imei不为空且与不存的不同，则说明已经改变
			return true;
		if( isNotBlank(imsi) && !savedImsi.equals(imsi))//如果当前取到的imsi不为空且与不存的不同，则说明已经改变
			return true;
		return false;
	}
	/**
	 * 初始化clientId: 
	 * 如果本地已经有保存的clientId且没有改变，则使用保存的。
	 * 如果本地保存了，但imei或imsi任意一个改变，则使用新的。取到的imei或imsi的值为空时不算改变。
	 * 如果本地没有保存，则使用取到的值，如果取到的值为空，则随机生成一个15位字符串。
	 * 这样可以保证对以前版本的兼容。同时避免imsi有时能取到，有时取不到时clientId的震荡变化。
	 * @param context
	 * @return
	 */
	private String initClientID(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telMgr.getDeviceId();
		LogUtil.logOnlyDebuggable(TAG, "origin imei:"+imei);
		if(isNotBlank(imei) && (imei.equalsIgnoreCase("unknown") || imei.equalsIgnoreCase("null") ))//imei有时候会为unknown或null，其实就意味着取不到，此时应该置为空
		{
			imei = "";
		}
		LogUtil.logOnlyDebuggable(TAG, "changed imei:"+imei);

		String imsi = telMgr.getSubscriberId();
		LogUtil.logOnlyDebuggable(TAG, "origin imsi:"+imsi);
		String savedClientId = settings.getString(AlipayDataStore.ALIPAY_CLIENT_ID);
		LogUtil.logOnlyDebuggable(TAG, "saved clientid:"+savedClientId);
		String newClientId = savedClientId;
		if(isValidClientID(savedClientId)) //如果保存的clientId合法
		{
			LogUtil.logOnlyDebuggable(TAG, "client id is valid:"+savedClientId);
			if(isClientIDChanged(imsi, imei , savedClientId))//如果当前使用设备的clientId与上次保存的不同
			{
				newClientId = normalizedClientId(imsi, imei);
				LogUtil.logOnlyDebuggable(TAG, "normarlize, imsi:"+imsi+",imei:"+imei+",newClientId:"+newClientId);
				settings.putString(AlipayDataStore.ALIPAY_CLIENT_ID, newClientId);
			}
		}else
		{
			LogUtil.logOnlyDebuggable(TAG, "client is is not valid, imei:"+imei+",imsi:"+imsi);
			if(isBlank(imei))//如果为空，则用当前时间随机生成一个
				imei = BaseHelper.getTimeStamp();
			if(isBlank(imsi))//如果为空，则用当前时间随机生成一个
				imsi = BaseHelper.getTimeStamp();
			newClientId = normalizedClientId(imsi, imei);
			LogUtil.logOnlyDebuggable(TAG, "normalize, imei:"+imei+",imsi:"+imsi+",newClientId:"+newClientId);
			settings.putString(AlipayDataStore.ALIPAY_CLIENT_ID, newClientId);
		}
		return newClientId;
	}
	
	public String getOsInfo(Context context) {
		return DeviceHelper.getOsInfo(context);
    }
}