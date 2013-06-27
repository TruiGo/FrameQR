package com.alipay.android.push.callback;

import android.appwidget.AppWidgetManager;
import android.content.Intent;

import com.alipay.android.push.NotificationService;
import com.alipay.android.push.XmppManager;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.connection.XMPPConnection;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.record.RecordtoFile;
import com.alipay.android.push.widget.TestWidgetProvider;

public class ConnectInitListener implements TaskListener{
	private static final String LOGTAG = LogUtil.makeLogTag(ConnectInitListener.class);
	
	XmppManager xmppManager = NotificationService.xmppManager;
	
	@Override
	public void onSuccess(XMPPConnection xmppConnection) {
		// Make note of the fact that we're now connected.
		xmppManager.getConnection().setConnected(true);
		
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_XMPP_CONNED, RecordtoFile.ACTIONT_REGISTER,
				System.currentTimeMillis(), 
				RecordtoFile.ACTIONT_KEEPLIVE, System.currentTimeMillis()+1*10*1000,
				"ConnectInitListener_onSuccess:setConnected",
				1);
		
		LogUtil.LogOut(3, LOGTAG, "===== Connected onSuccess()=====");
		
		//已经处于连接状态，则直接释放电源锁
		this.xmppManager.releaseWakeLock();
		
		//建立连接后注册连接监听器,当socket连接失败时重连
		xmppManager.getConnection().addConnectionListener(xmppManager.getConnectionListener());
		xmppManager.submitRegisterTask();
		
		if (Constants.PUSH_TEST) {
			Intent intent = new Intent();
			intent.setClass(xmppManager.getContext(), TestWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra("PushStatus", 0);

			xmppManager.getContext().sendBroadcast(intent);
		}
	}

	/**
	 * socket通道连接失败的操作 
	 */
	@Override
	public void onFail() {
		RecordtoFile.recordPushInfo(RecordtoFile.REASON_XMPP_CONNED, RecordtoFile.ACTIONT_TIMER,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_CONN, System.currentTimeMillis()+1*10*1000,
				"ConnectInitListener_onFail",
				1);
		
		LogUtil.LogOut(2, LOGTAG, "===== Connected onFail()=====");
		//建立连接失败，则必须释放电源锁
		this.xmppManager.releaseWakeLock();
		
		xmppManager.startReconnectionThread();
	}
}
