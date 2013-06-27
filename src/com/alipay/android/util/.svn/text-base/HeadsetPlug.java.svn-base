package com.alipay.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

public class HeadsetPlug {
	   protected HeadsetPlugReceiver headsetPlugReceiver           = null;
	    protected Context             contextOfHeadsetPlugReceiver 	= null;
	    private boolean hadRegisted;

	    private HeadsetCallBack mHeadsetCallback;
	    
	    public interface HeadsetCallBack{
	    	void onHeadsetOn();
	    	void onHeadsetOff();
	    }
	    
	    /**
	     * 检测外放耳麦插入、拔出的监听者类
	     */
	    protected class HeadsetPlugReceiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            if (intent.hasExtra("state")) {
	                if (intent.getIntExtra("state", 0) == 0) 
	                {
	                	// 耳机被拔出
	                	// 做相应业务处理
	                	if(mHeadsetCallback != null){
	                		mHeadsetCallback.onHeadsetOff();
	                	}
	                } 
	                else
	                {
	                	// 耳机被插入
	                	// 做相应业务处理
	                	if(mHeadsetCallback != null){
	                		mHeadsetCallback.onHeadsetOn();
	                	}
	                }
	            }
	        }
	    }
	    
	    /**
	     * 直接判断是否插入了外放耳麦。该函数的返回值可能不准确，建议结合  registerHeadsetPlugReceiver 方式
	     * @param context
	     * @return 是否插入了外放耳麦
	     */
	    public boolean isHeadsetOn(Context context)
	    {
	    	boolean isOn = false;
	        try
	        {
	            if (((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).isBluetoothA2dpOn())
	            	isOn = true;
	            else
	            	isOn = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isWiredHeadsetOn();
	        } 
	        catch (Exception e) 
	        {
	        	e.printStackTrace();
	        }
	        return isOn;
	    }
	    
	    /**
	     * 注册耳机拔出、插入的Receiver
	     * @param context
	     */
	    public void registerHeadsetPlugReceiver(Context context) 
	    {
	    	// 先反注册之前的那个
	    	unRegisterHeadsetPlugReceiver();
	    	
	        try 
	        {
	            // 注册外放耳机是否插入的监听者
	        	contextOfHeadsetPlugReceiver = context;
	            headsetPlugReceiver = new HeadsetPlugReceiver();
	            IntentFilter intentFilter = new IntentFilter();
	            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
	            contextOfHeadsetPlugReceiver.registerReceiver(headsetPlugReceiver, intentFilter);
	        } catch (Exception e) 
	        {
	        	e.printStackTrace();
	        }
	    }
	    
	    public void registerHeadsetCallback(HeadsetCallBack headsetCallback){
	    	if(headsetCallback != null){
	    		mHeadsetCallback = headsetCallback;
	    		hadRegisted = true;
	    	}
	    }
	    
	    public boolean hadRegisted(){
	    	return hadRegisted;
	    }
	    
	    /**
	     * 反注册耳机拔出、插入的Receiver
	     */
	    public void unRegisterHeadsetPlugReceiver()
	    {
	        if (contextOfHeadsetPlugReceiver != null) 
	        {
	            try 
	            {
	            	contextOfHeadsetPlugReceiver.unregisterReceiver(headsetPlugReceiver);
	            	contextOfHeadsetPlugReceiver = null;
	            	mHeadsetCallback = null;
	            	hadRegisted = false;
	            }
	            catch (Exception e)
	            {
	            	e.printStackTrace();
	            }
	        }
	        
	    }
	
	

}
