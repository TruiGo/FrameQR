package com.alipay.android.serviceImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.ZipHelper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.ui.bean.AlipassInfo;
import com.alipay.android.ui.bean.AlipassInfo.Barcode;
import com.alipay.android.ui.bean.AlipassInfo.Einfo;
import com.alipay.android.ui.bean.AlipassInfo.EvoucherInfo;
import com.alipay.android.ui.bean.AlipassInfo.Merchant;
import com.alipay.android.ui.bean.VerifyCode;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.bean.VoucherDetail;
import com.alipay.android.ui.beanutil.AlipassConverter;
import com.alipay.android.ui.beanutil.VoucherComparator;
import com.alipay.android.ui.voucher.VoucherDetailViewController;
import com.alipay.android.util.FileUtils;
import com.alipay.android.util.JsonConvert;

/**
 * 服务于Alipass业务逻辑
 */
public class AliPassServiceImpl {
	private Context mContext;
	public static final String ALIPASSDIR = "alipassDir";
	public static final String PASSJSON = "pass.json";
	public static final String PASSLOGO = "logo.png";
	public static final String STRIPFILE = "strip.png";
	private String mAlipassDir;

	public AliPassServiceImpl(Context context) {
		mContext = context;
		mAlipassDir = Helper.getCachePath(mContext, ALIPASSDIR);
	}

	/**
	 * 从第三方读取alipass文件
	 * @param pathUri
	 * @param passName
	 */
	public boolean readAndUnZipPassFile(Uri pathUri, String passName) {
		if(readPassFile(pathUri, passName)){
			return unZipPassFile2Temp(passName);
		}
		return false;
	}
	
	/**
	 * Alipass文件是否已经与对应账号绑定
	 * @param accountName 账号
	 * @param passName alipass文件名
	 * @return 是否已经存在
	 */
	public boolean passFileExist(String accountName,String passName){
		if(accountName != null){
			File cacheFile = getAlipassCacheFile(accountName);
			File previewAlipassFile = new File(cacheFile.getAbsolutePath() + File.separator + passName.substring(0,passName.indexOf(".")));
			return previewAlipassFile.exists();
		}
		return false;
	}

