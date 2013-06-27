package com.alipay.android.longlink;

import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtils {
	private static final HashMap<String, String> mNetTypeMap = new HashMap<String, String>();
	public  static final String TYPE_WIFI = "wifi";
	public  static final String TYPE_CMWAP = "cmwap";
	public  static final String TYPE_CMNET = "cmnet";
	public  static final String TYPE_UNIWAP = "uniwap";
	public  static final String TYPE_UNINET = "uninet";
	public  static final String TYPE_CTNET = "ctnet";
	public  static final String TYPE_CTWAP = "ctwap";
	public  static final String TYPE_3GNET = "3gnet";
	public  static final String TYPE_3GWAP = "3gwap";
	
	static{
		mNetTypeMap.clear();
		mNetTypeMap.put(TYPE_WIFI, "0");
		
		mNetTypeMap.put(TYPE_CMWAP, "1");
		mNetTypeMap.put(TYPE_CMNET, "2");
		
		mNetTypeMap.put(TYPE_UNIWAP, "3");
		mNetTypeMap.put(TYPE_UNINET, "4");
		
		mNetTypeMap.put(TYPE_CTNET, "5");
		mNetTypeMap.put(TYPE_CTWAP, "6");
		
		mNetTypeMap.put(TYPE_3GNET, "7");
		mNetTypeMap.put(TYPE_3GWAP, "8");
	}
	
	public static int getConnType(Context context) {
		int netType = 0;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					String typeName = info.getTypeName(); // cmwap/cmnet/wifi/uniwap/uninet
					if (typeName.equalsIgnoreCase("WIFI")) {
						netType = 0;
					} else if (typeName.equalsIgnoreCase("MOBILE")) { // 如果是使用的运营商网络
						typeName = info.getExtraInfo().toLowerCase();
						// 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
						netType = Integer.valueOf(mNetTypeMap.get(typeName)).intValue();
					} else {
						netType = -1;
						if (typeName.contains("777")) { // 可能是电信网络——华为是如此
							if (isWapAPN()) {
								netType = 6;
							} else {
								netType = 5;
							}
						}
					}
				} else {
					netType = 0;
				}
			}
		} catch (Exception e) {
			netType = 0;
		}

		return netType;
	}
	
	/**
	 * 是否是wap连接
	 */
	public static boolean isWapAPN(){
		String wap_proxy_ip = getWapIP();
		int wap_proxy_port = getWapPort();
		
		return  wap_proxy_ip != null && !wap_proxy_ip.equals("") && wap_proxy_port != -1;
	}
	
	public static String getWapIP(){
		String wapIp = android.net.Proxy.getDefaultHost();
		return wapIp == null ? "10.0.0.172" : wapIp;
	}
	
	public static int getWapPort(){
		int port = android.net.Proxy.getDefaultPort();
		return port == -1 ? 80 : port;
	}
}
