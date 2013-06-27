package com.alipay.android.push.packetListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.content.Intent;

import com.alipay.android.push.PacketIDFilter;
import com.alipay.android.push.XmppManager;
import com.alipay.android.push.connection.PacketFilter;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.util.ConnectParamConstant;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.record.RecordtoFile;
import com.alipay.android.push.widget.TestWidgetProvider;

/**
 * 初始化完成
 */
public class RegisterPacketListener implements PacketListener{
	private static final String LOGTAG = LogUtil.makeLogTag(RegisterPacketListener.class);
	
	private final XmppManager xmppManager;

    public RegisterPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

	@Override
	public void processPacket(Packet packet) {
		//此处仅处理心跳packet，故用filter过滤
		PacketFilter packetFilter = new PacketIDFilter(PacketConstants.MSG_PUSH_INITIALIZE);
		
		if (packetFilter.accept(packet)) {
			SmackConfiguration.setLastConnectedTime(System.currentTimeMillis());
			xmppManager.saveLastConnectedTime(System.currentTimeMillis());
			
			String registerData = packet.getData();
			JSONObject registerJson;
			try {
				registerJson = new JSONObject(registerData);
				SmackConfiguration.setKeepAliveInterval(registerJson.optInt(ConnectParamConstant.KEEPLIVE_TIME));	//心跳间隔时间
				SmackConfiguration.setReconnectInterval(registerJson.optInt(ConnectParamConstant.RECONNECT_TIME));	//重连时间
				SmackConfiguration.setLBSFlag(registerJson.optBoolean(ConnectParamConstant.UPDATE_LBSINFO));		//地理位置信息是否上传
				
				//reset waiting time after this inited command successfully
				//or else reconnect machanism will be effected
				xmppManager.resetWaitingTime();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (Constants.PUSH_TEST) {
				Intent intent = new Intent();
				intent.setClass(xmppManager.getContext(), TestWidgetProvider.class);
				intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
				intent.putExtra("PushStatus", 1);

				xmppManager.getContext().sendBroadcast(intent);
			}
			
			LogUtil.LogOut(3, LOGTAG, "processPacket() reconnectTime="+SmackConfiguration.getReconnectInterval()
					+"s, keepLiveTime="+SmackConfiguration.getKeepAliveInterval()
					+"s, updateLBSInfo="+SmackConfiguration.getLBSFlag());
			
			RecordtoFile.recordPushInfo(RecordtoFile.REASON_XMPP_REGISTERD, RecordtoFile.ACTIONT_KEEPLIVE,
					System.currentTimeMillis(), 
					RecordtoFile.ACTIONT_KEEPLIVE, System.currentTimeMillis()+1*1*1000,
					"RegisterPacketListener_processPacket:reconnectTime="+SmackConfiguration.getReconnectInterval()
					+" keepLiveTime="+SmackConfiguration.getKeepAliveInterval(),
					1);
			
			// Start keep alive process (after TLS was negotiated - if available)	
			//xmppManager.getConnection().initStartKeepAlive();
//			xmppManager.startHeartTimer();
			xmppManager.startHeartAlarmTimer();
//			xmppManager.submitHeartBeatTask();
		}
	}
}
