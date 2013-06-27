package com.alipay.android.push.util.mock;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import com.alipay.android.push.data.PubMsgRecord;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;

import android.os.Environment;
import android.util.Log;

/** 
 * Utility class to test config variable access point.
 */
public class testAPNConfig {
	private static final String LOGTAG = LogUtil.makeLogTag(PubMsgRecord.class);

	//
	private static String pathName="/sdcard/alipay/";   
	private static String fileName="apnConfig";  
	
	public static String APN_DEFAULT_NAME = "wifi";

	
	public static String getTestAPNName() {
    	File file = null;
    			
    	String sdStatus = Environment.getExternalStorageState();   
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {   
            Log.w("testAPNConfig", "SD card is not avaiable/writeable right now.");   
            return APN_DEFAULT_NAME;   
        }

        try { 
            File path = new File(pathName);
            if( !path.exists()) {   
                Log.d("testAPNConfig", "Not find the path:" + pathName);   
                return APN_DEFAULT_NAME;     
            } 
            
            file = new File(pathName + fileName); 
            LogUtil.LogOut(4, LOGTAG, "getTestAPNName() pathName:"+pathName +", fileName:"+fileName);
            if (file.exists() && file.length() > 0) {
            	// 获取其中的id标示列表
	        	FileInputStream inputStream = new FileInputStream(file);
	        	byte[] bs;
	            bs = new byte[inputStream.available()];
	            inputStream.read(bs);
	            
	            String apnName = new String(bs, "utf-8");
	            LogUtil.LogOut(4, LOGTAG, "getTestAPNName() apnName:"+apnName);
	            
	            if(apnName.trim().length() == 0) {
	            	apnName = APN_DEFAULT_NAME;
	            }
	            inputStream.close();
	            
            	return apnName;
            } else {
            	Log.d("testAPNConfig", "Not find the file:" + fileName);   
                return APN_DEFAULT_NAME;   
            }
               
        } catch(Exception e) {   
            Log.w("RecordtoFile", "Error on getTestAPNName.");   
            e.printStackTrace(); 
            return APN_DEFAULT_NAME;
        }
    }

}
