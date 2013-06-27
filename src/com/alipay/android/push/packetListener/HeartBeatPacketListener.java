package com.alipay.android.push.packetListener;

import java.util.Timer;

import android.content.Intent;

import com.alipay.android.push.PacketIDFilter;
import com.alipay.android.push.XmppManager;
import com.alipay.android.push.connection.PacketFilter;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.record.RecordtoFile;
import com.alipay.android.push.widget.TestWidgetProvider;

public class HeartBeatPacketListener implements PacketListener{
	private static final String LOGTAG = LogUtil.makeLogTag(HeartBeatPacketListener.class);

	private final XmppManager xmppManager;

    public HeartBeatPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }
	
	@Override
	public void processPacket(Packet packet) {
		//此处仅处理心跳packet，故用filter过滤
		PacketFilter packetFilter = new PacketIDFilter(PacketConstants.MSG_PUSH_KEEPLIVE);
		
		if (packetFilter.accept(packet)) {
			//收到心跳响应
			SmackConfiguration.setLastConnectedTime(System.currentTimeMillis());
			xmppManager.saveLastConnectedTime(System.currentTimeMillis());
			
			LogUtil.LogOut(4, LOGTAG, "processPacket() got one resp for HeartBeatPacket!");
			
			if (Constants.PUSH_TEST) {
				Intent intent = new Intent(Intent.ACTION_TIME_CHANGED);
				intent.setClass(xmppManager.getContext(), TestWidgetProvider.class);
				intent.putExtra("TimeTick", 1);

				xmppManager.getContext().sendBroadcast(intent);
			}
			
			RecordtoFile.recordPushInfo(RecordtoFile.REASON_XMPP_RECV, RecordtoFile.ACTIONT_KEEPLIVE,
					System.currentTimeMillis(), 
					RecordtoFile.ACTIONT_KEEPLIVE, 
					System.currentTimeMillis()+SmackConfiguration.getKeepAliveInterval()*1000,
					"HeartBeatPacketListener_processPacket:keepLiveTime="+SmackConfiguration.getKeepAliveInterval(),
					1);
			
//			xmppManager.startHeartTimer();
			xmppManager.startHeartAlarmTimer();
		}
	}
}
