package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

public class Header extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        if (action.equalsIgnoreCase("init")) {
            if (args.optString(0) != null && args.optString(0).length() > 0) {
                String str = args.optString(1);
                if (str.endsWith(";"))
                    str = str.substring(0, str.length() - 1);
                String title = "";
                String onclick = "";
                String icon = "";
                if(str.length()>0){
                    String[] strs = str.split(",");
                    for (String s : strs) {
                        if (s.trim().toLowerCase().startsWith("title")) {
                            title = s.split("=")[1];
                        } else if (s.trim().toLowerCase().startsWith("onclick")) {
                            onclick = s.split("=")[1];
                        } else if (s.trim().toLowerCase().startsWith("icon")) {
                            icon = s.split("=")[1];
                        }
                    }
                }
                mPage.getTitleBar().initBarItem(args.optString(0), title, onclick,icon);
            }
        } else if (action.equalsIgnoreCase("clear")) {
            mPage.getTitleBar().clearBarItem();
        } else if (action.equalsIgnoreCase("setTitle")) {
            if (args.optString(0) != null && args.optString(0).length() > 0) {
                mPage.getTitleBar().setTitle(args.optString(0), args.optString(1));
            }
        } else if (action.equalsIgnoreCase("setIcon")) {
            if (args.optString(0) != null && args.optString(0).length() > 0) {
                mPage.getTitleBar().setIcon(args.optString(0), args.optString(1));
            }
        } else if (action.equalsIgnoreCase("setCenterIndicator")) {
            boolean visiable = args.optBoolean(0);
            mPage.getTitleBar().setCenterIndicator(visiable);
        } else {
            status = PluginResult.Status.INVALID_ACTION;
        }
        return new PluginResult(status, result);
    }

}
