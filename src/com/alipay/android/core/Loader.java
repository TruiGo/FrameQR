/**
 * 
 */
package com.alipay.android.core;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.constant.Constant;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class Loader {
    private AlipayApplication mContext;
    private Engine mSource;
    private String mPkgId;
    private String mPath;
    private ManifestDoc mManifestDoc;

    public Loader(Context context, Engine source, String pkgId, String path) {
        mContext = (AlipayApplication) context.getApplicationContext();
        mSource = source;
        mPkgId = pkgId;
        mPath = path;
        mManifestDoc = new ManifestDoc(mPath);
    }

    public Engine load(String params) {
        Map<String, Class<?>> engines = mContext.getEngines();
        Class<?> clazz = engines.get(mManifestDoc.getType());
        if (clazz == null) {
            mContext.showToast("Can't support this pkg:" + mManifestDoc.getType());
            return null;
        }
        int version = EngineConfig.MAJOR * 100 + EngineConfig.MINOR * 10 + EngineConfig.REVISION;
        if (check(version)) {
            try {
                Engine engine = (Engine) clazz.newInstance();
                engine.setPkgId(mPkgId);
                engine.setPath(mPath);
                engine.setApp(true);
                engine.setManifest(mManifestDoc);
                engine.setParentId(mSource != null ? mSource.getPkgId() : "");
                engine.init(mContext);
                engine.execute(mSource != null ? mSource.getPkgId() : "", MsgAction.ACT_LAUNCH,
                    params);
                return engine;
            } catch (Exception e) {
                mContext.showToast("Can't support this pkg:" + mManifestDoc.getType());
                e.printStackTrace();
            }
        }
        //        mContext.showToast("Can't start this pkg.");
        return null;
    }

    private boolean check(int version) {
        if (!checkSDKVersion(version)) {
            mContext.showToast(mContext.getString(R.string.package_version_lower));
            return false;
        }
        //
        // Check the requisite for this app.
        if (!checkRequisite()) {
            return false;
        }
        return true;
    }

    private boolean checkSDKVersion(int version) {
        Object object = mManifestDoc.getMinSdkVersion();
        if (object != null&&object.toString().length()>0) {
            int major = 0;
            int minor = 0;
            //            int revision;
            String[] versions = object.toString().split("\\.");
            major = Integer.parseInt(versions[0]);
            if (versions.length > 1)
                minor = Integer.parseInt(versions[1]);
            //            if(versions.length>2)
            //                revision = Integer.parseInt(versions[2]);

            if (major * 100 + minor * 10 <= version)
                return true;
        }
        return false;
    }

    private boolean checkRequisite() {
        boolean bcheckRequisite = true;

        Object requisiteObj = mManifestDoc.getRequisite();

        String[] requisite = null;
        if (requisiteObj instanceof String) {
            requisite = new String[1];
            requisite[0] = (String) requisiteObj;
        } else if (requisiteObj instanceof String[]) {
            requisite = (String[]) requisiteObj;
        }

        if (requisite == null)
            ;
        else {
            for (String item : requisite) {
                if (item.equalsIgnoreCase("login")) {
                    bcheckRequisite = mContext.getUserData() != null;
                    //                    bcheckRequisite = appRunTime.login(null);
                    if (!bcheckRequisite) {
                        Activity activity = mContext.getActivity();
                        Intent intent = new Intent(activity, Login.class);
                        intent.putExtra("appId", mPkgId);
                        //                        intent.putExtra(Defines.REFERER, Defines.XML_APP);
                        activity.startActivityForResult(intent, Constant.REQUEST_LOGIN_BACK);
                        break;
                    }
                } else if (item.equalsIgnoreCase("")) {

                }
            }
        }

        return bcheckRequisite;
    }
}
