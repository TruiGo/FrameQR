package com.alipay.android.core.webapp.api;


import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.util.Base64;

import com.alipay.android.client.RootActivity;
import com.alipay.android.core.ActivityShell;
import com.alipay.android.core.ParamString;
import com.alipay.android.core.webapp.WebAppRunTime;

public class Navigation extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        RootActivity activity = (RootActivity) mPage.getContext();
        try {
            if (action.equals("pushWindow")) {
                String url = args.getString(0);
                Intent intent = new Intent(activity, ActivityShell.class);
                intent.putExtra("id", mPage.getRunTime().getPkgId());
                WebAppRunTime appRunTime = mPage.getRunTime();
                
                ParamString paramString = new ParamString("");
                if(appRunTime.getFrom()!=null){
                    paramString.addPair("_source_",appRunTime.getFrom());
                }
                paramString.addPair("entry",Base64.encodeToString(url.getBytes(), Base64.DEFAULT));
                intent.putExtra("data",paramString.toString());
                activity.startActivity(intent);
            } else if (action.equals("popWindow")) {
                mPage.endActivity();
            } 
            return new PluginResult(status, result);
        } catch (JSONException e) {
            return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
        }
    }

}