	/**
	 * 使用ContentResolver读取文件
	 * @param pathUri
	 * @param passName
	 * @return
	 */
	private boolean readPassFile(Uri pathUri, String passName) {
		InputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try {
			inputStream = mContext.getContentResolver().openInputStream(pathUri);
			if(inputStream != null){
				outputStream = new BufferedOutputStream(new FileOutputStream(getTempPassFile(passName)));
				byte[] buffer = new byte[1024 * 10];
				int length = 0;
				while ((length = inputStream.read(buffer)) != -1)
					outputStream.write(buffer, 0, length);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 解压到临时文件夹 cache/alipassDir/123/
	 * @param passName
	 * @return
	 */
	private boolean unZipPassFile2Temp(String passName){
		if(passName.endsWith(".alipass")){
			String tempFile = getUnzipedTempDir(passName);
			return ZipHelper.unZip(getTempPassFile(passName).getAbsolutePath(),tempFile);
		}
		return false;
	}

	//cache下为解压包目录
	private String getUnzipedTempDir(String passName) {
		return Helper.getCachePath(mContext, ALIPASSDIR) + passName.substring(0, passName.indexOf(".")) + File.separator;
	}
	
	//获取cache目录下缓存文件File对象
	public File getTempPassFile(String passName) {
		return new File(mAlipassDir + passName);
	}
	
	//获取账户对应缓存文件File对象
	public File getPassFile(String userAccount,String passFileName){
		if(userAccount!= null){
			File file = getAlipassCacheFile(userAccount);
			if(!file.exists()){
				file.mkdirs();
			}
			return new File(file.getAbsoluteFile()+ File.separator + passFileName);
		}
		return null;
	}

	//与账户对应的缓存目录
	public File getAlipassCacheFile(String userAccount) {
		return new File(mContext.getFilesDir().getAbsolutePath() + File.separator+ ALIPASSDIR + File.separator + userAccount);
	}
	
	/**
	 * alipass文件与特定账号绑定（账号区分的文件夹下）
	 * @param userAccount
	 * @param passName
	 * @return
	 */
	public boolean bindPassFile(String userAccount,String passName){
		File passFile = getTempPassFile(passName);
		File desFile = getPassFile(userAccount,passName);
		boolean result = passFile.renameTo(desFile);
		
		if(result){
			desFile.setLastModified(System.currentTimeMillis());
			File unzipedFiles = getTempPassFile(passName.substring(0, passName.indexOf(".")));
			File newAccountFile = getPassFile(userAccount,passName.substring(0, passName.indexOf(".")));
			unzipedFiles.renameTo(newAccountFile);
			try {
				if(unzipedFiles != null){
					FileUtils.delFiles(unzipedFiles);
					unzipedFiles.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			passFile.delete();
		}
		return result;
	}
	
	/**
	 * 解析改用户对应的alipass文件
	 * @param userAccount
	 * @param passName
	 */
	public AlipassInfo resolvePassFromTempDir(String passName){
		String unzipedDir = getUnzipedTempDir(passName);
		
		return resolvePass(unzipedDir);
	}
	
	public AlipassInfo resolvePass(String filePath){
		File passJsonFile = new File(filePath + PASSJSON);
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(passJsonFile));
			JSONObject passJson = Helper.getConfig(is);
			return resoveAlipassFile(passJson);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	/**
	 * 从本地获取alipass列表
	 * @param accountName
	 * @param expired 状态 可用/过期
	 * @return
	 */
	public List<AlipassInfo> getAlipassFileListFromLocal(String accountName){
		File alipassCacheFile = getAlipassCacheFile(accountName);
		File[] fileList = getAlipassFileList(accountName);
		List<AlipassInfo> alipassInfoList = new ArrayList<AlipassInfo>();
		if(fileList != null){
			for(File curFile : fileList){
				String curFileFullName = curFile.getName();
				String fileName = curFileFullName.substring(0, curFileFullName.indexOf("."));
				AlipassInfo alipassInfo = resolvePass(alipassCacheFile + File.separator + fileName + File.separator);
				
				if(alipassInfo != null){
					alipassInfo.setLastModifiedTime(curFile.lastModified());
					alipassInfo.setPassFileName(fileName);
					alipassInfoList.add(alipassInfo);
				}
			}
		}
		return alipassInfoList;
	}
	
	public AlipassInfo getAlipassFileByName(String accountName,String fileName){
		File passFile = getPassFile(accountName, fileName);
		if(passFile != null && passFile.exists()){
			String unzipedDir = passFile.getAbsolutePath() + File.separator ;
			return resolvePass(unzipedDir);
		}
		return null;
	}
	
	/**
	 * 混合本地券与从服务器获取的券列表
	 * @param accountName
	 * @param voucherArray
	 * @param expired 券列表状态 可用/不可用
	 * @return
	 */
	public List<Voucher> mixLocalAndOnlineVoucher(String accountName,JSONArray voucherArray) {
		//获取alipass列表数据
        List<AlipassInfo> alipassList = getAlipassFileListFromLocal(accountName);
        List<Voucher> vouchers = AlipassConverter.aliapssInfo2VoucherInfo(alipassList);
        
        if(voucherArray != null && voucherArray.length() > 0){
	        List<Voucher> onLineVouchers = JsonConvert.jArry2BeanList(voucherArray, new Voucher());
	        if(onLineVouchers.size() == 1 && vouchers.size() > 0){
	        	Voucher onlyVoucher = onLineVouchers.get(0);
	        	if(!VoucherDetailViewController.VIRTUALID.equals(onlyVoucher.getVoucherId())){
	        		vouchers.addAll(onLineVouchers);
	        	}
	        }else
	        	vouchers.addAll(onLineVouchers);
        }
        
		Collections.sort(vouchers,new VoucherComparator());
		return vouchers;
	}
	
	
	private AlipassInfo resoveAlipassFile(JSONObject passJson) {
		return AlipassConverter.json2Alipass(passJson, new AlipassInfo());
	}

	public File[] getAlipassFileList(String accountName) {
		File alipassCacheFile = getAlipassCacheFile(accountName);
		File[] fileList = alipassCacheFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".alipass") && dir.length() < 500 * 1024;
			}
		});
		return fileList;
	}

	public AlipassInfo convertVoucherDetail2Alipass(VoucherDetail voucherDetail,JSONObject responseJson) {
		AlipassInfo alipassInfo = new AlipassInfo();
		EvoucherInfo evoucherInfo = new EvoucherInfo();
		Merchant merchant = new Merchant();
		merchant.setMname(voucherDetail.getMerchantName());
		alipassInfo.setMerchant(merchant);
		
		String styleStr = responseJson.optString("displayInfo");
		if(styleStr != null && !"".equals(styleStr)){
			JSONObject styleJson = null;
			try {
				styleJson = new JSONObject(styleStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			AlipassInfo.Style style = new AlipassInfo.Style();
			if(styleJson != null){
				style.setBackgroundColor(styleJson.optString("backgroundColor"));
			}
			alipassInfo.setStyle(style);
		}
		
		evoucherInfo.setTitle(voucherDetail.getVoucherName());
		
		String startDate = voucherDetail.getExpiredBeginDate();
		String endDate = voucherDetail.getExpiredEndDate();
		if(startDate != null)
			evoucherInfo.setStartDate(startDate);
		if(endDate != null)
			evoucherInfo.setEndDate(endDate);
		
		String description = responseJson.optString("voucherDetail");
		evoucherInfo.setDescription(description);
		
		Einfo einfo = new Einfo();
		setEinfo(einfo,responseJson);
		evoucherInfo.seteInfo(einfo);
		
		alipassInfo.setStatus(voucherDetail.getStatus());
		alipassInfo.setEvoucherInfo(evoucherInfo);
		evoucherInfo.setDisclaimer(responseJson.optString("disclaimer"));
		return alipassInfo;
	}

	/**
	 * 根据返回数据设置Einfo中的barcodeList
	 * @param einfo
	 * @param responseJson
	 */
	private void setEinfo(Einfo einfo, JSONObject responseJson) {
		JSONArray verifyArray = responseJson.optJSONArray("verifyCodeList");
		if(verifyArray!=null && verifyArray.length() > 0){
			List<VerifyCode> verifyList = JsonConvert.jArry2BeanList(verifyArray, new VerifyCode());
			List<Barcode> barcodeList= new ArrayList<AlipassInfo.Barcode>();
			for (VerifyCode verifyCode : verifyList) {
				if(Voucher.Status.CAN_USE.equals(verifyCode.getVerifyStatus())){
					Barcode barcode = new Barcode();
					barcode.setAltText(verifyCode.getVerifyCode());
					barcode.setMessage(verifyCode.getMessage());
					barcode.setFormat(verifyCode.getFormat());
					barcodeList.add(barcode);
				}
			}
			einfo.setBarcodeList(barcodeList);
		}
	}

	//获取pass背景图片
	public File getPassBgImage(String voucherMode, final String stripUrl, Context context, String accountName, String prefixPassName,
			String passName, BitmapDownloadListener downLoadListener) {
		File stripFile = null;
		if (Voucher.Mode.OFFLINE.equals(voucherMode)) { // 本地离线券
			File tempCacheFile = getAlipassCacheFile(accountName);
			stripFile = new File(tempCacheFile.getAbsolutePath() + File.separator + passName + File.separator + AliPassServiceImpl.STRIPFILE);
		} else if (Voucher.Mode.PREVIEW.equals(voucherMode)) {// 预览
			File tempCacheFile = getTempPassFile(prefixPassName);
			stripFile = new File(tempCacheFile.getAbsolutePath() + File.separator + AliPassServiceImpl.STRIPFILE);
		} else {// 在线
			if (stripUrl != null) {
				File passBgFile = new File(Helper.getCachePath(mContext, AliPassServiceImpl.ALIPASSDIR) + Helper.urlToKey(stripUrl));
				if (passBgFile.exists()) {// 存在
					stripFile = new File(Helper.getCachePath(mContext, AliPassServiceImpl.ALIPASSDIR) + Helper.urlToKey(stripUrl));
				} else {// 不存在，下载图片
					Helper.bitmapFromUriString(context, stripUrl, downLoadListener, -1);
				}
			}
		}
		
		return stripFile;
	}
}
