package com.alipay.android.push;

import java.util.TimerTask;

import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.util.record.RecordtoFile;

public class ReconnectionTask extends TimerTask{
	private static final String LOGTAG = LogUtil.makeLogTag(ReconnectionTask.class);
	
	//通道重连等待时间
	private static final int TIME_RECONN_STEP = 37*60;	//	单位：秒
	private static final int TIME_RECONN_MAXVALUE = 3*60*60;	//	单位：秒
  	private static int waiting = TIME_RECONN_STEP;	//	单位：秒
  	
	@Override
	public void run() {
		synchronized (this) {
			//首先检查通知服务设置的状态
	    	Notifier notifier = new Notifier(NotificationService.xmppManager.getContext());
	    	boolean settingStatus = notifier.isNotificationEnabled() && notifier.isNotificationTime();
	    	
	    	RecordtoFile.recordPushInfo(RecordtoFile.REASON_CLIENT_RECONN, RecordtoFile.ACTIONT_STATUS_CHECK,
    				System.currentTimeMillis(), 
    				RecordtoFile.ACTION_CONN, 1*0*1000,
    				"ReconnectionTask_run:settingStatus="+settingStatus,
    				1);
	    	
	    	LogUtil.LogOut(3, LOGTAG, "run settingStatus="+settingStatus);
	    	
	    	if (settingStatus) {
	    		NotificationService.xmppManager.connect();
	    	}
			
		}
	}
	
	private static boolean isFirstConnect = true;
	public static int waiting() {
		waiting = isFirstConnect ? TIME_RECONN_STEP : waiting*2;
		isFirstConnect = false;
		if (waiting >= TIME_RECONN_MAXVALUE) {	//最大3小时的间隔
			waiting = TIME_RECONN_MAXVALUE;
		}
		LogUtil.LogOut(3, LOGTAG, "waiting seconds="+waiting);
		return waiting;
    }
    
    /**
     * 通道建立成功后清除连接等待时间
     */
    public static void resetWaitingTime(){
    	isFirstConnect = true;
    	waiting = TIME_RECONN_STEP;
    }

}
