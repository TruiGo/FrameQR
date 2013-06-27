package com.alipay.android.ui.bean;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.appHall.common.BitmapDownloader;
import com.alipay.android.serviceImpl.AliPassServiceImpl;
import com.alipay.android.util.FileUtils;
import com.eg.android.AlipayGphone.R;

public class Voucher extends Observable implements Parcelable{
	/**
	 * 券状态
	 */
	public static class Status{
		public static final String ALL = "all";
		public static final String UNPAID = "unpaid";
		public static final String CAN_USE = "can_use";
		public static final String SOON_EXPIRED = "soon_expired";
		public static final String USED = "used";
		public static final String EXPIRED = "expired";
		public static final String REFUNDED = "refunded";
		public static final String REFUNDING = "refunding";
		public static final String CLOSED = "closed";
		public static final String INVALID = "invalid";
		
		
		public static Map<String, String> STATUSMAP = new HashMap<String, String>();
		static {
			STATUSMAP.put(ALL, "全部");
			STATUSMAP.put(CAN_USE, "可使用");
			STATUSMAP.put(CLOSED, "已关闭");
			STATUSMAP.put(EXPIRED, "已过期");
			STATUSMAP.put(INVALID, "已失效");
			STATUSMAP.put(REFUNDED, "已退款");
			STATUSMAP.put(REFUNDING, "退款中");
			STATUSMAP.put(SOON_EXPIRED, "即将到期");
			STATUSMAP.put(UNPAID, "待付款");
			STATUSMAP.put(USED, "已使用");
		}
	}
	
	public static class Mode{
		/*ONLINE("online"),//在线
		PREVIEW("preView"),//临时，预览
		OFFLINE("offline");//离线，未完成同步
		String mode;
		Mode(String mode){
			this.mode = mode;
		}*/
		public static final String ONLINE = "online";//在线
		public static final String PREVIEW = "preView";//临时，预览
		public static final String OFFLINE = "offline";//离线，未完成同步
	}
	
	public static class TemplateType{
		public static final String COUPON1 = "coupon-1";
	}
	/**
	 * 券类型
	 */
	public static class VOUCHERTYPE{
		public static final String ONCETIME = "0";
		public static final String MORETIME = "1";
	}
	
	private String voucherId;
	private String voucherName;
	private String voucherCode;
	private String logoUrl;
	private String status;
	private String voucherType;
	private String actualAmount;// 实际金额
	private String amount;// 购买金额
	private Integer totalTimes;
	private Integer canUseTimes;
	private String merchantId;// 商户的唯一标识
	private String merchantName;
	private String voucherFrom;
	private String expiredBeginDate;
	private String expiredEndDate;
	private String outBizNo;
	private Integer leftDays;
	private Long gmtCreate;
	private String mode;
	private String passFileName;
	private int backGroundColor;
	private String templateType;
	private String displayInfo;
	
	public String getDisplayInfo() {
		return displayInfo;
	}

