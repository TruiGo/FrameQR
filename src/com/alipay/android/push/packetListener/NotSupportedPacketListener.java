package com.alipay.android.push.packetListener;

import com.alipay.android.push.packet.Packet;

public class NotSupportedPacketListener implements PacketListener{

	@Override
	public void processPacket(Packet packet) {
		//不支持的包类型,供分析包数据使用
		
	}
}
