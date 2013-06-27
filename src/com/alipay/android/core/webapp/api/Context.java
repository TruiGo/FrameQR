package com.alipay.android.core.webapp.api;

import java.net.URLDecoder;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.common.data.UserData;
import com.alipay.android.core.Engine;
import com.alipay.android.core.MsgAction;

public class Context extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        if (action.equalsIgnoreCase("startApp")) {
            String params, packageId;
            try {
                packageId = args.getString(0);
                params = args.getString(1);
                ((AlipayApplication) mPage.getContext().getApplicationContext()).getMBus().sendMsg(
                    mPage.getRunTime().getPkgId(), packageId, MsgAction.ACT_LAUNCH, params);
                mPage.getRunTime().setActivityResultCallback(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equalsIgnoreCase("quit")) {
            mPage.getRunTime().exit();
        } else if (action.equalsIgnoreCase("getInstalledPath")) {
            result = mPage.getRunTime().getPath();
        } else if (action.equalsIgnoreCase("getPkgId")) {
            result = mPage.getRunTime().getPkgId();
        } else if (action.equalsIgnoreCase("getServerPort")) {
            result = mPage.getRunTime().getWebServer().getPort() + "";
        } else if (action.equalsIgnoreCase("getFrompop")) {
            result = mPage.getRunTime().getPopParam();
            if (mPage.getRunTime().getPopParam()!=null)
                mPage.getRunTime().setPopParam(null);
        } else if (action.equalsIgnoreCase("setFrompop")) {
            String param="";
            try {
                if(args.length()>0)
                    param = args.getString(0);
                mPage.getRunTime().setPopParam(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals("isLogin")) {
            AlipayApplication application = (AlipayApplication) mPage.getContext()
                .getApplicationContext();
            UserData userData = application.getUserData();
            result = userData == null ? "false" : "true";
        } else if (action.equals("getLoginUsers")) {
            DBHelper dbHelper = new DBHelper(mPage.getContext());
            JSONArray array = dbHelper.getAllUser(Login.LOGINTYPE_ALIPAY);
            String strUser = array == null ? "[]" : array.toString();
            sendJavascript("cordova.require('cordova/plugin/context').loadCallBack('success','"
                           + strUser + "');");
        } else if (action.equals("startExtApp")) {            	
        	try{
        		//com.ddmap.android.privilege://alipay?alitoken=1234567890
        		String pkgUri = args.getString(0);	
        		String packageName = pkgUri;
        		String paramStr = null;          	
        		
        		int pkgPos = pkgUri.indexOf("://alipay");
        		if ( pkgPos != -1){       	
	        		packageName = pkgUri.substring(0, pkgPos);
	        		paramStr = pkgUri.substring(pkgPos + "://alipay".length());
	        		
	        		if (paramStr.startsWith("?"))
	        			paramStr = paramStr.substring(1);
	        		else
	        			paramStr = null;
        		}
	        	
	            AlipayApplication application = (AlipayApplication) mPage.getContext()
	                    .getApplicationContext();
	            
	            PackageManager pm = application.getPackageManager();
	            Intent intent = pm.getLaunchIntentForPackage(packageName);
	            if (packageName != null && intent != null){
	            	if (paramStr != null){
            			String[] paramList = paramStr.split("&");
            		    for(String nvStr:paramList){
            		    	String[] nv=nvStr.split("=");
            		          if(nv.length>1){
            		        	  intent.putExtra(nv[0], URLDecoder.decode(nv[1], "utf-8"));            		        	  
            		          }
            		    }
	            	}
	            	application.startActivity(intent);
	            }
	            else{
	            	status = PluginResult.Status.ERROR;
	            }
        	}
        	catch (Exception e) {
        		status = PluginResult.Status.ERROR;
                e.printStackTrace();
            }     
        	
        } else if (action.equals("isExtAppInstalled")) {
        	try{
	        	String packageName = args.getString(0);
	        	
	            AlipayApplication application = (AlipayApplication) mPage.getContext()
	                    .getApplicationContext();
	            
	            PackageManager pm = application.getPackageManager();
	            
	            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);	            
	            
	            if (packageInfo == null){
	            	status = PluginResult.Status.ERROR;
	            }
        	}catch (Exception e) {
        		status = PluginResult.Status.ERROR;
                e.printStackTrace();
            }   
        }       
        
        return new PluginResult(status, result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Engine.RESULT_OK) {
            sendJavascript("cordova.require('cordova/plugin/context').callback('success');");
        } else {
            sendJavascript("cordova.require('cordova/plugin/context').callback('error');");
        }
    }

    @Override
    public boolean isSynch(String action) {
        if (action.equalsIgnoreCase("getInstalledPath") || action.equalsIgnoreCase("getFrompop")
            || action.equalsIgnoreCase("getPkgId") || action.equalsIgnoreCase("isLogin")
            || action.equalsIgnoreCase("getLoginUsers") || action.equalsIgnoreCase("getServerPort")
            || action.equalsIgnoreCase("quit"))
            return true;
        return super.isSynch(action);
    }
}
