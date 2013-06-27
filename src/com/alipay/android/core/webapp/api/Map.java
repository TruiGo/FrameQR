/**
 * 
 */
package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;

import com.alipay.android.client.RootActivity;
import com.alipay.android.core.webapp.Page;
import com.alipay.android.map.IMap;
import com.alipay.android.map.MapManager;

/**
 * @author sanping.li
 *
 */
public class Map extends Plugin {
    private MapManager mMapManager;
    
    @Override
    public void setContext(Page page) {
        super.setContext(page);
        mMapManager = new MapManager(mPage.getContext());
    }

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
    	
        Activity activity = mPage.getContext();
        if (activity instanceof RootActivity) {
        	RootActivity rootActivity = (RootActivity)activity;
        	rootActivity.countMeNotTemporary(true);
        }
        
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";
        
        IMap mapProvider= mMapManager.getMap();

        if (action.equals("location")) {
            try {
                String geoPoint = args.getString(0);
                String label =  args.getString(1);
                if(label==null||label.length()<=0)
                    label = "*";
                mapProvider.location(geoPoint,label);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (action.equals("route")) {
            try {
                String srcGeoPoint = args.getString(0);
                String destGeoPoint = args.getString(1);
                String mode = args.getString(2);
                mapProvider.route(srcGeoPoint, destGeoPoint, mode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        } 
        
        return new PluginResult(status, result);
    }
    

}
