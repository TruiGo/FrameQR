package com.alipay.android.push.packet;


public class PacketFactory {

	public static Packet getPacket(int protoVer) throws Exception {

        Packet packet;
        
		//判断逻辑，返回具体的协议包 
        if(PacketConstants.PACKET_VERSION_2 == protoVer) {
        	packet = new PacketHdrVer2();
        } else 
        if(PacketConstants.PACKET_VERSION_3 == protoVer) {
        	packet = new PacketHdrVer3();
        }
        else {
        	packet = null;
        	throw new Exception("Don't support this protovern:"+protoVer); 
        }
        
        return packet;
	}

}