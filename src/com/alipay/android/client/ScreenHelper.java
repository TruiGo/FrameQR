package com.alipay.android.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.view.View;

import com.alipay.android.appHall.Helper;

public class ScreenHelper {
	public static String getScreenFileDir(Context context){
		//data/data/application/screenImage/xxx.png
		String imageDir = context.getFilesDir().getAbsolutePath() + File.separator + "screenImage";
		File file = new File(imageDir);
		if(!file.exists())
			file.mkdirs();
		return imageDir;
	}
	
	public static Bitmap getViewFormLocal(String bitmapName,Context context){
		Options options = new Options();
    	options.inDensity =(int) Helper.getDensityDpi(context);
    	options.inScaled = false;
    	
		String bitmapPath = getScreenFileDir(context) + File.separator + bitmapName + ".png";
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(bitmapPath);
			Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream, null, options);
			return imageBitmap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public static Bitmap getViewBitmap(View view){
		view.setDrawingCacheEnabled(true);
//		view.buildDrawingCache();
        Bitmap viewBitmap = view.getDrawingCache();
        if(viewBitmap == null)
        	return null;
        
        Bitmap clonedBitmap =  viewBitmap.copy(Bitmap.Config.ARGB_4444, true);
        
        view.setDrawingCacheEnabled(false);
        if(viewBitmap != null){
        	viewBitmap.recycle();
        	viewBitmap = null;
        }
        return clonedBitmap;
	}
	
	public static String getBitmapSavedPath(View appView,Context context){
        appView.setDrawingCacheEnabled(true);
        appView.buildDrawingCache();
        Bitmap viewBitmap = appView.getDrawingCache();
        if(viewBitmap == null)
        	return null;
        //保存图片到本地
    	String fileName = null;
    	FileOutputStream out = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
			fileName = dateFormat.format(new Date());
//			String absoluteFname = getScreenFileDir(context) + fileName + ".png";// 把获得的图片保存
			File file = new File(getScreenFileDir(context) + File.separator + fileName + ".png");
			if(!file.exists())
				file.createNewFile();
			
			out = new FileOutputStream(file);
//			Bitmap cloneBit =  viewBitmap.copy(Bitmap.Config.ARGB_8888, true);
			viewBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			appView.setDrawingCacheEnabled(false);
			viewBitmap.recycle();
//			cloneBit.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return fileName;
	}
	
	public static int[] getViewLocation(View view){
		int[] viewLocation = new int[2];
		view.getLocationOnScreen(viewLocation);
		
		return viewLocation;
	} 
	
	public static Rect getStatusBarRect(Activity activity){
		Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame;
	}
}
