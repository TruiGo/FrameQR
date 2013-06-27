package com.alipay.android.push;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;

import com.alipay.android.push.connection.ConnectionListener;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.record.RecordtoFile;
import com.alipay.android.push.widget.TestWidgetProvider;

/** 
 * A listener class for monitoring connection closing and reconnection events.
 */
public class PersistentConnectionListener implements ConnectionListener {

    private static final String LOGTAG = LogUtil.makeLogTag(PersistentConnectionListener.class);

    private final XmppManager xmppManager;

    public PersistentConnectionListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void connectionClosed() {
    	LogUtil.LogOut(3, LOGTAG, "connectionClosed()...");
    	
    	if (Constants.PUSH_TEST) {
			Intent intent = new Intent();
			intent.setClass(xmppManager.getContext(), TestWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra("PushStatus", -1);

			xmppManager.getContext().sendBroadcast(intent);
		}
    }

    /**
     * 出错重连
     */
    @Override
    public void connectionClosedOnError(Exception e) {
    	LogUtil.LogOut(2, LOGTAG, "=== connectionClosedOnError()===");
    	
    	RecordtoFile.recordPushInfo(RecordtoFile.REASON_CONN_EXCEPTION, RecordtoFile.ACTIONT_TIMER,
				System.currentTimeMillis(), 
				RecordtoFile.ACTION_CONN, 
				System.currentTimeMillis()+1*SmackConfiguration.getReconnectInterval()*1000,
				"PersistentConnectionListener_connectionClosedOnError:ReconnectInterval="+SmackConfiguration.getReconnectInterval()*1000,
				1);
    	
        if (xmppManager.getConnection() != null 
        		&& xmppManager.getConnection().isConnected()) {
        	//关闭失败的连接
            xmppManager.getConnection().disconnect();
        }

        xmppManager.startReconnectionThread();
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(LOGTAG, "reconnectingIn()...");
    }

    @Override
    public void reconnectionFailed(Exception e) {
    	LogUtil.LogOut(2, LOGTAG, "=== reconnectionFailed()===");
    }

    @Override
    public void reconnectionSuccessful() {
    	LogUtil.LogOut(3, LOGTAG, "=== reconnectionSuccessful()===");
    }

}
