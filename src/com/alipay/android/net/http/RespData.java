package com.alipay.android.net.http;

public class RespData
{
	RespData(String strResponse, String contentType, String charset)
	{
		this.strResponse = strResponse;
		this.contentType = contentType;
		this.charset = charset;
	}
	
	public String strResponse;
	public String contentType;
	public String charset;
}
