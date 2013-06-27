/**
 * 
 */
package com.alipay.platform.core;

import java.util.HashMap;

import android.app.Application;

/**
 * @author sanping.li@alipay.com
 *
 */
public abstract class AlipayApp extends Application {
	/**
	 * 控制器，总线，消息分发和处理
	 */
	protected Controller mController;
	
    
	/**
	 * 全局数据
	 */
	protected HashMap<String, Object> mDatas = null;
    protected HashMap<String, Object> mInfos = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mController = new Controller(this);
		
        mDatas = new HashMap<String, Object>();
        mInfos = new HashMap<String, Object>();
		
		onInital();
	}

	public Controller getController() {
		return mController;
	}



    public void putInfoData(String key,Object value){
        mInfos.put(key, value);
    }
    
    public Object getInfoData(String key){
        return mInfos.get(key);
    }

    public void removeInfoData(String key){
        mInfos.remove(key);
    }

    public void putGlobalData(String key,Object value){
        mDatas.put(key, value);
    }
    
    public Object getGlobalData(String key){
        return mDatas.get(key);
    }

    public void removeGlobalData(String key){
        mDatas.remove(key);
    }
    
    public void clearGloalData() {
        mDatas.clear();
    }
    
	/**
	 * 初始化
	 */
	protected abstract void onInital();
	/**
	 * 返回orm-mapping文件
	 */
	public abstract int getOrmMapping();

}
