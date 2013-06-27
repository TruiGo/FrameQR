package com.alipay.android.common.data;

import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.client.util.Responsor;

public class AdvertData extends Observable implements Saveable{
    public static final int TYPE_INFO = 1;
    public static final int TYPE_HOME = 2;
    public static final int TYPE_APPS = 3;

    private Context mContext;
    /**
     * 消息区广告位
     */
    private String mInfoAd;
    /**
     * 首页广告位
     */
    private String mHomeAd;
    /**
     * 资源交换
     */
    private String mAppAd;

    private Handler mHandler;

    public String getInfoAd() {
        return mInfoAd;
    }

    public String getAppAd() {
        return mAppAd;
    }

    public String getHomeAd() {
        return mHomeAd;
    }

    public AdvertData(Context context) {
        init(context);
    }

    public void requestData(DataHelper dataHelper) {
        dataHelper.sendQueryAdvert(mHandler, 0);
    }

    private void fillData(JSONObject jsonResp) throws JSONException {
        JSONArray array = jsonResp.optJSONArray(Constant.RPF_CMS_ADS);
        JSONObject ad = null;
        int type;
        boolean valid;
        String url = null;
        for (int i = 0; i < array.length(); ++i) {
            ad = array.optJSONObject(i);
            valid = ad.optBoolean(Constant.RPF_CMS_VALID);
            type = ad.optInt(Constant.RPF_CMS_TYPE);
            url = ad.optString(Constant.RPF_CMS_URL);
            switch (type) {
                case TYPE_INFO:
                    if (valid){
                        mInfoAd = url;
                    }else{
                        mInfoAd = null;
                    }
                    break;
                case TYPE_HOME:
                    if (valid){
                        mHomeAd = url;
                    }else{
                        mHomeAd = null;
                    }
                    break;
                case TYPE_APPS:
                    if (valid){
                        mAppAd = url;
                    }else{
                        mAppAd = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void init(Context context) {
        mContext = context;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                AlipayApplication application = (AlipayApplication) mContext
                    .getApplicationContext();
                Responsor responsor = (Responsor) msg.obj;
                JSONObject obj = responsor.obj;
                int status = responsor.status;
                if ((status == 100) && (!application.getDataHelper().isCanceled())) {
                    try {
                        fillData(obj);
                        setChanged();
                        notifyObservers();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
    }

    public void destroy() {
        mInfoAd = null;
        mHomeAd = null;
        mAppAd = null;
        deleteObservers();
    }

    @Override
    public void save(SharedPreferences preferences,String key) {
        Editor editor = preferences.edit();
        editor.putString(key+".info", mInfoAd);
        editor.putString(key+".home", mHomeAd);
        editor.putString(key+".app", mAppAd);
        editor.commit();
    }

    @Override
    public void restore(SharedPreferences preferences,String key) {
        mInfoAd = preferences.getString(key+".info", null);
        mHomeAd = preferences.getString(key+".home", null);
        mAppAd = preferences.getString(key+".app", null);
    }
}
