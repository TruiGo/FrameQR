package com.alipay.android.push.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * A collection of utility methods for data GZip.
 */
public class GZipUtil {
	private static String encodeType = "utf-8";
	
	/***
	 * 字符串压缩为字符串
	 * 
	 * @param data String
     * @return String
	 */
    public static String compress(String data) {
    	if (data == null || data.length() == 0) {
    		return data;
    	}
    	
    	try {
	    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	GZIPOutputStream gzip = new GZIPOutputStream(out);
	    	gzip.write(data.getBytes(encodeType));
	    	gzip.finish();
	    	gzip.close();
	    	
	    	String ret = out.toString(encodeType);
	    	return ret;
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }  

    /***
	 * 字符串解压缩为字符串
	 * 
	 * @param data String
     * @return String
	 */
    public static String uncompress(String data) {
    	if (data == null || data.length() == 0) {
    		return data;
    	}
    	
    	try {
	    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes(encodeType));
	    	GZIPInputStream gunzip = new GZIPInputStream(in);
	    	
	    	byte[] buffer = new byte[256];
	    	int n;
	    	while ((n = gunzip.read(buffer)) >= 0) {
	    		out.write(buffer, 0, n);
	    	}
	    	
	    	// toString()使用平台默认编码，也可以显式的指定如toString("GBK")  
	    	String ret = out.toString(encodeType);
	    	return ret; 
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }  

	
	/***
	 * 字符串压缩为字节数组
	 * 
	 * @param data String
     * @return byte[]
	 */
	public static byte[] compressToByte(String data) {
		if (data == null || data.length() == 0) {
			return null;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
//			gzip.write(data.getBytes(encodeType));
//			gzip.close();
			byte[] postbyte = data.getBytes(encodeType);
			System.out.print("compressToByte the len of postbyte:"+postbyte.length);
			gzip.write(postbyte, 0, postbyte.length);
			gzip.finish();
			gzip.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out.toByteArray();
	}

	/***
	 * 字节数组解压缩后返回字符串
	 * 
     * @param data byte[]
     * @return String
	 */
	public static String uncompressToString(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try {
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			return out.toString(encodeType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

    
    /***
     * 压缩GZip
     * 
     * @param data byte[]
     * @return byte[]
     */
    public static byte[] gZip(byte[] data) {
    	byte[] b = null;
    	
    	try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		GZIPOutputStream gzip = new GZIPOutputStream(bos);
    		gzip.write(data);
    		gzip.finish();
    		gzip.close();
    		b = bos.toByteArray();
    		bos.close();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	return b;
    }

    /***
     * 解压GZip
     * 
     * @param data byte[]
     * @return byte[]
     */
    public static byte[] unGZip(byte[] data) {
    	byte[] b = null;
    	
    	try {
    		ByteArrayInputStream bis = new ByteArrayInputStream(data);
    		GZIPInputStream gzip = new GZIPInputStream(bis);
    		byte[] buf = new byte[1024];
    		int num = -1;
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		
    		while ((num = gzip.read(buf, 0, buf.length)) != -1) {
    			baos.write(buf, 0, num);
    		}
    		
    		b = baos.toByteArray();
    		baos.flush();
    		baos.close();
    		gzip.close();
    		bis.close();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	return b;
    }


}
