package com.alipay.android.core;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.appHall.security.CertHandler;
import com.alipay.android.client.AlipayApplication;

public abstract class Runtime implements Engine {
    /**
     * 包Id
     */
    protected String mPkgId;
    /**
     * 父包Id
     */
    protected String mParentId;
    /**
     * manifest数据
     */
    protected ManifestDoc mManifestDoc;
    /**
     * 路径
     */
    protected String mPath;

    /**
     * 是否是应用
     */
    protected boolean mApp;

    /**
     * 上下文
     */
    protected Context mContext;

    /**
     * 消息handler
     */
    protected Handler mHandler;

    /**
     *  签名处理
     */
    protected CertHandler mCertHandler;

    @Override
    public void init(Context context) {
        mCertHandler = new CertHandler(this, mPath);
        final AlipayApplication application = (AlipayApplication) context.getApplicationContext();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int action = msg.what;
                String params = (String) msg.obj;
                ParamString paramString = new ParamString(params);
                String sourceId = paramString.getValue("sourceId");

                switch (action) {
                    case MsgAction.ACT_LAUNCH:
                        if (mApp) {
                            application.startActivity(mPkgId, params);
                        } else {
                            //TODO start service
                        }
                        break;

                    default:
                        handleMsg(sourceId, action, params);
                }
            }

        };
    }

    @Override
    public void execute(String sourceId, int action, String params) {
        Message message = mHandler.obtainMessage();
        message.what = action;
        message.obj = "sourceId=" + sourceId + "&" + params;
        mHandler.sendMessage(message);
    }

    @Override
    public String getPkgId() {
        return mPkgId;
    }

    @Override
    public void setPkgId(String pkgId) {
        mPkgId = pkgId;
    }

    @Override
    public void setManifest(ManifestDoc doc) {
        mManifestDoc = doc;
    }

    @Override
    public Object getManifest(String key) {
        return mManifestDoc.getManifest(key);
    }

    public String pagePathFromId(String pageId) {
        Map<?, ?> map = (HashMap<?, ?>) getManifest("layouts");
        return (String) map.get(pageId.toLowerCase());
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public void setPath(String path) {
        mPath = path;
    }

    @Override
    public boolean isApp() {
        return mApp;
    }

    @Override
    public void setApp(boolean isApp) {
        mApp = isApp;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void attachContext(Context context) {
        mContext = context;
    }

    @Override
    public String getParentId() {
        return mParentId;
    }

    @Override
    public void setParentId(String pkgId) {
        mParentId = pkgId;
    }

    @Override
    public CertHandler getCertHandler() {
        return mCertHandler;
    }

    @Override
    public void bringToFront() {
        if (mContext == null)
            return;
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        Intent intent = new Intent(application.getActivity(), mContext.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        application.getActivity().startActivity(intent);
    }
    
    public String getPkgVersion(){
    	if(mManifestDoc == null)
    		return "";
		return (String) mManifestDoc.getManifest("version");
    }
    
    public void setPkgVersion(String pkgId){
    	
    }
}
