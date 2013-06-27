package com.alipay.android.util;

import android.content.Context;
import android.util.Log;

import com.alipay.android.client.constant.Constant;

public class LogUtil {
    private static Context sContext;
    
    public static void init(Context context){
        sContext = context;
    }
	/**
	 * output the log only in debuggable mode
	 * @param tag Tag for the log
	 * @param info log info
	 */
	public static void logOnlyDebuggable(String tag,String info){
		if(Constant.getLog(sContext)){
			Log.d("Alipay_" + tag, info);
		}
	}
	
	/**
     * output the log only in debuggable mode
     * @param tag Tag for the log
     * @param info log info
     */
    public static void logContainerDebuggable(String tag,String info){
        if(Constant.getLog(sContext)){
            Log.e("Alipay_" + tag, info);
        }
    }
	
	/**
	 * output the log in any mode (debug and normal)
	 * @param tag Tag for the log
	 * @param info log info
	 */
	public static void logAnyTime(String tag,String info){
		Log.v("Alipay_" + tag, info);
	}
	
	/**
	 * log the error
	 * @param tag
	 * @param info
	 */
	public static void logAnyTime(String tag,String info,Exception ex){
		Log.e("Alipay_" + tag, info , ex);
	}

	/**
	 * log the info according to log level
	 * @param level log level (i,v,d,w,e)
	 * @param tag 
	 * @param info
	 */
	public static void logMsg(int level, String tag, String info) {
		if (Constant.LOG_LEVEL >= level) {
			switch(level) {
				case 1:
					logAnyTime(tag, info);
					break;
				case 2:
					logAnyTime(tag, info);
					break;
				case 3:
					logOnlyDebuggable(tag, info);
					break;
				case 4:
					logOnlyDebuggable(tag, info);
					break;
				case 5:
					logOnlyDebuggable(tag, info);
					break;
			}
		}
	}
}
