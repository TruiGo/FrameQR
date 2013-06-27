/**
 * 
 */
package com.alipay.platform.core;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;

import com.alipay.android.net.NetworkManager;
import com.alipay.android.net.http.DataParser;
import com.alipay.android.net.http.HttpManager;

/**
 * @author sanping.li@alipay.com
 * MVC-C
 *
 */
public class Controller {
	/**
	 * 命令处理器
	 */
	private CommandHandler mHandler;
	/**
	 * 网络模块
	 */
	private NetworkManager mNetworkManager;
	/**
	 * 数据解析
	 */
	private DataParser mDataPaser;
	
	
	public Controller(Context context) {
		mNetworkManager = new HttpManager(context);//http网络连接
		

		HandlerThread handlerThread = new HandlerThread("controller");
		handlerThread.start();
		mHandler = new CommandHandler(handlerThread.getLooper(),this);
	}


	public void excute(Command command){
		Message msg = mHandler.obtainMessage(0, command);
		mHandler.sendMessage(msg);
	}


    public NetworkManager getNetworkManager() {
		return mNetworkManager;
	}


	public void setNetworkManager(NetworkManager networkManager) {
		this.mNetworkManager = networkManager;
	}

	public void setDataPaser(DataParser dataPaser) {
		this.mDataPaser = dataPaser;
	}
	
	public DataParser getDataPaser() {
		return mDataPaser;
	}

}
