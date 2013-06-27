package com.alipay.android.push.packetListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.push.PacketIDFilter;
import com.alipay.android.push.XmppManager;
import com.alipay.android.push.connection.PacketFilter;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;

public class ReconnectPacketListener implements PacketListener{
	private final XmppManager xmppManager;

    public ReconnectPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }
    
	@Override
	public void processPacket(Packet packet) {
		//此处仅处理心跳packet，故用filter过滤
		PacketFilter packetFilter = new PacketIDFilter(PacketConstants.MSG_PUSH_RECONNECT);
		
		if (packetFilter.accept(packet)) {
			SmackConfiguration.setLastConnectedTime(System.currentTimeMillis());
			xmppManager.saveLastConnectedTime(System.currentTimeMillis());
			
			String registerData = packet.getData();
			JSONObject registerJson;
			try {
				registerJson = new JSONObject(registerData);
				SmackConfiguration.setReconnectInterval(registerJson.optInt("reconnectTime"));//重连时间
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			//关闭当前的连接
			xmppManager.disconnect();
		}
	}
}
