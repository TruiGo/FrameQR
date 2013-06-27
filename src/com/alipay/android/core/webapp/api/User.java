package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.common.data.UserData;

public class User extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        AlipayApplication application = (AlipayApplication) mPage.getContext().getApplicationContext();
        UserData userData = application.getUserData();
        String result = "";

        if (action.equals("account")) {
            result=userData==null?"":userData.getAccountName();
        }else if(action.equals("mobileNo")){
            result=userData==null?"":userData.getMobileNo();
        }else if(action.equals("balance")){
            result=userData==null?"":userData.getAvailableBalance();
        }else if(action.equals("name")){
            result=userData==null?"":userData.getRealName();
        }else if(action.equals("userId")){
            result=userData==null?"":userData.getUserId();
        }else if(action.equals("messageCount")){
            result=userData==null?"0":userData.getMsgCount()+"";
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