	public void setDisplayInfo(String displayInfo) {
		this.displayInfo = displayInfo;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public int getBackGroundColor() {
		if(!(Mode.OFFLINE.equals(mode) || Mode.PREVIEW.equals(mode)) && displayInfo != null){
			try {
				JSONObject displayInfoObject = new JSONObject(displayInfo);
				String backGroundColor = displayInfoObject.optString("backgroundColor");
				return AlipassInfo.resolveColor(backGroundColor);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return backGroundColor;
	}

	public void setBackGroundColor(int backGroundColor) {
		this.backGroundColor = backGroundColor;
	}

	public String getPassFileName() {
		return passFileName;
	}

	public void setPassFileName(String passFileName) {
		this.passFileName = passFileName;
	}

	public String getMode() {
		if(Voucher.TemplateType.COUPON1.equals(templateType)){
			mode = Mode.ONLINE;
		}
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Long getGmtCreate() {
		if(gmtCreate == null){
			return 0l;
		}
		return gmtCreate;
	}

	public void setGmtCreate(Long gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	//	private Context mContext;
	public static final String VOUCHERICONDIR= "voucher_icon";
	
	private String mAppsPath;
	
	private BitmapDownloadListener mBitmapDownloadListener = new BitmapDownloader() {
        @Override
        public void onComplete(Bitmap bm) {
        	if(bm != null){
        		FileUtils.saveBitmap2File(bm, getIconFile(mAppsPath,logoUrl));
        		setChanged();
        		notifyObservers();
        	}
        }

		@Override
		public void onDownLoading(boolean isDownLoading) {
			setChanged();
        	notifyObservers(isDownLoading);
		}
    };
    
    public boolean isAvailabelVoucher(){
    	return Voucher.Status.SOON_EXPIRED.equals(status)
				||Voucher.Status.CAN_USE.equals(status);
    }
    
    public Voucher() {}
    
	public String getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}

	public String getVoucherName() {
		return voucherName;
	}

	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}

	public String getVoucherCode() {
		return voucherCode;
	}

	public void setVoucherCode(String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(String actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Integer getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(Integer totalTimes) {
		this.totalTimes = totalTimes;
	}

	public Integer getCanUseTimes() {
		return canUseTimes;
	}

	public void setCanUseTimes(Integer canUseTimes) {
		this.canUseTimes = canUseTimes;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getVoucherFrom() {
		return voucherFrom;
	}

	public void setVoucherFrom(String voucherFrom) {
		this.voucherFrom = voucherFrom;
	}

	public String getExpiredBeginDate() {
		return expiredBeginDate;
	}

	public void setExpiredBeginDate(String expiredBeginDate) {
		this.expiredBeginDate = expiredBeginDate;
	}

	public String getExpiredEndDate() {
		return expiredEndDate;
	}

	public void setExpiredEndDate(String expiredEndDate) {
		this.expiredEndDate = expiredEndDate;
	}
	
	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}
	
	
	/**
	 * @return -1 不在过期范围
	 * 1-7  即将过期
	 * 0 今天到期
	 */
	public Integer getLeftDays() {
		if(leftDays == null)
			return -1;
		return leftDays;
	}

	public void setLeftDays(Integer leftDays) {
		if(leftDays == null)
			this.leftDays = -1;
		else
			this.leftDays = leftDays;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((voucherId == null) ? 0 : voucherId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Voucher other = (Voucher) obj;
		if (voucherId == null) {
			if (other.voucherId != null)
				return false;
		} else if (!voucherId.equals(other.voucherId))
			return false;
		return true;
	}
	
	public Bitmap getCouponLogoBitmap(Context context,boolean isNotLoading,String userAccount){
		File cacheFile = null;
		AliPassServiceImpl mAliPassServiceImpl = new AliPassServiceImpl(context);
		if (Mode.ONLINE.equals(mode)) {//网络
			mAppsPath = Helper.getCachePath(context, AliPassServiceImpl.ALIPASSDIR);
			cacheFile = getIconFile(mAppsPath, logoUrl);
			if(cacheFile == null || !cacheFile.exists()){
				getVoucherBitmap(context,true,cacheFile);
			}
		} else if (Mode.OFFLINE.equals(mode)) {
			File passFile = mAliPassServiceImpl.getPassFile(userAccount, getPassFileName());
			cacheFile = new File(passFile.getAbsolutePath() + File.separator + AliPassServiceImpl.PASSLOGO);
		} else if (Mode.PREVIEW.equals(mode)) {
			mAppsPath = Helper.getCachePath(context, AliPassServiceImpl.ALIPASSDIR);
			cacheFile = new File(mAppsPath + getPassFileName() + File.separator + AliPassServiceImpl.PASSLOGO);
		}
		
		return getVoucherBitmap(context,isNotLoading,cacheFile);
	}

	public Bitmap getLogoBitmap(Context context, boolean isNotLoading) {
		mAppsPath = Helper.getCachePath(context, VOUCHERICONDIR);
		File cacheFile = getIconFile(mAppsPath, logoUrl);
		return getVoucherBitmap(context, isNotLoading, cacheFile);
	}

	private Bitmap getVoucherBitmap(Context context, boolean isNotLoading, File cacheFile) {
		Object bitmapUri;
		if (cacheFile != null && cacheFile.exists())
			bitmapUri = cacheFile;
		else if ((cacheFile == null || !cacheFile.exists()) && !isNotLoading)
			bitmapUri = null;
		else
			bitmapUri = logoUrl;

		return Helper.bitmapFromUriString(context, bitmapUri, mBitmapDownloadListener, R.drawable.default_brand_logo);
	}

	public static File getIconFile(String appPath,String logoUrl) {
		String logoKey = Helper.urlToKey(logoUrl);
		if(logoKey != null){
			String logoBitmapPath = appPath + logoKey;
			return new File(logoBitmapPath);
		}
		return null;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(voucherId);
        dest.writeString(voucherName);
        dest.writeString(voucherCode);
        dest.writeString(logoUrl);
        dest.writeString(status);
        dest.writeString(voucherType);
        dest.writeString(actualAmount);
        dest.writeString(amount);
        dest.writeInt(totalTimes== null ? 0 : totalTimes);
        if(leftDays == null)
        	dest.writeInt(-1);
        else
        	dest.writeInt(leftDays);
        dest.writeInt(canUseTimes == null ? 0 : canUseTimes);
        dest.writeString(merchantId);
        dest.writeString(merchantName);
        dest.writeString(voucherFrom);
        dest.writeString(expiredBeginDate);
        dest.writeString(expiredEndDate);
        dest.writeString(outBizNo);
        dest.writeString(mode);
        dest.writeString(passFileName);
        dest.writeString(templateType);
        dest.writeInt(backGroundColor);
    }
	
	public Voucher(Parcel in){
		readFormParcel(in);
	}
	
	public static final Parcelable.Creator<Voucher> CREATOR = new Parcelable.Creator<Voucher>() {
        public Voucher createFromParcel(Parcel in) {
        	return new Voucher(in);
        }

		public Voucher[] newArray(int size) {
            return new Voucher[size];
        }
    };

	private void readFormParcel(Parcel in) {
		voucherId = in.readString();
		voucherName = in.readString();
		voucherCode = in.readString();
		logoUrl = in.readString();
		status = in.readString();
		voucherType = in.readString();
		actualAmount = in.readString();
		amount = in.readString();
		totalTimes = in.readInt();
		leftDays = in.readInt();
		canUseTimes = in.readInt();
		merchantId = in.readString();
		merchantName = in.readString();
		voucherFrom = in.readString();
		expiredBeginDate = in.readString();
		expiredEndDate = in.readString();
		outBizNo = in.readString();
		mode = in.readString();
		passFileName = in.readString();
		templateType = in.readString();
		backGroundColor = in.readInt();
	}
}
