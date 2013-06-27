/**
 * 
 */
package com.alipay.platform.view;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import com.alipay.android.net.RequestMaker;
import com.alipay.platform.core.AlipayApp;
import com.alipay.platform.core.Command;
import com.alipay.platform.core.Controller;
import com.alipay.platform.core.Module;

/**
 * @author sanping.li@alipay.com
 *
 */
public class ActivityMediator {
    public static final String ACT_CLEAN = "clean";
    public static final String ACT_CACHE = "cache";
    public static final String ACT_APPEND = "append";
    
	private OnDataChangeListener mCallBack;
	private ActivityHandler mHandler;
	
	private Controller mController;
	

	public ActivityMediator(OnDataChangeListener callBack) {
		super();
        mCallBack = callBack;
        mHandler = new ActivityHandler();

		AlipayApp alipayApp = (AlipayApp) this.mCallBack.getContext().getApplicationContext();
		mController = alipayApp.getController();
	}


	public ActivityHandler getHandler() {
		return mHandler;
	}

	/**
	 * 读取网络数据
	 */
	public void sendCommand(String DataId,String operator,RequestMaker mRequestMaker,ArrayList<String> params){
		Command command = new Command(new Messenger(mHandler),DataId);
		command.appendAction(Module.CTR_NET<<Command.ACT_READ);
		command.setRequestParam(params);
		command.setOperator(operator);
        mRequestMaker.makeRequest(command);//构造请求    result--> command.setRequestData(refinedInfo);
		mController.excute(command);
	}
	
	/**
	 * 读取网络媒体数据-图片等
	 */
	public void sendCommand(String url,String operator,ArrayList<String> params,int mimeType){
        Command command = new Command(new Messenger(mHandler),null);
        command.appendAction(Module.CTR_NET<<Command.ACT_READ);
        command.setOperator(operator);
        command.setMimeType(mimeType);
        command.setRequestParam(params);
        command.setRequestUrl(url);
        mController.excute(command);
	}
	
	/**
	 * 读取本地文件
	 */
	public void sendCommand(String url,String operator,int mimeType){
        Command command = new Command(new Messenger(mHandler),null);
        command.appendAction(Module.CTR_FILE<<Command.ACT_READ);
        command.setOperator(operator);
        command.setMimeType(mimeType);
        command.setRequestUrl(url);
        mController.excute(command);
	}

	public class ActivityHandler extends Handler {

        @Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Command command = (Command) msg.obj;
			switch(command.getState()){
			case Command.STATE_COMPLETED:
				mCallBack.onComplete(command);
				break;
			case Command.STATE_CANCELED:
				if(!mCallBack.preCancel(command))
					mCallBack.onCancel(command);
				break;
			case Command.STATE_FAILED:
				if(!mCallBack.preFail(command))
					mCallBack.onFail(command);
				break;
			}
		}
		
	};
}
