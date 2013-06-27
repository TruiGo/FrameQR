/**
 * 
 */
package com.alipay.android.core.webapp.api;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author sanping.li
 *
 */
public class GeoLocation extends Plugin {

    @Override
    public PluginResult execute(String action, JSONArray args, String callbackId) {
        PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        LocationManager locationManager = (LocationManager) mPage.getContext()
            .getSystemService(Context.LOCATION_SERVICE);
        if (action.equals("getLastLocation")) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                return new PluginResult(status, getPosition(location));
            }
        }

        return new PluginResult(status, result);
    }

    @Override
    public boolean isSynch(String action) {
        return true;
    }
    private JSONObject getPosition(Location location) {
        JSONObject position = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitude", location.getLatitude());
            obj.put("longitude", location.getLongitude());
            obj.put("altitude", location.hasAltitude()?location.getAltitude():JSONObject.NULL);
            obj.put("accuracy", location.hasAccuracy()?location.getAccuracy():JSONObject.NULL);
            obj.put("heading", location.hasBearing()?location.getBearing():JSONObject.NULL);
            obj.put("speed", location.hasSpeed()?location.getSpeed():JSONObject.NULL);
            obj.put("altitudeAccuracy", JSONObject.NULL);
            
            position.put("coords", obj);
            position.put("timestamp", location.getTime());
        } catch (JSONException e) {
            // Should never happen
        }
        
        return position;
    }
}
