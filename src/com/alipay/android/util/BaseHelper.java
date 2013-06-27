/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.alipay.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BaseHelper
{
	public static String convertStreamToString(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	

	public static void chmod(String permission, String path)
	{
		try
			{
				String command 	= "chmod " + permission + " " + path;
				Runtime runtime = Runtime.getRuntime();
				
				@SuppressWarnings("unused")
				Process proc 	= runtime.exec(command);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
	
	public static String getCharset(String contentType)
	{
		String charset = null;
		
		try
		{
			String charsetTag = "charset=";
			int start = contentType.indexOf(charsetTag);
			if( start != -1 )
			{
				start += charsetTag.length();
				
				int end = contentType.indexOf(";", start);
				if( end != -1 )
					charset = contentType.substring(start, end);
				else 
					charset = contentType.substring(start);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return charset;
	}
	
	public static String getContentType(String acontentType)
	{
		String contentType = null;
		
		try
		{
			String contentTypeTag = ";";
			int end = acontentType.indexOf(contentTypeTag);
			if( end > 0 )
				contentType = acontentType.substring(0, end);
			else contentType = acontentType;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return contentType;
	}

	public static String getTimeStamp()
	{		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = new Date();
		
		String timeStamp = format.format(date);
		return timeStamp;
	}	
	
}