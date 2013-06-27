package com.alipay.android.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;

public class AlipayDataCacheHelper
{
	
	private final String FILE_PATH = Constant.FILE_PATH;
	
	JSONObject myJsonObject = null;
	
	private RootActivity mContext;
	private String mDir = null;
	private String mFileName = null;
	private String mUserPath = null;
	
	public AlipayDataCacheHelper(RootActivity context) 
	{
		mContext = context;
	}
	
	public void setFileParam(String dir, String fileName)
	{
		this.mDir = dir;
		this.mFileName = fileName;
		
		if (mContext.getUserData()!=null) {
			//
			this.mUserPath = FILE_PATH +mContext.getPackageName() +"/files/" +mContext.getUserId() +"/" + mDir;
		}
	}
	
	public static void setRefreshFlag(boolean isRefresh)
	{
		Constant.isTradeListRefresh = isRefresh;
		Constant.isMsgRemindRefresh = isRefresh;
		Constant.isAccountInfoRefresh = isRefresh;
	}
	
	/*
	 * Get this file 
	 */
	private File isFile()
	{
		File file = new File(this.mUserPath, mFileName);
		return file;
	}
	
	/*
	 * write file
	 */
	public void writeContactToFile(JSONObject jObject)
	{
		try
		{			
			File file = new File(mUserPath);
			if (file.exists() && file.isDirectory()) {
				//
			} else {
				//创建目录
				file.mkdirs();
			}
			
			File fileName = new File(mUserPath, mFileName);
			if (file.exists()) {
				fileName.delete();
			}
			fileName.createNewFile();

			FileOutputStream out = new FileOutputStream(fileName);
			out.write(jObject.toString().getBytes());
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * read file
	 */
	public JSONObject readContactToFile()
	{
		JSONObject jObject = null;
		
		try
		{
			File file = isFile();
			if( file.exists() )
			{
				FileInputStream in = new FileInputStream(file);
				if(in.available()>0) {
					jObject = new JSONObject(BaseHelper.convertStreamToString(in));		
				}
				in.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}
	
	/*
	 * delete cache file
	 */
	public static boolean clearCacheData(String dir)
	{

		File dirFile = new File(dir);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		} 
		
//		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i=0; i<files.length; i++) {
			//删除子文件
			if (files[i].isFile()) {
				files[i].delete();
			}
		}
		
		if (dirFile.delete()) {
			return true;
		}

		return false;
	}
}