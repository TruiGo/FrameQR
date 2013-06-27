package com.alipay.android.nfd;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.alipay.android.biz.NfdBiz;

/**
 * lbs注册类
 * 
 * @author daping.gp
 * @version $Id: LBSObserverImpl.java, v 0.1 2012-6-25 上午11:38:02 daping.gp Exp
 */
public class LBSObserverPeerImpl implements ClientObserverPeer {
    LocationManager locManager;
    Context mContext;
    LbsLocation lbsLocation;

    public LBSObserverPeerImpl(Context context) {
        mContext = context;
        lbsLocation = new LbsLocation(context);
    }

    @Override
    public boolean regist(String clientInfo, int type, String bizType) {
        if ((type & RegistType.LBSTYPE.getCode()) == 0) {
            return false;
        }
        Location location = lbsLocation.getLocation();
        String cellId = lbsLocation.getCellId();
        JSONObject cellJson = null;
        JSONObject cellReqJson = null;
        try {
        	cellJson = new JSONObject(cellId);
        	cellReqJson = new JSONObject();
			cellReqJson.put("majorCell", cellJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        NfdBiz nfdbiz = new NfdBiz();
        if (location != null) {
            return nfdbiz.reportNFDLBSInfo(String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude()), clientInfo, cellReqJson.toString(), bizType);
        }else if(!"".equals(cellId) && !"{}".equals(cellId)){
        	return nfdbiz.reportNFDLBSInfo(String.valueOf(0), String.valueOf(0), clientInfo, cellReqJson.toString(),
        			bizType);
        }
        return false;
    }

    @Override
    public boolean unRegist() {
        return true;
    }

}
