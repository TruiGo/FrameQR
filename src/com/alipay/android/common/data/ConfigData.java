/**
 * 
 */
package com.alipay.android.common.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.client.util.TelephoneInfoHelper;

/**
 * @author sanping.li
 *
 */
public class ConfigData implements Saveable {
    private Context mContext;
    /**
     * 公钥 
     */
    private String mPublicKey;
    /**
     * 时间戳
     */
    private String mTimeStamp;
    /**
     * SessionId
     */
    private String mSessionId;
    /**
     * 淘宝SessionId
     */
    private String mTaobaoSid;
    /**
     * ClientId
     */
    private String mClientId;
    private String mProductId;
    private String mProductVersion;
    /**
     * 用户代理
     */
    private String mUserAgent;
    private String mScreenWidth;
    private String mScreenHeight;

    private String mImei;
    
    private int mDensityDpi;

    public ConfigData(Context context) {
        mContext = context;
    }

    public void init() {
        TelephoneInfoHelper teleHelper = TelephoneInfoHelper.getTelephoneHelper(mContext);
        mClientId = teleHelper.getClientID();
        mProductId = teleHelper.getProductId();
        mProductVersion = teleHelper.getProductVersion();

        mScreenWidth = teleHelper.getScreenWidth() + "";
        mScreenHeight = teleHelper.getScreenHeight() + "";
        mImei = teleHelper.getIMEI();
        mDensityDpi = teleHelper.getDensity();
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setPublicKey(String publicKey) {
        mPublicKey = publicKey;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getSessionId() {
        if (mSessionId != null)
            return mSessionId;
        else
            return "";
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public String getTaobaoSid() {
        return mTaobaoSid;
    }

    public void setTaobaoSid(String taobaoSid) {
        mTaobaoSid = taobaoSid;
    }

    public String getClientId() {
        return mClientId;
    }

    public void setClientId(String clientId) {
        mClientId = clientId;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public String getProductVersion() {
        return mProductVersion;
    }

    public void setProductVersion(String productVersion) {
        mProductVersion = productVersion;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    /**
     * 接入点
     */
    public String getAccessPoint() {
        return DeviceHelper.getApnInUse(mContext);
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public void setUserAgent(String userAgent) {
        mUserAgent = userAgent;
    }

    public String getScreenWidth() {
        return mScreenWidth;
    }

    public void setScreenWidth(String screenWidth) {
        mScreenWidth = screenWidth;
    }

    public String getScreenHeight() {
        return mScreenHeight;
    }

    public void setScreenHeight(String screenHeight) {
        mScreenHeight = screenHeight;
    }

    public String getImei() {
        return mImei;
    }

    public int getDensity() {
        return mDensityDpi;
    }

    public void destroy() {
        mSessionId = null;
    }

    @Override
    public void save(SharedPreferences preferences,String key) {
        Editor editor = preferences.edit();
        editor.putString(key+".publickey", mPublicKey);
        editor.putString(key+".timestamp", mTimeStamp);
        editor.putString(key+".sessionid", mSessionId);
        editor.putString(key+".taobaosid", mTaobaoSid);
        editor.commit();
    }

    @Override
    public void restore(SharedPreferences preferences,String key) {
        mPublicKey =  preferences.getString(key+".publickey", null);
        mTimeStamp =  preferences.getString(key+".timestamp", null);
        mSessionId =  preferences.getString(key+".sessionid", null);
        mTaobaoSid =  preferences.getString(key+".taobaosid", null);
    }
}
