package com.alipay.android.push.packet;

import java.io.UnsupportedEncodingException;

import com.alipay.android.push.util.LogUtil;


public class PacketHdrVer3 extends Packet {
	private static final String LOGTAG = LogUtil.makeLogTag(PacketHdrVer3.class);
	
	/**
     * Represents the version of a packet.
     */
	private int mVersionNum = PacketConstants.PACKET_VERSION_3;
	
	/**
     * Represents the length of a packet header.
     */
	private int mHeaderLen = PacketConstants.PACKET_HEADER_LEN_3;	//6+8;	//bytes

	
	public PacketHdrVer3() {
		super();
		
		this.setPacketVersion(PacketConstants.PACKET_VERSION_3);
		this.setPacketHdrLen(PacketConstants.PACKET_HEADER_LEN_3);
	}
	
	@Override
	public byte[] getHdrbufforWrite() {
		int byteLen = mHeaderLen;
		if (this.getMsgId() == PacketConstants.MSG_PUSH_KEEPLIVE) {
			//当前版本，消息为keeplive时，仅传送2个字节的基本头部
			byteLen = 2;
		}
    	byte[] buffer = new byte[byteLen];
    	
    	int i = 0;
    	
    	// the first byte
    	buffer[i] = (byte)((mVersionNum << 4) | (this.getMsgId()));
    	i = i+1;
    	
    	buffer[i] = (byte)((this.getMsgType() << 7) | (this.getEncyFlag() << 6) | (this.getZipFlag() << 5));
    	i = i+1;
    	
    	LogUtil.LogOut(4, LOGTAG,"getHdrbufforWrite() the 1st buffer:" + buffer[0]);
    	LogUtil.LogOut(4, LOGTAG,"getHdrbufforWrite() the 2nd buffer:" + buffer[1]);

    	if (this.getMsgId() != PacketConstants.MSG_PUSH_KEEPLIVE) {
    		//当前版本，当消息为非keeplive时，才传送更多字段
    		//目的：节约12个字节
    		byte[] len = intToBytes(this.getDataLength());
        	System.arraycopy(len, 0, buffer, i, 4);
        	i = i+4;

    		//8个字节的网关路由信息socketId
        	byte[] netId = new byte[8];
        	System.arraycopy(netId, 0, buffer,i,8);
        	
        	LogUtil.LogOut(4, LOGTAG,"getHdrbufforWrite() all len=" + i);
    	}
    	

    	return buffer;
    }

	@Override
	public void initHdrfromRead(byte[] readBuf) {
		// TODO Auto-generated method stub
		if (this.mId != PacketConstants.MSG_PUSH_KEEPLIVE) {
			byte[] hdrLen = new byte[4];
			System.arraycopy(readBuf, 0, hdrLen, 0, 4);
			int msgLen = bytesToInt(hdrLen);
			this.setDataLength(msgLen);
			
			LogUtil.LogOut(4, LOGTAG,"getHdrfromRead() got valid packet! msgLen=" + msgLen);
		}
	}

	public int getPacketHdrLen() {
		// TODO Auto-generated method stub
		if (this.getMsgId() == PacketConstants.MSG_PUSH_KEEPLIVE) {
			this.setPacketHdrLen(2);
		}
		return this.mHdrLen;
	}

	@Override
	public void initBaseHdrfromRead(byte[] readBuf) {
		// TODO Auto-generated method stub
		int i = 0;
		byte msgByte1st = readBuf[i];
		int msgVer = (int) ((msgByte1st >>> 4) & 0x0f);		// 消息version
		int msgId = (int) (msgByte1st & 0x0f);				// 消息ID
		i = i+1;
		
		int msgByte2nd = readBuf[1]; 						// 第二字节
		int msgType = (int) ((msgByte2nd >>> 7) & 0x01);	// 消息类型
		int msgEncFlag = (int)((msgByte2nd >>> 6) & 0x01);	// 加密标示
		int msgZipFlag = (int)((msgByte2nd >>> 5) & 0x01);	// 压缩标示
		i = i+1;
		
		this.setMsgId(msgId);
		this.setMsgType(msgType);
		this.setEncyFlag(msgEncFlag);
		this.setZipFlag(msgZipFlag);
		
		LogUtil.LogOut(4, LOGTAG, "getHdrfromRead() got valid packet! msgId=" + msgId);
		LogUtil.LogOut(5, LOGTAG, "getHdrfromRead() got valid packet! msgType=" + msgType);
	}

}