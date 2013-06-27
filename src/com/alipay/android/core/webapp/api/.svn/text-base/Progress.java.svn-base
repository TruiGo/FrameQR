/**
 * 
 */
package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

/**
 * @author sanping.li
 *
 */
public class Progress extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        if (action.equals("open")) {
            final String msg = args.optString(0);
            mPage.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPage.getRunTime().openProgress(mPage.getContext(),msg,false);
                }
            });
        } else if (action.equals("close")) {
            mPage.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPage.getRunTime().closeProgress();
                }
            });
        }
        return new PluginResult(status, result);
    }

}
