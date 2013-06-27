package com.alipay.android.log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.http.HttpClient;
import com.alipay.android.util.LogUtil;

import android.content.Context;

public class LogSendManager extends Thread{
	private static Context mContext;
//	private String m_Errorstr;
	LogSendManager(Context context/*, String errorstr*/){
		mContext = context;
//		m_Errorstr = errorstr;
	}
	public void run(){
		SendLog(mContext);
	}
	/*
	 * 发送日志
	 */
	public static boolean SendLog(Context context){	
		boolean ret = false;
		synchronized(Constants.lock){
			String logStr = LogBaseHelper.readFile(context, Constants.LOGFILE_PATH + Constants.LOGFILE_NAME);
//			if(logStr!=null){
				try{
					HttpClient hc = new HttpClient(Constant.getStatisticsUrl(context), mContext);
					HttpResponse httpResponse = hc.sendGZipSynchronousRequest(logStr);
					if(httpResponse!=null){
						HttpEntity he = httpResponse.getEntity();
						String response = EntityUtils.toString(he);
						LogUtil.logOnlyDebuggable("LogSendManager", "logsend result==> " + response);
						int id = response.indexOf("logSwitch=");
						if(id>0){
							String logswitch = response.substring(id+10);
							if(logswitch.compareTo("false")==0){
								Constants.LOG_SWITCH = false;
							}else{
								Constants.LOG_SWITCH = true;
							}
							LogBaseHelper.fileClean(mContext, Constants.LOGFILE_PATH+Constants.LOGFILE_NAME);
							ret = true;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
//			}
		}
		return ret;	
	}
}
