/**
 * 
 */
package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.alipay.android.client.RootActivity;

import android.app.Activity;
import android.content.Intent;

/**
 * @author sanping.li
 *
 */
public class Preference extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        if (action.equals("startGeoSetting")) {
            Activity activity = mPage.getContext();
            if (activity instanceof RootActivity) {
            	RootActivity rootActivity = (RootActivity)activity;
            	rootActivity.countMeNotTemporary(true);
            }
            
            Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
            mPage.getContext().startActivity(intent);
        }

        return new PluginResult(status, result);
    }

}
