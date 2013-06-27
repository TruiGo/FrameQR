package com.alipay.android.safepay;

import org.json.JSONObject;

import com.alipay.android.client.util.BaseHelper;

public class SafePayResultChecker
{
	public static final int RESULT_INVALID_PARAM = 0;
	public static final int RESULT_CHECK_SIGN_FAILED = 1;
	public static final int RESULT_CHECK_SIGN_SUCCEED = 2;
	
	String mContent;
	public SafePayResultChecker(String content)
	{
		this.mContent = content.replaceAll("(\".*);(.*\")", "$1-$2");
	}
	
	public String getReturnStr(String fieldName)
	{
		String result = "";
		
		try
		{
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			result = objContent.getString(fieldName);
			if (result != null && !result.equals("")) {
				result = result.substring(1, result.length()-1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String getResultField(String fieldName)
	{
		String success = null;
		
		try
		{
//			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");

			String result = getReturnStr("result");
			if (result != null && !result.equals("")) {
				JSONObject objResult = BaseHelper.string2JSON(result, "&");
				success = objResult.getString(fieldName);
				success = success.replace("\"", "");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return success;
	}
	
	public boolean isPayOk()
	{
		boolean isPayOk = false;
		
		String success = getResultField("success");
		if(success!=null && success.equalsIgnoreCase("true"))
			isPayOk = true;
		
		return isPayOk;
	}
}