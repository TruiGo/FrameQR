package com.alipay.android.ui.beanutil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.serviceImpl.AliPassServiceImpl;
import com.alipay.android.servicebeans.SyncVoucher;
import com.alipay.android.util.FileUtils;
import com.alipay.android.util.JsonConvert;

public class VoucherSyncHelper {
	private static VoucherSyncHelper mVoucherSyncHelper;
	
	public synchronized static VoucherSyncHelper getInstance(Context context){
		if(mVoucherSyncHelper == null){
			mVoucherSyncHelper = new VoucherSyncHelper(context);
		}
		
		return mVoucherSyncHelper;
	}
	
	public static final int POOL_SIZE = 3;
	public static final int KEEP_ALIVE_TIME = 5;
	public static final int QUEUE_SIZE = 20;
	
	private ThreadPoolExecutor mPool;
	private AliPassServiceImpl mAliPassServiceImpl;
	private String mUserAccount;
	private Context mContext;
	private SyncVoucher mSyncVoucher;
	private int syncingFileCount = 0;
	
	private VoucherSyncHelper(Context context){
		//控制pool大小,防止同时上传的文件过多，导致byte[]分配过大 
		mPool = new ThreadPoolExecutor(2, POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_SIZE));
		mAliPassServiceImpl = new AliPassServiceImpl(context);
		mContext = context;
		mSyncVoucher = new SyncVoucher(mContext); 
	}
	
	public void syncFiles(String userAccount) {
		mUserAccount = userAccount; 
		File[] files = mAliPassServiceImpl.getAlipassFileList(mUserAccount);
		if(files != null && files.length > 0){
			List<File> fileList = Arrays.asList(files);
			Collections.sort(fileList, new Comparator<File>() {//按照加入到客户端的时间排序
				@Override
				public int compare(File lhs, File rhs) {
					if(lhs.lastModified() < rhs.lastModified())
						return -1;
					else if(lhs.lastModified() > rhs.lastModified())
						return 1;
					return 0;
				}
			});
			syncingFileCount = fileList.size();
			for(File curFile : fileList){
				mPool.execute(new SyncVoucherRunnable(curFile));
			}
		}
	}
	
	public boolean isSyncing(){
		return syncingFileCount == 0;
	}
	
	final class SyncVoucherRunnable implements Runnable{
		private File mCurrentSyncFile;
		public SyncVoucherRunnable(File curFile) {
			mCurrentSyncFile = curFile;
		}

		@Override
		public void run() {
			if(mCurrentSyncFile.exists()){
				BufferedInputStream bufferedInputStream = null;
				ByteArrayOutputStream fileArrayStream = null;
				try {
					bufferedInputStream = new BufferedInputStream(new FileInputStream(mCurrentSyncFile));
					fileArrayStream = new ByteArrayOutputStream((int) mCurrentSyncFile.length());
					byte[] buffer = new byte[8 * 1024];
					int length;
					while ((length = bufferedInputStream.read(buffer)) != -1)
						fileArrayStream.write(buffer, 0, length);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(bufferedInputStream != null){
						try {
							bufferedInputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(fileArrayStream != null){
						try {
							fileArrayStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
				if(fileArrayStream != null && fileArrayStream.size() > 0){
					try {
						byte[] alipassByte = fileArrayStream.toByteArray();
						mSyncVoucher.initParams(new String(Base64.encode(alipassByte, Base64.DEFAULT)));
						fileArrayStream.flush();
						String response = mSyncVoucher.doX();
						syncingFileCount --;
						if(syncingFileCount < 0)
							syncingFileCount = 0;
						if(response != null)
							processResult(response,mCurrentSyncFile);
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						try {
							if(fileArrayStream != null)
								fileArrayStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 上传成功后的处理
	 * @param result
	 * @param file
	 */
	private void processResult(String result,File file){
		JSONObject responseJson = JsonConvert.convertString2Json((String) result);
		int resultStatus = responseJson.optInt(Defines.resultStatus);
		if(resultStatus == 100 || resultStatus == 3022 || resultStatus == 3021)
			file.delete();
		
		if (resultStatus == 100) {
			String logoImg = responseJson.optString("logo");
			String fileName = file.getName();
			File passFile = mAliPassServiceImpl.getPassFile(mUserAccount, fileName.substring(0,fileName.indexOf(".")));
			File logoImgFile = new File(passFile.getAbsolutePath() + File.separator + AliPassServiceImpl.PASSLOGO);
			
			String passImg = responseJson.optString("strip");
			if(passImg != null){
				File passImgFile = new File(passFile.getAbsolutePath() + File.separator + AliPassServiceImpl.STRIPFILE);
				passImgFile.renameTo(new File(Helper.getCachePath(mContext, AliPassServiceImpl.ALIPASSDIR) + Helper.urlToKey(passImg)));
			}
			
			boolean renameResult = logoImgFile.renameTo(new File(Helper.getCachePath(mContext, AliPassServiceImpl.ALIPASSDIR) + Helper.urlToKey(logoImg)));
			if(renameResult){
				try {
					FileUtils.delFiles(passFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}