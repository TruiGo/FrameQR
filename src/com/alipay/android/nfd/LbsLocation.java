package com.alipay.android.nfd;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.util.LogUtil;

public class LbsLocation {
    LbsLocationListener lbsNetWork;
    LbsLocationListener lbsGps;
    Location mLocation;
    private Object mlock = new Object();
    HandlerThread mHandlerThread;
    private Context mContext;
    LocationManager locMgr;
    Timer prelbsTimer;
    
    LbsRequestParams lbsParams;
    HandlerThread mAliYunHandlerThread;
    
    public LbsLocation(Context context) {
        mContext = context;
        try {
            locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initLocation() {
        mHandlerThread = new HandlerThread("LbsThread");
        mHandlerThread.start();

        Handler handler = new Handler(mHandlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
					if (locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						lbsNetWork = new LbsLocationListener();
						locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, lbsNetWork);
					}
					if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						lbsGps = new LbsLocationListener();
						locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lbsGps);
					}
					
					timeOutNotity();
                } catch (Exception e) {
                    e.printStackTrace();
                    synchronized (mlock) {
						mlock.notify();
					}
                }
            }

			private void timeOutNotity() {
				if (prelbsTimer != null){
					prelbsTimer.cancel();
					prelbsTimer = null;
				}
				
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        resetLisener();

                        if (prelbsTimer != null){
        					prelbsTimer.cancel();
        					prelbsTimer = null;
        				}
                        synchronized (mlock) {
                            mlock.notify();
                        }
                    }
                };
				
				timer.schedule(task, 1500);
				prelbsTimer = timer;
			}
        });
    }

    private void resetLisener() {
		if (lbsNetWork != null) {
			locMgr.removeUpdates(lbsNetWork);
			lbsNetWork = null;
		}
		if (lbsGps != null) {
			locMgr.removeUpdates(lbsGps);
			lbsGps = null;
		}
	}
    
    public Location getLocation() {
    	try {
            if (locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Location networkLocation = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (!isSignificantlyOlder(networkLocation))
                    mLocation = networkLocation;
            }
            
            if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location gpsLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (!isSignificantlyOlder(gpsLocation))
                    mLocation = gpsLocation;
            }
            
            if(mLocation != null)
            	LogUtil.logOnlyDebuggable("xxxx", "getLastKownLocation==>" + mLocation.getLatitude() + ";" + mLocation.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	initLocation();
            synchronized (mlock) {
                mlock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
    	
    	return mLocation;
    }

    class LbsLocationListener implements LocationListener {
        @Override
        public void onStatusChanged(String provider, int status, android.os.Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        	if(location == null){
				LogUtil.logOnlyDebuggable("xxxx", "onLocationChanged get location==> null");
			}else{
				LogUtil.logOnlyDebuggable("xxxx", "onLocationChanged get location==>"+ location.getLatitude() + ";" + location.getLongitude());
			}
        	
			try {
				if(isBetterLocation(location, mLocation))
					mLocation = location;
				
				if (prelbsTimer != null){
					prelbsTimer.cancel();
					prelbsTimer = null;
				}
				
				resetLisener();
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
                synchronized (mlock) {
                    mlock.notify();
                }
			}
        }
    }

    public String getCellId() {
        if (mContext == null) {
            return "";
        }
        JSONObject requestJson = new JSONObject();
//        JSONObject requestMajorCellJson = new JSONObject();
        // 获取当前的cell id信息
        TelephonyManager tm = (TelephonyManager) mContext
            .getSystemService(Context.TELEPHONY_SERVICE);
        int typeNetwork = tm.getNetworkType();
        int typePhone = tm.getPhoneType();
        String netOperator = tm.getNetworkOperator();
        if (netOperator != null && netOperator.length() >= 5) {
            try {
                requestJson.put("mcc", Integer.valueOf(netOperator.substring(0, 3)));
                requestJson.put("mnc", Integer.valueOf(netOperator.substring(3, 5)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "BaseHelper", "getLocation() typeNetwork="
                                                               + typeNetwork + ", typePhone="
                                                               + typePhone);

        switch (typePhone) {
            case TelephonyManager.PHONE_TYPE_GSM:
                GsmCellLocation gclocation = (GsmCellLocation) tm.getCellLocation();
                if (gclocation != null) {
                    try {
                        requestJson.put("lac", gclocation.getLac());
                        requestJson.put("cid", gclocation.getCid());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 4:// TelephonyManager.PHONE_TYPE_CDMA:
                if (Integer.parseInt(Build.VERSION.SDK) > 4) {
                    try {
                        CellLocation clocation = tm.getCellLocation();
                        if (clocation != null) {
                            Class<?> clazz = clocation.getClass();
                            Method mgbsi = clazz.getMethod("getBaseStationId", (Class[]) null);
                            requestJson.put("cid",
                                (Integer) mgbsi.invoke(clocation, (Object[]) null));// cclocation.getBaseStationId();//基站小区号
                                                                                    // cellId
                            Method mgsi = clazz.getMethod("getSystemId", (Class[]) null);
                            requestJson.put("lac",
                                (Integer) mgsi.invoke(clocation, (Object[]) null));// cclocation.getSystemId();//系统标识
                                                                                   // mobileNetworkCode
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        /*try {
            requestMajorCellJson.put("majorCell", requestJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
//        return requestMajorCellJson.toString();
        return requestJson.toString();
    }
    
    
    private static final int ONE_MINUTES = 1000 * 60 * 1;
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than one minute since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
    // Check whether the new location fix is newer or older
    private boolean isSignificantlyOlder(Location location) {
        if (location == null)
            return true;
        
        long timeDelta = System.currentTimeMillis() - location.getTime();
        boolean isSignificantlyOlder = timeDelta > 10*ONE_MINUTES;
        return isSignificantlyOlder;
    }
    
    public LbsRequestParams getCurrentPositionInfo(){
    	lbsParams = new LbsRequestParams(mContext);
    	try {
            if (locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Location networkLocation = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (!isSignificantlyOlder(networkLocation))
                    mLocation = networkLocation;
            }
            if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location gpsLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (!isSignificantlyOlder(gpsLocation))
                    mLocation = gpsLocation;
            }
            if(mLocation != null)
            	LogUtil.logOnlyDebuggable("xxxx", "getLastKownLocation==>" + mLocation.getLatitude() + ";" + mLocation.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        try {
        	initLocation();
            synchronized (mlock) {
                mlock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
        //gps定位或wifi定位location为null
    	if(mLocation ==null){
            try {
            	getAliYunCellId();
                synchronized (mlock) {
                    mlock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mAliYunHandlerThread != null) {
            	mAliYunHandlerThread.quit();
            }
    	}else{
    		lbsParams.setmLatitude(mLocation.getLatitude());
    		lbsParams.setmLongitude(mLocation.getLongitude());
    	}
    	return lbsParams;
    
    }
    //获取阿里云的参数
	public void getAliYunCellId() {
		mAliYunHandlerThread = new HandlerThread("AliThread");
		mAliYunHandlerThread.start();
		Handler handler = new Handler(mAliYunHandlerThread.getLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				PhoneInfoManager phoneManager = PhoneInfoManager.getInstance();
				try{
				lbsParams.setmApiVer("1");
				phoneManager.init(mContext);
				TelephonyManager telManager = phoneManager
						.getTelephonyManager();
				if(telManager.getSimState()!=TelephonyManager.SIM_STATE_ABSENT){
				switch (telManager.getPhoneType()) {
				case TelephonyManager.PHONE_TYPE_GSM:
					lbsParams.setmNetworkType("GSM");
					break;
				case TelephonyManager.PHONE_TYPE_CDMA:
					lbsParams.setmNetworkType("CDMA");
					break;
				}
				lbsParams.setmNetworkOperator(telManager.getNetworkOperator());
				
				}
				lbsParams.setmUserAgent(Build.BRAND + " " + Build.MODEL
						+ " " + Build.VERSION.RELEASE);
				setWifiInfo(phoneManager);
				CellLocation location = telManager.getCellLocation();
				setCellIdInfo(phoneManager,telManager,location);
				// CDMA信息
				setCdmaInfo(phoneManager,telManager,location);
				// return aliLocData;
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					phoneManager.kill();
					AliYunNotity();
				}
			}
		});
	}
	//设置cdma信息
	private void setCdmaInfo(PhoneInfoManager phoneManager,TelephonyManager telManager,CellLocation location){
		JSONArray tempArray = new JSONArray();
		if (location != null && location instanceof CdmaCellLocation && telManager.getSimState()!=TelephonyManager.SIM_STATE_ABSENT) {
			JSONObject cdmaJson = new JSONObject();
			CdmaCellLocation curCDMA = (CdmaCellLocation) location;
			try {
				cdmaJson.put("sid", curCDMA.getSystemId());
				cdmaJson.put("nid", curCDMA.getNetworkId());
				cdmaJson.put("bsid", curCDMA.getBaseStationId());
				cdmaJson.put("rssi", phoneManager.getInstance()
						.getCDMAStrength());
				tempArray.put(cdmaJson);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lbsParams.setmCdmaInfo(tempArray);
		}
	}
	//设置基站信息
	private void setCellIdInfo(PhoneInfoManager phoneManager,
			TelephonyManager telManager, CellLocation location) {
		// 基站信息
		JSONObject tempObject = new JSONObject();
		String netOperator = telManager.getNetworkOperator();
		if (location != null && location instanceof GsmCellLocation && telManager.getSimState()!=TelephonyManager.SIM_STATE_ABSENT) {
			GsmCellLocation gsmLocation = (GsmCellLocation) location;
			JSONObject mainCellIdJson = new JSONObject();
			if (netOperator != null && netOperator.length() >= 5) {
				try {
					mainCellIdJson.put("mcc",
							Integer.valueOf(netOperator.substring(0, 3)));
					mainCellIdJson.put("mnc",
							Integer.valueOf(netOperator.substring(3, 5)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				mainCellIdJson.put("cid", gsmLocation.getCid());
				mainCellIdJson.put("lac", gsmLocation.getLac());
				mainCellIdJson.put("rssi", phoneManager.getCellStrength());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				tempObject.put("majorCell", mainCellIdJson);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray jsonArray = new JSONArray();
			List<NeighboringCellInfo> ncis = telManager
					.getNeighboringCellInfo();
			try {
				for (int i = 0; ncis != null && i < ncis.size() && i < 20; i++) {
					NeighboringCellInfo nci = ncis.get(i);
					mainCellIdJson = new JSONObject();
					mainCellIdJson.put("mcc",
							Integer.valueOf(netOperator.substring(0, 3)));
					mainCellIdJson.put("mnc",
							Integer.valueOf(netOperator.substring(3, 5)));
					mainCellIdJson.put("lac", nci.getLac());
					mainCellIdJson.put("cid", nci.getCid());
					mainCellIdJson.put("rssi", nci.getRssi());
					jsonArray.put(mainCellIdJson);
				}
				tempObject.put("neighborCells", jsonArray);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lbsParams.setmCellId(tempObject);
		}
	}
    //设置wifi信息
	public void setWifiInfo(PhoneInfoManager phoneManager){
		WifiManager wifiManager = phoneManager.getWifiManager();
		if(!wifiManager.isWifiEnabled()){
			return;
		}
		// wifi信息
		JSONArray wifiArray = new JSONArray();
		List<ScanResult> srs = phoneManager.getWifiItems();//.getScanResults();
		for (int i = 0; srs != null && i < srs.size() && i < 20; i++) {
			JSONObject wifiJson = new JSONObject();
			ScanResult sr = srs.get(i);
			String mac = sr.BSSID.replace(":", "");
			if (mac.length() != 12)
				continue;
			try {
				wifiJson.put("mac", sr.BSSID);
				wifiJson.put("rssi", sr.level);
				wifiArray.put(wifiJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		lbsParams.setmWifiInfo(wifiArray);
	}
	
	
    private void  AliYunNotity() {
        synchronized (mlock) {
            mlock.notify();
        }
    }
}
