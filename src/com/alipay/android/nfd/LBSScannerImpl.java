package com.alipay.android.nfd;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.alipay.android.biz.NfdBiz;
import com.alipay.android.security.Des;
import com.alipay.android.util.LogUtil;

/**
 * lbs发现类
 * 
 * @author daping.gp
 * @version $Id: LBSScannerPeerImpl.java, v 0.1 2012-6-25 下午12:05:31 daping.gp Exp $
 */
public class LBSScannerImpl implements ClientScanner {
    private final static String TAG = "LBSScannerPeerImpl";
    private boolean discoverFinished = true;
    LocationManager locManager;
    Context mContext;
    LbsLocation lbsLocation;

    public LBSScannerImpl(Context context) {
        mContext = context;
        lbsLocation = new LbsLocation(context);
    }

    @Override
    public boolean discoverClient(DiscoveredResultSet discoveredResultSet, String bizType) {
        String retStr = "";
        Long starttime = System.currentTimeMillis();
        Location location = lbsLocation.getLocation();
        String cellid = lbsLocation.getCellId();
        
        JSONObject cellJson = null;
        JSONObject cellReqJson = null;
        try {
        	cellJson = new JSONObject(cellid);
        	cellReqJson = new JSONObject();
			cellReqJson.put("majorCell", cellJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        if(location == null && ("".equals(cellid) || "{}".equals(cellid))){
        	setIsDiscoverFinished(true);
            discoveredResultSet.refreshClient(parseLBSRetInfo(retStr));
        	return false;
        }
        
        Long endtime = System.currentTimeMillis();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        LogUtil.logAnyTime("xxxcellTime", String.valueOf(endtime - starttime));
        NfdBiz nfdbiz = new NfdBiz();
        if (location != null) {
            retStr = nfdbiz.getNFDLBSInfo(String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude()), cellReqJson.toString(), bizType);
        } else if(!"".equals(cellid) && !"{}".equals(cellid)){
            retStr = nfdbiz.getNFDLBSInfo(String.valueOf(0), String.valueOf(0), cellReqJson.toString(), bizType);
        }
        setIsDiscoverFinished(true);
        
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof DispatchDiscoveryTaskThread) {
			DispatchDiscoveryTaskThread temp = (DispatchDiscoveryTaskThread) currentThread;
			if (!temp.timeout) {
				discoveredResultSet.refreshClient(parseLBSRetInfo(retStr));
			}
		}
        
        LogUtil.logOnlyDebuggable("xxxx", "====> LBS discover finish --> result : " + retStr);
        return true;
    }

    public Set<String> parseLBSRetInfo(String retStr) {
        Set<String> lbsList = new LinkedHashSet<String>();
        String subStr;
        if (retStr == null || retStr.length() == 0) {
            return lbsList;
        }
        try {
            JSONObject responseJson = new JSONObject(retStr);
            JSONArray retArray = responseJson.getJSONArray("results");
            for (int i = 0; i < retArray.length(); i++) {
                subStr = retArray.optJSONObject(i).getString("userInfo");
                subStr = Des.decrypt(subStr, NFDUtils.ALIPAY_INFO);
                if(subStr==null){
                	continue;
                }
                lbsList.add(subStr);
            }
        } catch (Exception e) {
            LogUtil.logOnlyDebuggable(TAG, e.toString());
            return lbsList;
        }
        return lbsList;
    }

    @Override
    public boolean isDiscoverFinished() {
        return discoverFinished;
    }

    @Override
    public synchronized void setIsDiscoverFinished(boolean finished) {
        discoverFinished = finished;
    }

    @Override
    public void relaseResources() {
        setIsDiscoverFinished(true);
    }
}
