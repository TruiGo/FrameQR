package com.alipay.android.nfd;

import java.util.HashMap;

import com.alipay.android.util.LogUtil;

public class ShakeProcessor {
	private HashMap<String, SensorData> mDataMap = new HashMap<String, SensorData>();
	
	public ShakeProcessor() {
		initData("X");
		initData("Y");
		initData("Z");
	}

	private void initData(String type) {
		if("X".equals(type)){
			mDataMap.put("minX", new SensorData());
			mDataMap.put("maxX", new SensorData());
		}else if("Y".equals(type)){
			mDataMap.put("minY", new SensorData());
			mDataMap.put("maxY", new SensorData());
		}else if("Z".equals(type)){
			mDataMap.put("minZ", new SensorData());
			mDataMap.put("maxZ", new SensorData());
		}
	}

	public boolean addSensorData(SensorData sensorData, String type) {
		processUpdate(sensorData, mDataMap.get("min" + type),mDataMap.get("max" + type));
		
		if(isHit()){
			initData("X");
			initData("Y");
			initData("Z");
			return true;
		}
		return false;
	}

	private void processUpdate(SensorData sdNow, SensorData sdMIn,
			SensorData sdMax) {
		if (sdNow.data < 0) {
			if (sdNow.data < sdMIn.data) {
				sdMIn.data = sdNow.data;
				sdMIn.time = sdNow.time;
			}
		} else {
			if (sdNow.data > sdMax.data) {
				sdMax.data = sdNow.data;
				sdMax.time = sdNow.time;
			}
		}
	}
	
	private final long DELTAINTERVAL = 1500;
	private final long DELTAA = 3;
	private final long A = 11;
	private boolean isHit(String type){
		SensorData sdMIn = mDataMap.get("min" + type);
		SensorData sdMax = mDataMap.get("max" + type);
		
		long timeInterval = Math.abs(sdMax.time - sdMIn.time);
		if(timeInterval < DELTAINTERVAL && timeInterval > 500){
//			LogUtil.logOnlyDebuggable("xxxxxx", "data ==>" + (Math.abs(Math.abs(sdMIn.data) - Math.abs(sdMax.data))) + ";A ==>" + Math.abs(sdMIn.data));
			if(/*Math.abs(Math.abs(sdMIn.data) - Math.abs(sdMax.data)) < DELTAA &&*/ Math.abs(sdMIn.data) > A || Math.abs(sdMax.data) > A){
				return true;
			}
		}else if(timeInterval > DELTAINTERVAL){
			initData(type);
		}
		return false;
	}
	
	private boolean isHit(){
		return isHit("X") || isHit("Y") || isHit("Z");
	}
}


