package com.alipay.android.push.packet;

import java.io.UnsupportedEncodingException;


public abstract class Packet {
    /**
     * Represents the version of a packet.
     */
	private int mVer = 0;
	protected int mId = 0;
	protected int mHdrLen = 0;
	
	private int mType = 0;
	private int mEncy = 0;
	private int mZip = 0;
	
	private int mLen = 0;
	private String mExtField = "";

	private String mData = "";
	
	public static boolean isSupport(Packet packet) {
		boolean ret = true;
		
		if (packet.getPacketVersion() < PacketConstants.PACKET_MIN_VERSION
				|| packet.getPacketVersion() > PacketConstants.PACKET_MAX_VERSION) {
			ret = false;
		}
		return ret;
	}
	
	public Packet() {
    	//
	}
    
    public Packet(int id, int type, String data) {
		this.mId = id;
		this.mType = type;
		
		if (data != null)
			this.mData = data;
		this.mExtField = "extend";
	}

    
    public int getMsgId() {
		return this.mId;
	}
	public void setMsgId(int id) {
		this.mId = id;
	}
	
	public int getMsgType() {
		return this.mType;
	}
	public void setMsgType(int type) {
		this.mType = type;
	}
	
	public int getEncyFlag() {
		return this.mEncy;
	}
	public void setEncyFlag(int flag) {
		this.mEncy = flag;
	}
	
	public int getZipFlag() {
		return this.mZip;
	}
	public void setZipFlag(int flag) {
		this.mZip = flag;
	}
	
	public int getDataLength(){
		return this.mLen;
	}
	
	public void setDataLength(int len){
		this.mLen = len;
	}
	
	public String getData() {
		return this.mData;
	}
	public void setData(String data) {
		this.mData = data;
		
		try {
			if (data != null && !data.equals("")) {
				byte[] b_utf8 = data.toString().getBytes("utf8");
				this.mLen = b_utf8.length;
			} else {
				this.mData = "";
				this.mLen = 0;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setData(byte[] data) {
		String rawData = "";
		
		try  {
			rawData = new String(data, "utf8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mData = rawData;
	}
	
	public String getExtField() {
		return this.mExtField;
	}


	public int getPacketVersion() {
		// TODO Auto-generated method stub
		return this.mVer;
	}
	
	protected void setPacketVersion(int ver){
		this.mVer = ver;
	}
	
	protected void setPacketHdrLen(int len){
		this.mHdrLen = len;
	}
	
	/**
     * used to get the header of this packet
     */
    public int getPacketHdrLen() {
		return this.mHdrLen;
	}

	
	/**
     * used to get the header of this packet
     */
    public abstract byte[] getHdrbufforWrite();
    
    /**
     * used to get the header from buffer
     */
    public abstract void initHdrfromRead(byte[] readBuf);
    
    /**
     * used to get the base header from buffer
     */
    public abstract void initBaseHdrfromRead(byte[] readBuf);
    
    
    /**
     * 将int类型的数据转换为byte数组
     * 原理：将int数据中的四个byte取出，分别存储
     * @param n int数据
     * @return 生成的byte数组
     */
    protected static byte[] intToBytes(int n){
    	byte[] b = new byte[4];
    	for(int i=0; i<4; i++){
    		b[i] = (byte)(n >> (24 - i * 8)); 
    	}
    	return b;
    }
    
    /**
     * 字节数组到int的转换
     *
     * @param b
     * @return
     */
    protected static int bytesToInt(byte[] b) {  
        int mask=0xff;  
        int temp=0;  
        int n=0;  
        for(int i=0;i<4;i++){  
           n<<=8;  
           temp=b[i]&mask;  
           n|=temp;  
       }  
       return n;  
	} 
    
    /**
     * Returns the packet as byte array. 
     *
     * @return the content of the packet as byte array .
     * @throws UnsupportedEncodingException 
     */
    public byte[] toByteBuf() throws UnsupportedEncodingException {
    	byte[] buffer = new byte[this.getPacketHdrLen() + this.getDataLength()];
    	System.arraycopy(this.getHdrbufforWrite(), 0, buffer, 0, this.getPacketHdrLen());
    	System.arraycopy(this.getData().getBytes("utf8"), 0, buffer, this.getPacketHdrLen(), this.getDataLength());
    	return buffer;
    }
}