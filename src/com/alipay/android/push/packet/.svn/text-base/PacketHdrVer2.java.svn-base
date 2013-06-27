package com.alipay.android.push.packet;

import com.alipay.android.push.util.LogUtil;


public class PacketHdrVer2 extends Packet {
	private static final String LOGTAG = LogUtil.makeLogTag(PacketHdrVer2.class);
	
	/**
     * Represents the version of a packet.
     */
	private int mVersionNum = PacketConstants.PACKET_VERSION_2;
	
	/**
     * Represents the length of a packet header.
     */
	private int mHeaderLen = PacketConstants.PACKET_HEADER_LEN_2;	//7+8;	//bytes

	
	public PacketHdrVer2() {
		super();
		
		this.setPacketVersion(PacketConstants.PACKET_VERSION_2);
		this.setPacketHdrLen(PacketConstants.PACKET_HEADER_LEN_2);
	}
	
	@Override
	public byte[] getHdrbufforWrite() {
    	byte[] buffer = new byte[mHeaderLen];
    	
    	int i = 0;
    	
    	Integer ver = Integer.valueOf(mVersionNum);
    	buffer[i] = ver.byteValue();
    	i = i+1;
    	
    	Integer id = Integer.valueOf(this.getMsgId());
    	buffer[i] = id.byteValue();
    	i = i+1;
    	
    	Integer type = Integer.valueOf(this.getMsgType());
    	buffer[i] = type.byteValue();
    	i = i+1;

    	byte[] len = intToBytes(this.getDataLength());
    	System.arraycopy(len, 0, buffer, i, 4);
    	i = i+4;
    	
    	
		//4个字节的扩展域
    	byte[] extend1 = new byte[4];
    	System.arraycopy(extend1, 0, buffer,i,4);
    	i = i+4;

    	//4个字节的扩展域
    	byte[] extend2 = new byte[4];
    	System.arraycopy(extend2, 0, buffer,i,4);
    	
    	return buffer;
    }

	@Override
	public void initHdrfromRead(byte[] readBuf) {
		// TODO Auto-generated method stub
		int i = 0;
		
		int msgType = (int) readBuf[i];;				// 消息类型
		i = i+1;

		LogUtil.LogOut(5, LOGTAG,"getHdrfromRead() got valid packet! msgType=" + msgType);
		
		byte[] hdrLen = new byte[4];
		System.arraycopy(readBuf, i, hdrLen, 0, 4);
		int msgLen = bytesToInt(hdrLen);
		LogUtil.LogOut(4, LOGTAG,"getHdrfromRead() got valid packet! msgLen=" + msgLen);

		this.setMsgType(msgType);
		this.setDataLength(msgLen);
	}

	@Override
	public void initBaseHdrfromRead(byte[] readBuf) {
		// TODO Auto-generated method stub
		int i = 0;
		
		int msgVer = (int) readBuf[i];					// 消息version
		i = i+1;
		
		int msgId = (int) readBuf[i];;					// 消息ID
		i = i+1;
		
		LogUtil.LogOut(4, LOGTAG,"getHdrfromRead() got valid packet! msgId=" + msgId);
		this.setMsgId(msgId);
	}
}