package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.common.data.UserData;

public class AlipaySession extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        AlipayApplication application = (AlipayApplication) mPage.getContext().getApplicationContext();
        String result = "";

        if (action.equals("sessionId")) {
            result = application.getConfigData().getSessionId();
        } else if (action.equals("clientId")) {
            result = application.getConfigData().getClientId();
        }  else if (action.equals("token")) {
            UserData userData = application.getUserData();
            result=userData==null?"":userData.getToken();
        }  else if (action.equals("extToken")) {
            UserData userData = application.getUserData();
            result=userData==null?"":userData.getExtToken();
        } else{
            status = PluginResult.Status.INVALID_ACTION;
        }
        return new PluginResult(status, result);
    }

    @Override
    public boolean isSynch(String action) {
        return true;
    }
}
