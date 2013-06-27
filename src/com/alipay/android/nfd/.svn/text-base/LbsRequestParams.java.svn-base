package com.alipay.android.nfd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.ConfigData;

public class LbsRequestParams {
	
	protected JSONObject requestJsonParams;
	
	//经度
	private double mLongitude =0;
	//纬度
	private double mLatitude =0;
	//业务类型
	private String mBusinessType;
	//基站Id
	private JSONObject mCellId = new JSONObject();
	// API版本
	private String mApiVer;
	// 网络类型
	private String mNetworkType;
	// 网络运营商
	private String mNetworkOperator;
	// 客户端UA
	private String mUserAgent;
	// WIFI基站信息
	private JSONArray mWifiInfo = new JSONArray();
	// CDMA信息
	private JSONArray mCdmaInfo = new JSONArray();;
	//获取设备IMEI
	private String mImei;
	
	private AlipayApplication mApplication;
	
	private ConfigData configData;
	
	private String mSessionId;
	
	private String mClientID;
	
	
	public LbsRequestParams(Context mContext) {
		requestJsonParams = new JSONObject();
		mApplication = (AlipayApplication) mContext.getApplicationContext();
		configData = mApplication.getConfigData();

		// TODO Auto-generated constructor stub
		mClientID = configData.getClientId();
		mSessionId = configData.getSessionId();
		try {
			requestJsonParams.put(Constant.RQF_SMSTRANS_SESSIONID, mSessionId);
			requestJsonParams.put(Constant.RQF_CLIENT_ID, mClientID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getmImei() {
		return mImei;
	}
	public void setmImei(String mImei) {
		try {
			requestJsonParams.put("imei", mImei);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mImei = mImei;
	}
	
	public String getmSessionId() {
		return mSessionId;
	}
	public void setmSessionId(String mSessionId) {
		try {
			requestJsonParams.put(Constant.RQF_CLIENT_ID, mSessionId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mSessionId = mSessionId;
	}
	public String getmClientID() {
		try {
			requestJsonParams.put(Constant.RQF_SMSTRANS_SESSIONID,
					mClientID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mClientID;
	}
	public void setmClientID(String mClientID) {
		this.mClientID = mClientID;
	}
	
	public double getmLongitude() {
		return mLongitude;
	}
	public void setmLongitude(double mLongitude) {
		try {
			requestJsonParams.put("longitude", mLongitude);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mLongitude = mLongitude;
	}
	public double getmLatitude() {
		return mLatitude;
	}
	public void setmLatitude(double mLatitude) {
		try {
			requestJsonParams.put("latitude", mLatitude);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mLatitude = mLatitude;
	}
	public String getmBusinessType() {
		return mBusinessType;
	}
	public void setmBusinessType(String mBusinessType) {
		try {
			requestJsonParams.put("businessType", mBusinessType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mBusinessType = mBusinessType;
	}
	public String getmApiVer() {
		return mApiVer;
	}
	public void setmApiVer(String mApiVer) {
		try {
			requestJsonParams.put("apiVer", mApiVer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mApiVer = mApiVer;
	}
	public String getmNetworkType() {
		return mNetworkType;
	}
	public void setmNetworkType(String mNetworkType) {
		try {
			requestJsonParams.put("networkType", mNetworkType);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mNetworkType = mNetworkType;
	}
	public String getmNetworkOperator() {
		return mNetworkOperator;
	}
	public void setmNetworkOperator(String mNetworkOperator) {
		try {
			requestJsonParams.put("networkOperator", mNetworkOperator);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mNetworkOperator = mNetworkOperator;
	}
	public String getmUserAgent() {
		return mUserAgent;
	}
	public void setmUserAgent(String mUserAgent) {
		try {
			requestJsonParams.put("userAgent", mUserAgent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mUserAgent = mUserAgent;
	}
	
	public JSONObject getmCellId() {
		return mCellId;
	}
	public void setmCellId(JSONObject mCellId) {
		try {
			requestJsonParams.put("cellId", mCellId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mCellId = mCellId;
	}
	public JSONArray getmWifiInfo() {
		return mWifiInfo;
	}
	public void setmWifiInfo(JSONArray mWifiInfo) {
		try {
			requestJsonParams.put("wifiInfo", mWifiInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mWifiInfo = mWifiInfo;
	}
	public JSONArray getmCdmaInfo() {
		return mCdmaInfo;
	}
	public void setmCdmaInfo(JSONArray mCdmaInfo) {
		try {
			requestJsonParams.put("cdmaInfo", mCdmaInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mCdmaInfo = mCdmaInfo;
	}
	
	public JSONObject getRequestJsonParams() {
		return requestJsonParams;
	}
	public void setRequestJsonParams(JSONObject requestJsonParams) {
		this.requestJsonParams = requestJsonParams;
	}
	
}
