package com.alipay.android.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

public class LogBaseHelper {
//	private static String m_IMSI=null;
//	private static String m_IMEI=null;
//	private static final String TAG="LOG_BASEHELPER";

	
	/*
	 * 写文件
	 */
	public static void writeToFile(Context context, String filePath, String fileName, String content){
		synchronized(Constants.lock){
			try{	
				String path = context.getFilesDir().getAbsolutePath()+ filePath;
				File file = new File(path);
				if(!file.exists()){
					file.mkdir();
				}else{
					String str = readFile(context, filePath+fileName);
					String tempstr = str;
					if (str!=null){
						int count = 0;
						int index =0;
						while(index!=-1){
							index = tempstr.indexOf("$$");
							if(index != -1){
								count++;
								tempstr = tempstr.substring(index+2);
							}
						}
						if(count >= Constants.LOG_MAX_ACCOUNT){
							FileOutputStream fo = new FileOutputStream(path + fileName, false);
							BufferedOutputStream bo = new BufferedOutputStream(fo);
							str = str.substring(str.indexOf("$$")+2) + content;
							bo.write(str.getBytes());
							bo.close();
							fo.close();
							return;
						}
					}
				}
				FileOutputStream fo = new FileOutputStream(path + fileName, true);
				BufferedOutputStream bo = new BufferedOutputStream(fo);
				bo.write(content.getBytes());
				bo.close();
				fo.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/*
	 * 读取文件
	 */
	public static String readFile(Context context,  String filePath){
		synchronized(Constants.lock){
			try{
				String path = context.getFilesDir().getAbsolutePath()+ filePath;
				FileInputStream fi = new FileInputStream(path);
				String str = "";
			      byte[] arrayOfByte = new byte[1024];
			      int m = 0;
			      while ((m = fi.read(arrayOfByte)) != -1)
			        str = str + new String(arrayOfByte, 0, m);
			      fi.close();
			      if (str.length() == 0)
			        return null;
			      return str;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}
	/*
	 * 清除文件里的内容
	 */
	public static void fileClean(Context context, String filePath){
		synchronized(Constants.lock){
			try{
				String path = context.getFilesDir().getAbsolutePath()+ filePath;
				FileOutputStream fo = new FileOutputStream(path);
				fo.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/*
	 * 得到当前时间
	 */
	public static String getNowTime(){
		Date date = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss:ms ");
		return sdf.format(date);
	}
	
}
