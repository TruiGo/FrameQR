/**
 * 
 */
package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import com.alipay.android.client.RootActivity;
import com.alipay.android.log.AlipayLogAgent;

/**
 * @author sanping.li
 *
 */
public class Analysis extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        RootActivity activity = (RootActivity) mPage.getContext();
        if (action.equals("jumpPage")) {
        } else if (action.equals("event")) {
            String[] extendParams = null;
            
            String behaviourID = args.optString(0);
            String behaviourStatus = args.optString(1);
            String statusMessage = args.optString(2);
            String params = args.optString(3);
            if (params != null && params.length() > 0) {
                extendParams = params.split("\\|");
            }
        	AlipayLogAgent.writeLog(activity, behaviourID, behaviourStatus, 
        			statusMessage, mPage.getRunTime().getPkgId(),
        			(String)mPage.getRunTime().getPkgVersion(), extendParams);
        }

        return new PluginResult(status, result);
    }
}