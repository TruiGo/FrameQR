package com.alipay.android.client;


public class AlipayPushMsgData {
	// message id
	public static final int MSG_PUSH_INITIALIZE = 0;
	public static final int MSG_PUSH_CLOSE = 1;
	public static final int MSG_PUSH_RECONNECT = 2;
	public static final int MSG_PUSH_KEEPLIVE = 3;
	public static final int MSG_PUSH_DATA = 4;
	public static final int MSG_PUSH_DATA_RESULT = 5;
	
	public static final int MSG_PUSH_TYPE_REQUEST = 0;
	public static final int MSG_PUSH_TYPE_RESPONSE = 1;

	public static int mVersionNum = 1;
	public static int mHeaderLen = 15;	//7+8;	//bytes
	
	private int mLen = 0;
	private int mId = 0;
	private int mType = 0;
	private String mData = null;
	private String mExtField = null;
	
	public AlipayPushMsgData(int id, int type, String data) {
		this.mId = id;
		this.mType = type;
		
		if (data != null)
			this.mData = data;
		this.mExtField = "extend";
	}
	
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
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
	
	public int getMsgVersion() {
		return this.mVersionNum;
	}
	
	public String getData() {
		return this.mData;
	}
	public void setData(String data) {
		this.mData = data;
	}
	
	public String getExtField() {
		return this.mExtField;
	}
	
	public static int bytes2short(byte[] b)
	{
	         //byte[] b=new byte[]{1,2,3,4}; 
	         int mask=0xff;
	         int temp=0;
	        int res=0;
	        for(int i=0;i<2;i++){
	            res<<=8;
	            temp=b[i]&mask;
	            res|=temp;
	        }
	       return res;
	}
	
	public static byte[] short2bytes(int num)
	{
	       byte[] b = new byte[2];
	       int mask=0xff;
	       
	       for(int i=0; i<2; i++){
	            b[i]=(byte)(num>>(8-i*8));
	       }
	      return b;
	}
	
	public byte[] intToByte(short i) { 
		byte[] bt = new byte[2]; 
		
		bt[0] = (byte) ((0xff00 & i) >> 8); 
		bt[1] = (byte) (0xff & i); 
		
		return bt; 
	}


	private static byte toByte(char c) {
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);
	    return b;
	}
	
}