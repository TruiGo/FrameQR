package com.alipay.android.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class FileUtils {
	public static void delFiles(File filepath) throws IOException {
		if (filepath.exists() && filepath.isDirectory()) {// 判断是文件还是目录
			if (filepath.listFiles().length == 0) {// 若目录下没有文件则直接删除
				filepath.delete();
			} else {// 若有则把文件放进数组，并判断是否有下级目录
				File delFile[] = filepath.listFiles();
				int i = filepath.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						delFiles(delFile[j]);// 递归调用del方法并取得子目录路径
					}
					delFile[j].delete();// 删除文件
				}
			}
		}
	}
	
	public static void saveBitmap2File(Bitmap bitmap,File file){
		if(file != null && file.exists())
			file.delete();
			
		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(outputStream != null)
					outputStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
		// Raw height and width of image
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (width > reqWidth) {
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}
	
	public static Options getFileOption(File imageFile){
		if(imageFile != null && imageFile.exists()){
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);
			return options;
		}
		return null;
	}
	
	public static float widthRetio(int reqWidth,File bitmap){
		Options option = null;
		option = getFileOption(bitmap);
		float ratio = 1;
		if(option != null){
			int width = option.outWidth;
			if (reqWidth > width) {
				float widthRatio = (float) reqWidth / (float) width;
				ratio = widthRatio;
			}
		}
		return ratio;
	}
	
	public static Bitmap getImageBitmap(int reqWidth,File bitmap){
		Options option = null;
		option = getFileOption(bitmap);
		int sampleSize = calculateInSampleSize(option,reqWidth);
		
		option.inSampleSize = sampleSize;
		option.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(((File) bitmap).getAbsolutePath(), option);
	}
}
