package com.etao.kaka.decode;

public class DecodeResult {
	public int type;
	public int subType;
	public String strCode;
	public byte[] bytes;
	public DecodeResult(int type,int subType,String strCode){
		this.type = type;
		this.subType = subType;
		this.strCode = strCode;
	}
	public DecodeResult(int type,int subType,byte[] bytes){
		this.type = type;
		this.subType = subType;
		this.bytes = bytes;
	}
}
